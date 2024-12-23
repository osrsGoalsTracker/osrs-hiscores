package com.osrs_hiscores_fetcher.impl.example;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.osrs_hiscores_fetcher.api.FetchOptions;
import com.osrs_hiscores_fetcher.api.OsrsHiscoresPlayerFetcher;
import com.osrs_hiscores_fetcher.api.models.Activity;
import com.osrs_hiscores_fetcher.api.models.OsrsPlayer;
import com.osrs_hiscores_fetcher.api.models.Skill;
import com.osrs_hiscores_fetcher.impl.config.OsrsHiscoresModule;

/**
 * Example application demonstrating the usage of the OSRS Hiscores API implementation.
 * This class shows how to fetch and display player statistics including skills and activities.
 * 
 * <p>The example demonstrates:
 * <ul>
 *   <li>Setting up Guice dependency injection</li>
 *   <li>Fetching player data using the API</li>
 *   <li>Handling player names with spaces</li>
 *   <li>Processing and displaying skills data</li>
 *   <li>Processing and displaying activities/boss data</li>
 *   <li>Proper error handling</li>
 *   <li>Virtual level calculation</li>
 * </ul>
 * 
 * <p>Run this example using Gradle:
 * <pre>{@code
 * ./gradlew run --args="player_name [--virtual]"
 * }</pre>
 * 
 * <p>The RSN (RuneScape Name) can contain spaces, underscores, or dashes.
 * Add --virtual flag to show virtual levels above 99.
 * Examples:
 * <ul>
 *   <li>{@code ./gradlew run --args="Zezima"}</li>
 *   <li>{@code ./gradlew run --args="eow btw --virtual"}</li>
 *   <li>{@code ./gradlew run --args="player_name"}</li>
 * </ul>
 */
public class Example {
    /**
     * Creates a new Example instance.
     * Private constructor to prevent instantiation as this is a utility class with only static methods.
     * This follows the utility class pattern where all methods are static.
     */
    private Example() {
        // Utility class, no instantiation needed
    }

    /**
     * Main method that demonstrates fetching and displaying player statistics.
     * This method shows a complete workflow of fetching and displaying OSRS player data.
     * 
     * <p>The method performs the following steps:
     * <ol>
     *   <li>Validates command line arguments</li>
     *   <li>Sets up Guice dependency injection</li>
     *   <li>Fetches player data from the OSRS Hiscores</li>
     *   <li>Displays all skills with their levels, ranks, and XP</li>
     *   <li>Displays all activities with their scores and ranks</li>
     * </ol>
     *
     * @param args Command line arguments. First argument should be the player's RSN.
     *             Multiple words are joined with spaces to support RSNs containing spaces.
     *             Optional --virtual flag to show virtual levels above 99.
     */
    public static void main(String[] args) {
        // Validate command line arguments
        if (args.length < 1) {
            System.err.println("Please provide a player RSN as a command line argument.");
            System.err.println("Example: ./gradlew run --args=\"SoloMission\"");
            System.err.println("Add --virtual flag to show virtual levels:");
            System.err.println("./gradlew run --args=\"SoloMission --virtual\"");
            System.exit(1);
        }

        // Check for virtual flag and build RSN
        boolean virtualLevels = false;
        StringBuilder rsnBuilder = new StringBuilder();

        for (String arg : args) {
            if ("--virtual".equals(arg)) {
                virtualLevels = true;
            } else {
                if (rsnBuilder.length() > 0) {
                    rsnBuilder.append(" ");
                }
                rsnBuilder.append(arg);
            }
        }

        String rsn = rsnBuilder.toString();

        try {
            // Initialize Guice with our module for dependency injection
            Injector injector = Guice.createInjector(new OsrsHiscoresModule());

            // Get the fetcher instance - this is thread-safe and can be reused
            OsrsHiscoresPlayerFetcher fetcher = injector.getInstance(OsrsHiscoresPlayerFetcher.class);

            // Fetch player data with specified level calculation option
            OsrsPlayer player = fetcher.getPlayerByRsn(rsn, FetchOptions.builder()
                .calculateVirtualLevels(virtualLevels)
                .build());

            // Display player information
            System.out.println("Player: " + player.getRsn());
            System.out.println("Level Type: " + (virtualLevels ? "Virtual" : "Regular"));

            // Display all skills (24 total skills)
            System.out.println("\nSkills:");
            for (Skill skill : player.getSkills()) {
                System.out.printf("%s: Level %d (Rank %d, XP %d)%n",
                    skill.getName(),
                    skill.getLevel(),
                    skill.getRank(),
                    skill.getXp()
                );
            }
            
            // Display all activities (including boss kills, minigames, etc.)
            System.out.println("\nActivities:");
            for (Activity activity : player.getActivities()) {
                // A rank of -1 indicates the player is unranked in this activity
                System.out.printf("%s: Score %d (Rank %d)%n",
                    activity.getName(),
                    activity.getScore(),
                    activity.getRank()
                );
            }
        } catch (Exception e) {
            // Handle any errors (network issues, player not found, etc.)
            System.err.println("Error fetching player data: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 