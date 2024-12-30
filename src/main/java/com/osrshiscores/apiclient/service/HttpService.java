package com.osrshiscores.apiclient.service;

import java.io.IOException;

/**
 * Service interface for making HTTP requests.
 * This interface abstracts the HTTP client implementation to make the code more testable.
 */
public interface HttpService {
    /**
     * Makes a GET request to the specified URL and returns the response body as a string.
     *
     * @param url The URL to send the GET request to
     * @return The response body as a string
     * @throws IOException If there is an error making the request or reading the response
     */
    String get(String url) throws IOException;
}
