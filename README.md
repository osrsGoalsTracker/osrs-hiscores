# OSRS API Client

A Java client library for the Old School RuneScape Hiscores API. This library provides a simple and intuitive way to fetch player statistics from the OSRS hiscores.

## Features

- Fetch player skills and activities from the OSRS hiscores
- Support for virtual levels (levels above 99)
- Clean and type-safe API with standard Java exceptions
- Built with Java 17 and modern best practices
- Minimal dependencies (OkHttp, Jackson, SLF4J)
- Easy to use builder pattern for configuration

## Project Structure

```
src/main/java/com/osrshiscores/apiclient/
├── OsrsApiClient.java           # Main API client class
├── model/                       # Data models
│   ├── input/                  # Input models (e.g., FetchOptions)
│   └── output/                 # Output models (e.g., OsrsPlayer, Skill, Activity)
├── service/                    # Internal services
└── example/                    # Example usage
```

## Requirements

- Java 17 or higher
- Gradle 7.0 or higher (for building from source)

## Quick Start

1. Add the dependency to your `build.gradle`:
```groovy
dependencies {
    implementation 'com.osrshiscores:osrs-api-client:1.0-SNAPSHOT'
}
```

2. Create a client and fetch player data:
```java
import java.io.IOException;
import com.osrshiscores.apiclient.OsrsApiClient;
import com.osrshiscores.apiclient.model.output.OsrsPlayer;

try {
    // Create the client (thread-safe, can be reused)
    OsrsApiClient client = new OsrsApiClient();

    // Fetch player data
    OsrsPlayer player = client.getPlayerByRsn("Zezima");

    // Access skills
    player.getSkills().forEach(skill -> {
        System.out.printf("%s: Level %d (XP: %d)%n",
            skill.getName(),
            skill.getLevel(),
            skill.getXp());
    });

    // Access activities/bosses
    player.getActivities().forEach(activity -> {
        System.out.printf("%s: Score %d%n",
            activity.getName(),
            activity.getScore());
    });
} catch (IOException e) {
    // Handle network errors or API issues
    System.err.println("Error: " + e.getMessage());
}
```

## Advanced Usage

### Virtual Levels

Enable virtual levels (above 99) using `FetchOptions`:

```java
import com.osrshiscores.apiclient.model.input.FetchOptions;

try {
    FetchOptions options = FetchOptions.builder()
        .calculateVirtualLevels(true)
        .build();

    OsrsPlayer player = client.getPlayerByRsn("Zezima", options);
} catch (IOException e) {
    System.err.println("Error: " + e.getMessage());
}
```

### Error Handling

The library uses standard Java exceptions:

```java
try {
    OsrsPlayer player = client.getPlayerByRsn("NonExistentPlayer");
} catch (IOException e) {
    if (e.getMessage().contains("404")) {
        System.err.println("Player doesn't exist");
    } else if (e.getMessage().contains("503")) {
        System.err.println("OSRS Hiscores are down");
    } else {
        System.err.println("Error: " + e.getMessage());
    }
}
```

## Example Application

Run the included example application:

```bash
# Show regular levels
./gradlew run --args="PlayerName"

# Show virtual levels (above 99)
./gradlew run --args="PlayerName --virtual"
```

## Building from Source

```bash
# Clone the repository
git clone https://github.com/yourusername/osrs-api-client.git
cd osrs-api-client

# Build and run tests
./gradlew clean build

# Run the example
./gradlew run --args="Zezima"
```

## Contributing

We welcome contributions! Here's how you can help:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## Support

- Create an issue on GitHub
- Check existing issues for solutions
- Read the [documentation](https://github.com/yourusername/osrs-api-client/wiki)

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.