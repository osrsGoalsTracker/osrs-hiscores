package com.osrshiscores.apiclient.model.output;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Value;

/**
 * Represents a player's skill in OSRS.
 * This class contains information about a player's level, experience, and rank in a particular skill.
 */
@Value
public class Skill {
    /**
     * The ID of the skill.
     */
    private final int id;

    /**
     * The name of the skill.
     */
    private final String name;

    /**
     * The player's rank in this skill.
     */
    private final int rank;

    /**
     * The player's level in this skill.
     */
    private final int level;

    /**
     * The player's experience points in this skill.
     */
    private final long xp;

    /**
     * Creates a new Skill instance.
     *
     * @param id The ID of the skill
     * @param name The name of the skill
     * @param rank The player's rank in this skill
     * @param level The player's level in this skill
     * @param xp The player's experience points in this skill
     */
    @JsonCreator
    public Skill(
            @JsonProperty("id") int id,
            @JsonProperty("name") String name,
            @JsonProperty("rank") int rank,
            @JsonProperty("level") int level,
            @JsonProperty("xp") long xp) {
        this.id = id;
        this.name = name;
        this.rank = rank;
        this.level = level;
        this.xp = xp;
    }
}
