package com.osrs_hiscores_fetcher.impl.fetcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.osrs_hiscores_fetcher.api.FetchOptions;
import com.osrs_hiscores_fetcher.api.OsrsHiscoresPlayerFetcher;
import com.osrs_hiscores_fetcher.api.models.Activity;
import com.osrs_hiscores_fetcher.api.models.OsrsPlayer;
import com.osrs_hiscores_fetcher.api.models.Skill;
import com.osrs_hiscores_fetcher.impl.model.HiscoreResponse;
import com.osrs_hiscores_fetcher.impl.service.HttpService;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the OsrsHiscoresPlayerFetcher interface.
 * This class fetches player statistics from the OSRS hiscores API and converts them into the appropriate model objects.
 */
@Singleton
public class OsrsHiscoresPlayerFetcherImpl implements OsrsHiscoresPlayerFetcher {
    private static final String HISCORE_API_URL = "https://secure.runescape.com/m=hiscore_oldschool/index_lite.json?player=";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    private final HttpService httpService;

    /**
     * Creates a new OsrsHiscoresPlayerFetcherImpl with the specified HttpService.
     *
     * @param httpService The HTTP service to use for making requests to the OSRS hiscores API
     */
    @Inject
    public OsrsHiscoresPlayerFetcherImpl(HttpService httpService) {
        this.httpService = httpService;
    }

    /**
     * {@inheritDoc}
     * This implementation fetches the player's data from the OSRS hiscores API,
     * parses the JSON response, and converts it into an OsrsPlayer object.
     */
    @Override
    public OsrsPlayer getPlayerByRsn(String username, FetchOptions options) throws IOException {
        String encodedUsername = URLEncoder.encode(username, StandardCharsets.UTF_8);
        String response = httpService.get(HISCORE_API_URL + encodedUsername);
        
        HiscoreResponse hiscoreResponse = objectMapper.readValue(response, HiscoreResponse.class);
        
        List<Skill> skills = new ArrayList<>();
        List<Activity> activities = new ArrayList<>();
        
        for (var entry : hiscoreResponse.getSkills()) {
            skills.add(new Skill(entry.getId(), entry.getName(), entry.getRank(), entry.getLevel(), entry.getExperience()));
        }
        
        for (var entry : hiscoreResponse.getActivities()) {
            activities.add(new Activity(entry.getId(), entry.getName(), entry.getRank(), entry.getScore()));
        }
        
        return new OsrsPlayer(username, skills, activities);
    }
} 