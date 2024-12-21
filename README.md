# OSRS Player Data API

This is a JDK21 implementation of the [OSRS Player Data Interface](https://github.com/osrsGoalsTracker/osrs-player-data-interface) for fetching player statistics from the Old School RuneScape hiscores. This package implements the interface defined in `osrs-player-data-interface` to provide a concrete implementation for fetching and parsing player data.

## Features

- Full implementation of the [osrs-player-data-interface](https://github.com/osrsGoalsTracker/osrs-player-data-interface) contract
- Fetches player skills and activities from the OSRS hiscores using the JSON API
- Uses Google Guice for dependency injection
- Built with JDK 21
- Supports JitPack for easy dependency management

## Prerequisites

- JDK 21
- Gradle 8.x (or use the included Gradle wrapper)

## Interface Implementation

This package implements the following interfaces from `osrs-player-data-interface`:

- `OsrsHiscoresPlayerFetcher`: Main interface for fetching player data
- `OsrsPlayer`: Model for player data with skills and activities
- `Skill`: Model for individual skill data
- `Activity`: Model for activity/boss data

The implementation uses the OSRS Hiscores JSON API to fetch and parse player data, providing a clean and type-safe way to access player statistics.

## Setup

1. Add the JitPack repository to your build file:
```gradle
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

2. Add the dependency:
```gradle
dependencies {
    implementation 'com.github.osrsGoalsTracker:osrs-player-data-api:v2.0.0'
}
```

## Running the Example

The project includes an example application that demonstrates how to use the API. To run it, provide a player's RSN as a command line argument:

```bash
./gradlew run --args="SoloMission"
```

This will fetch and display the stats for the specified player (in this example, "SoloMission"), showing:
- All skills (level, rank, and XP)
- All activities (rank and score)

You can look up any player by replacing "SoloMission" with their RSN:
```bash
./gradlew run --args="eow btw"
```

The example code is located in `src/main/java/com/osrs_hiscores_fetcher/impl/example/Example.java`.

## Usage in Your Project

⚠️ **Rate Limiting Warning**: This implementation provides direct access to the OSRS Hiscores API without rate limiting. In a production environment, you should implement appropriate rate limiting on top of this package to avoid overwhelming the OSRS API and to be a good API citizen.

There are several ways to use this implementation:

### 1. Using Guice Dependency Injection (Recommended)

This is the recommended approach as it properly handles dependency injection:

```java
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.osrs_hiscores_fetcher.api.FetchOptions;
import com.osrs_hiscores_fetcher.api.OsrsHiscoresPlayerFetcher;
import com.osrs_hiscores_fetcher.api.models.OsrsPlayer;
import com.osrs_hiscores_fetcher.api.models.Skill;
import com.osrs_hiscores_fetcher.api.models.Activity;
import com.osrs_hiscores_fetcher.impl.config.OsrsHiscoresModule;
import java.io.IOException;

public class Example {
    public static void main(String[] args) {
        // Initialize Guice with our OsrsHiscoresModule
        Injector injector = Guice.createInjector(new OsrsHiscoresModule());
        
        // Get an instance of our implementation
        OsrsHiscoresPlayerFetcher fetcher = injector.getInstance(OsrsHiscoresPlayerFetcher.class);
        
        try {
            // Use the fetcher
            OsrsPlayer player = fetcher.getPlayerByRsn("playerName", FetchOptions.builder().build());
            // ... process player data ...
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

### 2. Direct Instantiation

If you don't want to use Guice, you can manually create the implementation:

```java
import com.osrs_hiscores_fetcher.impl.fetcher.OsrsHiscoresPlayerFetcherImpl;
import com.osrs_hiscores_fetcher.impl.service.impl.HttpServiceImpl;
import com.osrs_hiscores_fetcher.api.OsrsHiscoresPlayerFetcher;

public class Example {
    public static void main(String[] args) {
        // Create the HTTP service
        HttpServiceImpl httpService = new HttpServiceImpl();
        
        // Create the fetcher with the HTTP service
        OsrsHiscoresPlayerFetcher fetcher = new OsrsHiscoresPlayerFetcherImpl(httpService);
        
        try {
            OsrsPlayer player = fetcher.getPlayerByRsn("playerName", FetchOptions.builder().build());
            // ... process player data ...
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

### 3. Using in a Spring Application

If you're using Spring, you can configure the beans:

```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.osrs_hiscores_fetcher.impl.service.HttpService;
import com.osrs_hiscores_fetcher.impl.service.impl.HttpServiceImpl;
import com.osrs_hiscores_fetcher.impl.fetcher.OsrsHiscoresPlayerFetcherImpl;

@Configuration
public class OsrsConfig {
    @Bean
    public HttpService httpService() {
        return new HttpServiceImpl();
    }
    
    @Bean
    public OsrsHiscoresPlayerFetcher osrsHiscoresPlayerFetcher(HttpService httpService) {
        return new OsrsHiscoresPlayerFetcherImpl(httpService);
    }
}
```

### Processing Player Data

Once you have a fetcher instance, you can process the data:

```java
// Basic usage with default options
OsrsPlayer player = fetcher.getPlayerByRsn("playerName", FetchOptions.builder().build());

// Print total level
System.out.println("Total level: " + player.getSkills().stream()
    .mapToInt(Skill::getLevel)
    .sum());

// Print all skills
player.getSkills().forEach(skill -> {
    System.out.printf("%s: Level %d (Rank %d, XP %d)%n",
        skill.getName(),
        skill.getLevel(),
        skill.getRank(),
        skill.getXp());
});

// Print all activities with a rank
player.getActivities().stream()
    .filter(activity -> activity.getRank() > 0)
    .forEach(activity -> {
        System.out.printf("%s: Score %d (Rank %d)%n",
            activity.getName(),
            activity.getScore(),
            activity.getRank());
    });
```

### Error Handling

The API can throw the following exceptions:
- `IOException`: Network errors or API issues
- `IllegalArgumentException`: Invalid RSN or player not found

### Thread Safety

The implementation is thread-safe and can be used concurrently. The `OsrsHiscoresPlayerFetcher` instance can be safely shared between threads.

### Response Models

All models returned by the API are immutable and thread-safe:

- `OsrsPlayer`: Contains the player's RSN and collections of skills and activities
- `Skill`: Contains skill data (id, name, rank, level, xp)
- `Activity`: Contains activity data (id, name, rank, score)

Collections returned by these models are unmodifiable.

## Testing

Run the tests using:
```bash
./gradlew test
```

## Building

Build the project using:
```bash
./gradlew build
```

## Interface Documentation

For detailed information about the interfaces being implemented, please refer to the [osrs-player-data-interface documentation](https://github.com/osrsGoalsTracker/osrs-player-data-interface).

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.