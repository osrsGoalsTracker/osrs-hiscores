package com.osrs_hiscores_fetcher.impl.fetcher;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.osrs_hiscores_fetcher.api.FetchOptions;
import com.osrs_hiscores_fetcher.api.models.OsrsPlayer;
import com.osrs_hiscores_fetcher.impl.model.HiscoreEntry;
import com.osrs_hiscores_fetcher.impl.model.HiscoreResponse;
import com.osrs_hiscores_fetcher.impl.service.HttpService;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class OsrsHiscoresPlayerFetcherImplTest {
    private static final int OVERALL_SKILL_ID = 0;
    private static final int ATTACK_SKILL_ID = 1;
    private static final int OVERALL_RANK = 50;
    private static final int MAX_TOTAL_LEVEL = 2277;
    private static final long MAX_EXPERIENCE = 200_000_000L;
    private static final int ATTACK_RANK = 100;
    private static final int MAX_REGULAR_LEVEL = 99;
    private static final long LEVEL_100_EXPERIENCE = 14_391_160L;
    private static final int BOUNTY_HUNTER_RANK = 1000;
    private static final int BOUNTY_HUNTER_SCORE = 50;
    private static final int UNRANKED_VALUE = -1;
    private static final int VIRTUAL_LEVEL_100 = 100;
    private static final int DEFAULT_SCORE = 0;

    @Mock
    private HttpService httpService;
    private OsrsHiscoresPlayerFetcherImpl fetcher;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        fetcher = new OsrsHiscoresPlayerFetcherImpl(httpService);
    }

    @Test
    void testSuccessfulPlayerFetch() throws IOException {
        HiscoreResponse mockResponse = createMockResponse();
        when(httpService.get(anyString()))
            .thenReturn(objectMapper.writeValueAsString(mockResponse));

        verifyRegularLevelFetch();
        verifyVirtualLevelFetch();
    }

    private HiscoreResponse createMockResponse() {
        HiscoreResponse mockResponse = new HiscoreResponse();
        mockResponse.setSkills(createSkillEntries());
        mockResponse.setActivities(createActivityEntries());
        return mockResponse;
    }

    private HiscoreEntry[] createSkillEntries() {
        HiscoreEntry[] skills = new HiscoreEntry[2];
        skills[0] = createOverallSkillEntry();
        skills[1] = createAttackSkillEntry();
        return skills;
    }

    private HiscoreEntry createOverallSkillEntry() {
        HiscoreEntry overallEntry = new HiscoreEntry();
        overallEntry.setId(OVERALL_SKILL_ID);
        overallEntry.setName("Overall");
        overallEntry.setRank(OVERALL_RANK);
        overallEntry.setLevel(MAX_TOTAL_LEVEL);
        overallEntry.setExperience(MAX_EXPERIENCE);
        return overallEntry;
    }

    private HiscoreEntry createAttackSkillEntry() {
        HiscoreEntry attackEntry = new HiscoreEntry();
        attackEntry.setId(ATTACK_SKILL_ID);
        attackEntry.setName("Attack");
        attackEntry.setRank(ATTACK_RANK);
        attackEntry.setLevel(MAX_REGULAR_LEVEL);
        attackEntry.setExperience(LEVEL_100_EXPERIENCE);
        return attackEntry;
    }

    private HiscoreEntry[] createActivityEntries() {
        HiscoreEntry[] activities = new HiscoreEntry[2];
        activities[0] = createRankedActivityEntry();
        activities[1] = createUnrankedActivityEntry();
        return activities;
    }

    private HiscoreEntry createRankedActivityEntry() {
        HiscoreEntry rankedActivity = new HiscoreEntry();
        rankedActivity.setId(OVERALL_SKILL_ID);
        rankedActivity.setName("Bounty Hunter");
        rankedActivity.setRank(BOUNTY_HUNTER_RANK);
        rankedActivity.setScore(BOUNTY_HUNTER_SCORE);
        return rankedActivity;
    }

    private HiscoreEntry createUnrankedActivityEntry() {
        HiscoreEntry unrankedActivity = new HiscoreEntry();
        unrankedActivity.setId(ATTACK_SKILL_ID);
        unrankedActivity.setName("LMS");
        unrankedActivity.setRank(UNRANKED_VALUE);
        unrankedActivity.setScore(UNRANKED_VALUE);
        return unrankedActivity;
    }

    private void verifyRegularLevelFetch() throws IOException {
        FetchOptions regularOptions = FetchOptions.builder()
            .calculateVirtualLevels(false)
            .build();
        OsrsPlayer regularPlayer = fetcher.getPlayerByRsn("TestPlayer", regularOptions);

        verifyBasicPlayerData(regularPlayer);
        verifyRegularSkillLevels(regularPlayer);
        verifyActivityData(regularPlayer);
    }

    private void verifyBasicPlayerData(OsrsPlayer player) {
        Assertions.assertNotNull(player);
        Assertions.assertEquals("TestPlayer", player.getRsn());
        Assertions.assertEquals(2, player.getSkills().size());
        Assertions.assertEquals(2, player.getActivities().size());
    }

    private void verifyRegularSkillLevels(OsrsPlayer player) {
        var regularOverall = player.getSkills().get(0);
        Assertions.assertEquals(OVERALL_SKILL_ID, regularOverall.getId());
        Assertions.assertEquals("Overall", regularOverall.getName());
        Assertions.assertEquals(OVERALL_RANK, regularOverall.getRank());
        Assertions.assertEquals(MAX_TOTAL_LEVEL, regularOverall.getLevel());
        Assertions.assertEquals(MAX_EXPERIENCE, regularOverall.getXp());

        var regularAttack = player.getSkills().get(1);
        Assertions.assertEquals(ATTACK_SKILL_ID, regularAttack.getId());
        Assertions.assertEquals("Attack", regularAttack.getName());
        Assertions.assertEquals(ATTACK_RANK, regularAttack.getRank());
        Assertions.assertEquals(MAX_REGULAR_LEVEL, regularAttack.getLevel());
        Assertions.assertEquals(LEVEL_100_EXPERIENCE, regularAttack.getXp());
    }

    private void verifyActivityData(OsrsPlayer player) {
        var rankedActivityResult = player.getActivities().get(0);
        Assertions.assertEquals(OVERALL_SKILL_ID, rankedActivityResult.getId());
        Assertions.assertEquals("Bounty Hunter", rankedActivityResult.getName());
        Assertions.assertEquals(BOUNTY_HUNTER_RANK, rankedActivityResult.getRank());
        Assertions.assertEquals(BOUNTY_HUNTER_SCORE, rankedActivityResult.getScore());

        var unrankedActivityResult = player.getActivities().get(1);
        Assertions.assertEquals(ATTACK_SKILL_ID, unrankedActivityResult.getId());
        Assertions.assertEquals("LMS", unrankedActivityResult.getName());
        Assertions.assertEquals(UNRANKED_VALUE, unrankedActivityResult.getRank());
        Assertions.assertEquals(DEFAULT_SCORE, unrankedActivityResult.getScore());
    }

    private void verifyVirtualLevelFetch() throws IOException {
        FetchOptions virtualOptions = FetchOptions.builder()
            .calculateVirtualLevels(true)
            .build();
        OsrsPlayer virtualPlayer = fetcher.getPlayerByRsn("TestPlayer", virtualOptions);

        var virtualOverall = virtualPlayer.getSkills().get(0);
        Assertions.assertEquals(MAX_TOTAL_LEVEL, virtualOverall.getLevel());

        var virtualAttack = virtualPlayer.getSkills().get(1);
        Assertions.assertEquals(VIRTUAL_LEVEL_100, virtualAttack.getLevel());
    }

    @Test
    void testHttpError() throws IOException {
        when(httpService.get(anyString()))
            .thenThrow(new IOException("HTTP Error"));

        FetchOptions options = FetchOptions.builder().build();
        Assertions.assertThrows(IOException.class, () ->
            fetcher.getPlayerByRsn("TestPlayer", options)
        );
    }
} 
