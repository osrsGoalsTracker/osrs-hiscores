package com.osrs.impl.example;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.osrs.FetchOptions;
import com.osrs.OsrsPlayerFetcher;
import com.osrs.impl.config.DataModule;
import com.osrs.models.OsrsPlayer;
import com.osrs.models.Skill;
import com.osrs.models.Activity;

public class Example {
    public static void main(String[] args) {
        try {
            // Initialize Guice
            Injector injector = Guice.createInjector(new DataModule());
            OsrsPlayerFetcher fetcher = injector.getInstance(OsrsPlayerFetcher.class);

            // Fetch player data
            FetchOptions options = FetchOptions.builder().build();
            OsrsPlayer player = fetcher.getPlayerByRsn("SoloMission", options);

            // Print player information
            System.out.println("Player: " + player.getRsn());
            
            // Print skills
            System.out.println("\nSkills:");
            for (Skill skill : player.getSkills()) {
                System.out.printf("%-12s Level: %-3d Rank: %-8d XP: %d%n",
                    skill.getName(),
                    skill.getLevel(),
                    skill.getRank(),
                    skill.getXp()
                );
            }

            // Print activities
            System.out.println("\nActivities:");
            for (Activity activity : player.getActivities()) {
                System.out.printf("%-20s Rank: %-8d Score: %d%n",
                    activity.getName(),
                    activity.getRank(),
                    activity.getScore()
                );
            }

        } catch (Exception e) {
            System.err.println("Error fetching player data: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 