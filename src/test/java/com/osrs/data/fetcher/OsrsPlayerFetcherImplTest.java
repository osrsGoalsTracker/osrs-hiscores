package com.osrs.data.fetcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.osrs.FetchOptions;
import com.osrs.data.model.HiscoreEntry;
import com.osrs.data.model.HiscoreResponse;
import com.osrs.data.service.HttpService;
import com.osrs.models.OsrsPlayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class OsrsPlayerFetcherImplTest {
    @Mock
    private HttpService httpService;
    private OsrsPlayerFetcherImpl fetcher;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        fetcher = new OsrsPlayerFetcherImpl(httpService);
    }

    @Test
    void fetch_ValidUsername_ReturnsOsrsPlayer() throws IOException {
        // Arrange
        String username = "testUser";
        HiscoreResponse mockResponse = new HiscoreResponse();
        HiscoreEntry[] skills = new HiscoreEntry[1];
        skills[0] = new HiscoreEntry();
        skills[0].setName("Attack");
        skills[0].setRank(100);
        skills[0].setLevel(99);
        skills[0].setExperience(13034431);
        mockResponse.setSkills(skills);

        String jsonResponse = objectMapper.writeValueAsString(mockResponse);
        when(httpService.get(anyString())).thenReturn(jsonResponse);

        // Act
        OsrsPlayer result = fetcher.getPlayerByRsn(username, FetchOptions.builder().build());

        // Assert
        assertNotNull(result);
        assertEquals(username, result.getRsn());
        assertFalse(result.getSkills().isEmpty());
        assertEquals("Attack", result.getSkills().get(0).getName());
        assertEquals(99, result.getSkills().get(0).getLevel());
    }
} 