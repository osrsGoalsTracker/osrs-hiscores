package com.osrs_hiscores_fetcher.impl.fetcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.osrs_hiscores_fetcher.api.FetchOptions;
import com.osrs_hiscores_fetcher.api.models.OsrsPlayer;
import com.osrs_hiscores_fetcher.impl.model.HiscoreEntry;
import com.osrs_hiscores_fetcher.impl.model.HiscoreResponse;
import com.osrs_hiscores_fetcher.impl.service.HttpService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class OsrsHiscoresPlayerFetcherImplTest {
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
    void getPlayerByRsn_Success() throws IOException {
        // Prepare test data
        HiscoreResponse mockResponse = new HiscoreResponse();
        HiscoreEntry[] skills = new HiscoreEntry[2];
        HiscoreEntry[] activities = new HiscoreEntry[2];

        // Overall skill entry
        HiscoreEntry overallEntry = new HiscoreEntry();
        overallEntry.setId(0);
        overallEntry.setName("Overall");
        overallEntry.setRank(50);
        overallEntry.setLevel(2277);  // Max total level
        overallEntry.setExperience(200000000);  // High XP to test virtual level handling
        skills[0] = overallEntry;

        // Attack skill entry
        HiscoreEntry attackEntry = new HiscoreEntry();
        attackEntry.setId(1);
        attackEntry.setName("Attack");
        attackEntry.setRank(100);
        attackEntry.setLevel(99);
        attackEntry.setExperience(14391160); // Level 100 XP
        skills[1] = attackEntry;

        // Ranked activity
        HiscoreEntry rankedActivity = new HiscoreEntry();
        rankedActivity.setId(0);
        rankedActivity.setName("Bounty Hunter");
        rankedActivity.setRank(1000);
        rankedActivity.setScore(50);
        activities[0] = rankedActivity;

        // Unranked activity
        HiscoreEntry unrankedActivity = new HiscoreEntry();
        unrankedActivity.setId(1);
        unrankedActivity.setName("LMS");
        unrankedActivity.setRank(-1);
        unrankedActivity.setScore(-1);
        activities[1] = unrankedActivity;

        mockResponse.setSkills(skills);
        mockResponse.setActivities(activities);

        // Mock HTTP response with JSON
        when(httpService.get(anyString()))
            .thenReturn(objectMapper.writeValueAsString(mockResponse));

        // Execute test with regular levels
        FetchOptions regularOptions = FetchOptions.builder()
            .calculateVirtualLevels(false)
            .build();
        OsrsPlayer regularPlayer = fetcher.getPlayerByRsn("TestPlayer", regularOptions);

        // Verify regular level results
        assertNotNull(regularPlayer);
        assertEquals("TestPlayer", regularPlayer.getRsn());
        assertEquals(2, regularPlayer.getSkills().size());
        assertEquals(2, regularPlayer.getActivities().size());

        // Verify Overall skill (should be regular level)
        var regularOverall = regularPlayer.getSkills().get(0);
        assertEquals(0, regularOverall.getId());
        assertEquals("Overall", regularOverall.getName());
        assertEquals(50, regularOverall.getRank());
        assertEquals(2277, regularOverall.getLevel());
        assertEquals(200000000, regularOverall.getXp());

        // Verify Attack skill
        var regularAttack = regularPlayer.getSkills().get(1);
        assertEquals(1, regularAttack.getId());
        assertEquals("Attack", regularAttack.getName());
        assertEquals(100, regularAttack.getRank());
        assertEquals(99, regularAttack.getLevel());
        assertEquals(14391160, regularAttack.getXp());

        // Verify ranked activity
        var rankedActivityResult = regularPlayer.getActivities().get(0);
        assertEquals(0, rankedActivityResult.getId());
        assertEquals("Bounty Hunter", rankedActivityResult.getName());
        assertEquals(1000, rankedActivityResult.getRank());
        assertEquals(50, rankedActivityResult.getScore());

        // Verify unranked activity returns score of 0
        var unrankedActivityResult = regularPlayer.getActivities().get(1);
        assertEquals(1, unrankedActivityResult.getId());
        assertEquals("LMS", unrankedActivityResult.getName());
        assertEquals(-1, unrankedActivityResult.getRank());
        assertEquals(0, unrankedActivityResult.getScore(), "Unranked activity should have score of 0");

        // Execute test with virtual levels
        FetchOptions virtualOptions = FetchOptions.builder()
            .calculateVirtualLevels(true)
            .build();
        OsrsPlayer virtualPlayer = fetcher.getPlayerByRsn("TestPlayer", virtualOptions);

        // Verify Overall skill still uses regular level
        var virtualOverall = virtualPlayer.getSkills().get(0);
        assertEquals(2277, virtualOverall.getLevel(),
            "Overall level should not be affected by virtual level calculation");

        // Verify Attack skill uses virtual level
        var virtualAttack = virtualPlayer.getSkills().get(1);
        assertEquals(100, virtualAttack.getLevel(),
            "Attack should show virtual level 100 for XP of 14,391,160");
    }

    @Test
    void getPlayerByRsn_HttpError() throws IOException {
        // Mock HTTP error
        when(httpService.get(anyString()))
            .thenThrow(new IOException("HTTP Error"));

        // Execute test and verify exception
        FetchOptions options = FetchOptions.builder().build();
        assertThrows(IOException.class, () ->
            fetcher.getPlayerByRsn("TestPlayer", options)
        );
    }
} 