package com.osrs_hiscores_fetcher.impl.example;

// Third-party imports
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
public final class Example {
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
     * @param args Command line arguments. First argument should be the player's RSN.
     *             Multiple words are joined with spaces to support RSNs containing spaces.
     *             Optional --virtual flag to show virtual levels above 99.
     */
    public static void main(String[] args) {
        if (!validateArgs(args)) {
            return;
        }

        PlayerNameOptions options = parsePlayerNameOptions(args);
        fetchAndDisplayPlayerData(options.rsn(), options.virtualLevels());
    }

    /**
     * Validates the command line arguments.
     *
     * @param args Command line arguments to validate
     * @return true if arguments are valid, false otherwise
     */
    private static boolean validateArgs(String[] args) {
        if (args.length >= 1) {
            return true;
        }

        System.err.println("Please provide a player RSN as a command line argument.");
        System.err.println("Example: ./gradlew run --args=\"SoloMission\"");
        System.err.println("Add --virtual flag to show virtual levels:");
        System.err.println("./gradlew run --args=\"SoloMission --virtual\"");
        return false;
    }

    /**
     * Parses command line arguments into player name options.
     *
     * @param args Command line arguments to parse
     * @return PlayerNameOptions containing the RSN and virtual levels flag
     */
    private static PlayerNameOptions parsePlayerNameOptions(String[] args) {
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

        return new PlayerNameOptions(rsnBuilder.toString(), virtualLevels);
    }

    /**
     * Fetches and displays player data using the OSRS Hiscores API.
     *
     * @param rsn Player's RuneScape name
     * @param virtualLevels Whether to show virtual levels above 99
     */
    private static void fetchAndDisplayPlayerData(String rsn, boolean virtualLevels) {
        try {
            Injector injector = Guice.createInjector(new OsrsHiscoresModule());
            OsrsHiscoresPlayerFetcher fetcher = injector.getInstance(OsrsHiscoresPlayerFetcher.class);

            OsrsPlayer player = fetcher.getPlayerByRsn(rsn, FetchOptions.builder()
                .calculateVirtualLevels(virtualLevels)
                .build());

            displayPlayerInformation(player, virtualLevels);
        } catch (Exception e) {
            System.err.println("Error fetching player data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Displays player information including skills and activities.
     *
     * @param player Player data to display
     * @param virtualLevels Whether virtual levels are being displayed
     */
    private static void displayPlayerInformation(OsrsPlayer player, boolean virtualLevels) {
        System.out.println("Player: " + player.getRsn());
        System.out.println("Level Type: " + (virtualLevels ? "Virtual" : "Regular"));

        displaySkills(player);
        displayActivities(player);
    }

    /**
     * Displays all skills for a player.
     *
     * @param player Player whose skills to display
     */
    private static void displaySkills(OsrsPlayer player) {
        System.out.println("\nSkills:");
        for (Skill skill : player.getSkills()) {
            System.out.printf("%s: Level %d (Rank %d, XP %d)%n",
                skill.getName(),
                skill.getLevel(),
                skill.getRank(),
                skill.getXp()
            );
        }
    }

    /**
     * Displays all activities for a player.
     *
     * @param player Player whose activities to display
     */
    private static void displayActivities(OsrsPlayer player) {
        System.out.println("\nActivities:");
        for (Activity activity : player.getActivities()) {
            System.out.printf("%s: Score %d (Rank %d)%n",
                activity.getName(),
                activity.getScore(),
                activity.getRank()
            );
        }
    }

    /**
     * Record to hold player name options parsed from command line arguments.
     *
     * @param rsn Player's RuneScape name
     * @param virtualLevels Whether to show virtual levels
     */
    private record PlayerNameOptions(String rsn, boolean virtualLevels) { }
}
