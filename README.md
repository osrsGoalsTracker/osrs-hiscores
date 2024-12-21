# OSRS Player Data API

This is a JDK21 implementation of the OSRS Player Data Interface for fetching player statistics from the Old School RuneScape hiscores.

## Features

- Fetches player skills and activities from the OSRS hiscores
- Uses Google Guice for dependency injection
- Implements the `osrs-player-data-interface` package
- Built with JDK 21

## Prerequisites

- JDK 21
- Gradle 8.x (or use the included Gradle wrapper)

## Setup

1. Clone the repository
2. Build the project:
```bash
./gradlew build
```

## Running the Example

The project includes an example application that demonstrates how to use the API. To run it:

```bash
./gradlew run
```

This will fetch and display the stats for the player "SoloMission", showing:
- All skills (level, rank, and XP)
- All activities (rank and score)

To modify the example to look up a different player, edit `src/main/java/com/osrs/data/Example.java` and change the username in:
```java
OsrsPlayer player = fetcher.getPlayerByRsn("YourPlayerName", options);
```

## Usage in Your Project

The implementation uses Google Guice for dependency injection. Here's a basic example:

```java
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.osrs.OsrsPlayerFetcher;
import com.osrs.data.config.DataModule;

public class Example {
    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new DataModule());
        OsrsPlayerFetcher fetcher = injector.getInstance(OsrsPlayerFetcher.class);
        
        try {
            FetchOptions options = FetchOptions.builder().build();
            OsrsPlayer player = fetcher.getPlayerByRsn("playerName", options);
            System.out.println("Total level: " + player.getSkills().stream()
                .mapToInt(Skill::getLevel)
                .sum());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

## Testing

Run the tests using:
```bash
./gradlew test
``` 