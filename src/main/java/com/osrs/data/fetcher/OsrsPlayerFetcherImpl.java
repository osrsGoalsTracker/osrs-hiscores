package com.osrs.impl.fetcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.osrs.FetchOptions;
import com.osrs.OsrsPlayerFetcher;
import com.osrs.impl.model.HiscoreResponse;
import com.osrs.impl.service.HttpService;
import com.osrs.models.Activity;
import com.osrs.models.OsrsPlayer;
import com.osrs.models.Skill;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class OsrsPlayerFetcherImpl implements OsrsPlayerFetcher {
    private static final String HISCORE_API_URL = "https://secure.runescape.com/m=hiscore_oldschool/index_lite.json?player=";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    private final HttpService httpService;

    @Inject
    public OsrsPlayerFetcherImpl(HttpService httpService) {
        this.httpService = httpService;
    }

    @Override
    public OsrsPlayer getPlayerByRsn(String username, FetchOptions options) throws IOException {
        String encodedUsername = URLEncoder.encode(username, StandardCharsets.UTF_8);
        String response = httpService.get(HISCORE_API_URL + encodedUsername);
        HiscoreResponse hiscoreResponse = objectMapper.readValue(response, HiscoreResponse.class);

        List<Skill> skills = new ArrayList<>();
        List<Activity> activities = new ArrayList<>();

        if (hiscoreResponse.getSkills() != null) {
            for (var entry : hiscoreResponse.getSkills()) {
                skills.add(new Skill(
                    entry.getId(),
                    entry.getName(),
                    entry.getRank(),
                    entry.getLevel(),
                    entry.getExperience()
                ));
            }
        }

        if (hiscoreResponse.getActivities() != null) {
            for (var entry : hiscoreResponse.getActivities()) {
                activities.add(new Activity(
                    entry.getId(),
                    entry.getName(),
                    entry.getRank(),
                    entry.getScore()
                ));
            }
        }

        return new OsrsPlayer(username, skills, activities);
    }
} 