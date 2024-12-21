package com.osrs_hiscores_fetcher.impl.utils;

/**
 * Utility class for calculating OSRS skill levels based on experience points.
 * This class handles both regular and virtual level calculations.
 */
public final class LevelCalculator {
    private static final int MAX_REGULAR_LEVEL = 99;
    private static final int MAX_VIRTUAL_LEVEL = 126;
    private static final int[] XP_TABLE = new int[MAX_VIRTUAL_LEVEL + 1];

    static {
        // Initialize XP table using OSRS formula
        for (int level = 1; level <= MAX_VIRTUAL_LEVEL; level++) {
            double points = 0;
            for (int i = 1; i < level; i++) {
                points += Math.floor(i + 300 * Math.pow(2, i / 7.0));
            }
            XP_TABLE[level] = (int) Math.floor(points / 4);
        }
    }

    /**
     * Private constructor to prevent instantiation.
     * This is a utility class with only static methods.
     */
    private LevelCalculator() {
        // Utility class, no instantiation needed
    }

    /**
     * Calculates the skill level based on experience points.
     * If calculateVirtualLevels is true, returns the actual level even above 99.
     * If false, caps the level at 99.
     *
     * @param xp The experience points
     * @param calculateVirtualLevels Whether to calculate levels above 99
     * @return The skill level, capped at 99 if calculateVirtualLevels is false
     */
    public static int calculateLevel(long xp, boolean calculateVirtualLevels) {
        int maxLevel = calculateVirtualLevels ? MAX_VIRTUAL_LEVEL : MAX_REGULAR_LEVEL;
        
        // Find the highest level where the XP requirement is met
        for (int level = maxLevel; level >= 1; level--) {
            if (xp >= XP_TABLE[level]) {
                return level;
            }
        }
        
        return 1; // Default to level 1 if XP is too low
    }
} 