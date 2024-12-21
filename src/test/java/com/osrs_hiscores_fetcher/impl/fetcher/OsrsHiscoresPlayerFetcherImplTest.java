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
        HiscoreEntry[] skills = new HiscoreEntry[1];
        HiscoreEntry[] activities = new HiscoreEntry[1];

        HiscoreEntry skillEntry = new HiscoreEntry();
        skillEntry.setId(0);
        skillEntry.setName("Attack");
        skillEntry.setRank(100);
        skillEntry.setLevel(99);
        skillEntry.setExperience(13034431);
        skills[0] = skillEntry;

        HiscoreEntry activityEntry = new HiscoreEntry();
        activityEntry.setId(0);
        activityEntry.setName("Bounty Hunter");
        activityEntry.setRank(1000);
        activityEntry.setScore(50);
        activities[0] = activityEntry;

        mockResponse.setSkills(skills);
        mockResponse.setActivities(activities);

        // Mock HTTP response with JSON
        when(httpService.get(anyString()))
            .thenReturn(objectMapper.writeValueAsString(mockResponse));

        // Execute test
        FetchOptions options = FetchOptions.builder().build();
        OsrsPlayer player = fetcher.getPlayerByRsn("TestPlayer", options);

        // Verify results
        assertNotNull(player);
        assertEquals("TestPlayer", player.getRsn());
        assertEquals(1, player.getSkills().size());
        assertEquals(1, player.getActivities().size());

        var skill = player.getSkills().get(0);
        assertEquals(0, skill.getId());
        assertEquals("Attack", skill.getName());
        assertEquals(100, skill.getRank());
        assertEquals(99, skill.getLevel());
        assertEquals(13034431, skill.getXp());

        var activity = player.getActivities().get(0);
        assertEquals(0, activity.getId());
        assertEquals("Bounty Hunter", activity.getName());
        assertEquals(1000, activity.getRank());
        assertEquals(50, activity.getScore());
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