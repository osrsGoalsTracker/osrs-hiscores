package com.osrs_hiscores_fetcher.impl.model;

import lombok.Data;

/**
 * Represents the complete response from the OSRS hiscores API.
 * Contains arrays of skills and activities for a player.
 * This class is used internally to parse the JSON response.
 */
@Data
public class HiscoreResponse {
    /** Array of skill entries containing the player's skill statistics. */
    private HiscoreEntry[] skills;
    
    /** Array of activity entries containing the player's activity statistics. */
    private HiscoreEntry[] activities;

    /**
     * Creates a new HiscoreResponse instance.
     * This constructor is used by Jackson for JSON deserialization.
     */
    public HiscoreResponse() {
        // Default constructor for Jackson
    }
}
