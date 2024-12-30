package com.osrshiscores.apiclient.model.output;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Value;

/**
 * Represents a player in OSRS.
 * This class contains information about a player's skills and activities.
 */
@Value
public class OsrsPlayer {
    /**
     * The player's RuneScape name.
     */
    private final String rsn;

    /**
     * The player's skills.
     */
    private final List<Skill> skills;

    /**
     * The player's activities.
     */
    private final List<Activity> activities;

    /**
     * Creates a new OsrsPlayer instance.
     *
     * @param rsn The player's RuneScape name
     * @param skills The player's skills
     * @param activities The player's activities
     */
    @JsonCreator
    public OsrsPlayer(
            @JsonProperty("rsn") String rsn,
            @JsonProperty("skills") List<Skill> skills,
            @JsonProperty("activities") List<Activity> activities) {
        this.rsn = rsn;
        this.skills = Collections.unmodifiableList(new ArrayList<>(skills));
        this.activities = Collections.unmodifiableList(new ArrayList<>(activities));
    }
} 