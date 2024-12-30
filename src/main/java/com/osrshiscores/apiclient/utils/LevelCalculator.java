package com.osrshiscores.apiclient.utils;

/**
 * Utility class for calculating OSRS skill levels based on experience points.
 * This class handles both regular and virtual level calculations.
 */
public final class LevelCalculator {
    private static final int MAX_REGULAR_LEVEL = 99;
    private static final int MAX_VIRTUAL_LEVEL = 126;
    private static final int[] XP_TABLE = new int[MAX_VIRTUAL_LEVEL + 1];
    private static final int XP_MULTIPLIER = 300;
    private static final double XP_POWER_BASE = 2.0;
    private static final double XP_POWER_DIVISOR = 7.0;
    private static final int XP_POINTS_DIVISOR = 4;
    private static final int MIN_LEVEL = 1;

    static {
        // Initialize XP table using OSRS formula
        for (int level = MIN_LEVEL; level <= MAX_VIRTUAL_LEVEL; level++) {
            double points = 0;
            for (int i = MIN_LEVEL; i < level; i++) {
                points += Math.floor(i + XP_MULTIPLIER * Math.pow(XP_POWER_BASE, i / XP_POWER_DIVISOR));
            }
            XP_TABLE[level] = (int) Math.floor(points / XP_POINTS_DIVISOR);
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
        for (int level = maxLevel; level >= MIN_LEVEL; level--) {
            if (xp >= XP_TABLE[level]) {
                return level;
            }
        }
        
        return MIN_LEVEL;
    }
}
