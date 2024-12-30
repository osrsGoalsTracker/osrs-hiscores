package com.osrshiscores.apiclient;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import com.osrshiscores.apiclient.model.input.FetchOptions;
import com.osrshiscores.apiclient.model.output.OsrsPlayer;
import com.osrshiscores.apiclient.service.HttpService;
import com.osrshiscores.apiclient.service.impl.HttpServiceImpl;
import com.osrshiscores.apiclient.utils.HiscoresResponseParser;

/**
 * Main client for interacting with the OSRS Hiscores API.
 * This class provides methods to fetch player statistics from the OSRS hiscores.
 */
public class OsrsApiClient {
    private static final String HISCORES_URL = "https://secure.runescape.com/m=hiscore_oldschool/index_lite.ws?player=";
    private final HttpService httpService;

    /**
     * Creates a new OsrsApiClient with default settings.
     */
    public OsrsApiClient() {
        this.httpService = new HttpServiceImpl();
    }

    /**
     * Creates a new OsrsApiClient with a custom HTTP service.
     *
     * @param httpService The HTTP service to use for making requests
     */
    public OsrsApiClient(HttpService httpService) {
        this.httpService = httpService;
    }

    /**
     * Fetches player data from the OSRS hiscores using default options.
     *
     * @param rsn The player's RuneScape name
     * @return OsrsPlayer object containing the player's statistics
     * @throws IOException If there is an error fetching the data
     */
    public OsrsPlayer getPlayerByRsn(String rsn) throws IOException {
        return getPlayerByRsn(rsn, FetchOptions.defaults());
    }

    /**
     * Fetches player data from the OSRS hiscores with custom options.
     *
     * @param rsn The player's RuneScape name
     * @param options Options for fetching the data (e.g., virtual levels)
     * @return OsrsPlayer object containing the player's statistics
     * @throws IOException If there is an error fetching the data
     */
    public OsrsPlayer getPlayerByRsn(String rsn, FetchOptions options) throws IOException {
        String encodedRsn = URLEncoder.encode(rsn, StandardCharsets.UTF_8);
        String response = httpService.get(HISCORES_URL + encodedRsn);
        return HiscoresResponseParser.parse(rsn, response, options);
    }
} 