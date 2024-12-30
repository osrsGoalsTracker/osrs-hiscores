package com.osrs_hiscores_fetcher.api;

import lombok.Builder;
import lombok.Data;

/**
 * Options for fetching player data from the OSRS hiscores.
 * This class provides configuration options for how the data should be fetched and processed.
 */
@Data
@Builder
public class FetchOptions {
    /**
     * Whether to calculate virtual levels above 99 based on experience points.
     * If true, skills can show levels above 99 if the player has enough experience.
     * If false, all skills are capped at level 99 regardless of experience.
     */
    private final boolean calculateVirtualLevels;

    /**
     * Creates default fetch options.
     * By default, virtual levels are disabled.
     *
     * @return Default FetchOptions instance
     */
    public static FetchOptions defaults() {
        return FetchOptions.builder()
                .calculateVirtualLevels(false)
                .build();
    }
} 
