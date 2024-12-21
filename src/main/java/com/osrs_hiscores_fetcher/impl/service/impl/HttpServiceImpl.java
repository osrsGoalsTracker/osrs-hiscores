package com.osrs_hiscores_fetcher.impl.service.impl;

import com.osrs_hiscores_fetcher.impl.service.HttpService;
import jakarta.inject.Singleton;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Implementation of the HttpService interface using Java's built-in HttpClient.
 * This implementation includes a configurable timeout and handles request interruption.
 */
@Singleton
public class HttpServiceImpl implements HttpService {
    private final HttpClient httpClient;

    /**
     * Creates a new HttpServiceImpl with a configured HttpClient.
     * The HttpClient is configured with a 10-second connection timeout.
     */
    public HttpServiceImpl() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    /**
     * {@inheritDoc}
     * This implementation uses Java's HttpClient to make the request.
     * If the request is interrupted, it will restore the interrupt flag and throw an IOException.
     */
    @Override
    public String get(String url) throws IOException {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            return httpClient.send(request, HttpResponse.BodyHandlers.ofString())
                    .body();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Request interrupted", e);
        }
    }
} 