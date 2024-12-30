package com.osrshiscores.apiclient.example;

import java.io.IOException;

import com.osrshiscores.apiclient.OsrsApiClient;
import com.osrshiscores.apiclient.model.input.FetchOptions;
import com.osrshiscores.apiclient.model.output.Activity;
import com.osrshiscores.apiclient.model.output.OsrsPlayer;
import com.osrshiscores.apiclient.model.output.Skill;

/**
 * Example application demonstrating the usage of the OSRS API Client.
 * This class shows how to fetch and display player statistics.
 */
public final class Example {
    private Example() {
        // Utility class, no instantiation needed
    }

    /**
     * Main method that demonstrates fetching and displaying player statistics.
     *
     * @param args Command line arguments. First argument should be the player's RSN.
     *             Optional --virtual flag to show virtual levels above 99.
     */
    public static void main(String[] args) {
        if (!validateArgs(args)) {
            return;
        }

        PlayerNameOptions options = parsePlayerNameOptions(args);
        fetchAndDisplayPlayerData(options.rsn(), options.virtualLevels());
    }

    private static boolean validateArgs(String[] args) {
        if (args.length >= 1) {
            return true;
        }

        System.err.println("Please provide a player RSN as a command line argument.");
        System.err.println("Example: ./gradlew run --args=\"Zezima\"");
        System.err.println("Add --virtual flag to show virtual levels:");
        System.err.println("./gradlew run --args=\"Zezima --virtual\"");
        return false;
    }

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

    private static void fetchAndDisplayPlayerData(String rsn, boolean virtualLevels) {
        try {
            OsrsApiClient client = new OsrsApiClient();
            FetchOptions options = FetchOptions.builder()
                .calculateVirtualLevels(virtualLevels)
                .build();

            OsrsPlayer player = client.getPlayerByRsn(rsn, options);
            displayPlayerInformation(player, virtualLevels);
        } catch (IOException e) {
            System.err.println("Error fetching player data: " + e.getMessage());
            if (e.getMessage().contains("404")) {
                System.err.println("Player not found");
            } else if (e.getMessage().contains("503")) {
                System.err.println("OSRS Hiscores are currently unavailable");
            }
            e.printStackTrace();
        }
    }

    private static void displayPlayerInformation(OsrsPlayer player, boolean virtualLevels) {
        System.out.println("Player: " + player.getRsn());
        System.out.println("Level Type: " + (virtualLevels ? "Virtual" : "Regular"));

        displaySkills(player);
        displayActivities(player);
    }

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

    private record PlayerNameOptions(String rsn, boolean virtualLevels) { }
}
