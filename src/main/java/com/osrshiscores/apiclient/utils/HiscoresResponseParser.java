package com.osrshiscores.apiclient.utils;

import java.util.ArrayList;
import java.util.List;

import com.osrshiscores.apiclient.model.input.FetchOptions;
import com.osrshiscores.apiclient.model.output.Activity;
import com.osrshiscores.apiclient.model.output.OsrsPlayer;
import com.osrshiscores.apiclient.model.output.Skill;

/**
 * Parser for the OSRS hiscores response.
 * This class handles parsing the CSV-like response from the OSRS hiscores API.
 */
public final class HiscoresResponseParser {
    private static final int SKILL_COUNT = 24;
    private static final int ACTIVITY_COUNT = 40;
    private static final int OVERALL_SKILL_ID = 0;
    private static final String OVERALL_SKILL_NAME = "Overall";
    private static final int UNRANKED_VALUE = -1;
    private static final int DEFAULT_SCORE = 0;

    private static final String[] SKILL_NAMES = {
        "Overall", "Attack", "Defence", "Strength", "Hitpoints", "Ranged",
        "Prayer", "Magic", "Cooking", "Woodcutting", "Fletching", "Fishing",
        "Firemaking", "Crafting", "Smithing", "Mining", "Herblore", "Agility",
        "Thieving", "Slayer", "Farming", "Runecrafting", "Hunter", "Construction",
    };

    private static final String[] ACTIVITY_NAMES = {
        "League Points", "Bounty Hunter - Hunter", "Bounty Hunter - Rogue",
        "Clue Scrolls (all)", "Clue Scrolls (beginner)", "Clue Scrolls (easy)",
        "Clue Scrolls (medium)", "Clue Scrolls (hard)", "Clue Scrolls (elite)",
        "Clue Scrolls (master)", "LMS - Rank", "PvP Arena - Rank",
        "Soul Wars Zeal", "Rifts closed", "Abyssal Sire", "Alchemical Hydra",
        "Artio", "Barrows Chests", "Bryophyta", "Callisto", "Calvarion",
        "Cerberus", "Chambers of Xeric", "Chambers of Xeric: Challenge Mode",
        "Chaos Elemental", "Chaos Fanatic", "Commander Zilyana",
        "Corporeal Beast", "Crazy Archaeologist", "Dagannoth Prime",
        "Dagannoth Rex", "Dagannoth Supreme", "Deranged Archaeologist",
        "Duke Sucellus", "General Graardor", "Giant Mole", "Grotesque Guardians",
        "Hespori", "Kalphite Queen", "King Black Dragon",
    };

    private HiscoresResponseParser() {
        // Utility class, no instantiation needed
    }

    /**
     * Parses the OSRS hiscores response into an OsrsPlayer object.
     *
     * @param rsn Player's RuneScape name
     * @param response Raw response from the OSRS hiscores API
     * @param options Fetch options for processing the response
     * @return OsrsPlayer object containing the parsed data
     */
    public static OsrsPlayer parse(String rsn, String response, FetchOptions options) {
        String[] lines = response.split("\n");
        List<Skill> skills = parseSkills(lines, options);
        List<Activity> activities = parseActivities(lines);
        return new OsrsPlayer(rsn, skills, activities);
    }

    private static List<Skill> parseSkills(String[] lines, FetchOptions options) {
        List<Skill> skills = new ArrayList<>(SKILL_COUNT);
        for (int i = 0; i < SKILL_COUNT; i++) {
            String[] parts = lines[i].split(",");
            int rank = Integer.parseInt(parts[0]);
            int level = Integer.parseInt(parts[1]);
            long xp = Long.parseLong(parts[2]);

            // Overall skill should always use regular levels
            boolean shouldCalculateVirtual = options.isCalculateVirtualLevels()
                && i != OVERALL_SKILL_ID;

            if (shouldCalculateVirtual) {
                level = LevelCalculator.calculateLevel(xp, true);
            }

            skills.add(new Skill(i, SKILL_NAMES[i], rank, level, xp));
        }
        return skills;
    }

    private static List<Activity> parseActivities(String[] lines) {
        List<Activity> activities = new ArrayList<>(ACTIVITY_COUNT);
        for (int i = 0; i < ACTIVITY_COUNT; i++) {
            String[] parts = lines[i + SKILL_COUNT].split(",");
            int rank = Integer.parseInt(parts[0]);
            int score = Integer.parseInt(parts[1]);

            // If rank is -1 (unranked), set score to 0
            if (rank == UNRANKED_VALUE) {
                score = DEFAULT_SCORE;
            }

            activities.add(new Activity(i, ACTIVITY_NAMES[i], rank, score));
        }
        return activities;
    }
} 