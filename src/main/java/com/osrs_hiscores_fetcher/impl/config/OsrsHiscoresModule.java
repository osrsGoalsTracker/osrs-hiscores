package com.osrs_hiscores_fetcher.impl.config;

import com.google.inject.AbstractModule;
import com.osrs_hiscores_fetcher.api.OsrsHiscoresPlayerFetcher;
import com.osrs_hiscores_fetcher.impl.fetcher.OsrsHiscoresPlayerFetcherImpl;
import com.osrs_hiscores_fetcher.impl.service.HttpService;
import com.osrs_hiscores_fetcher.impl.service.impl.HttpServiceImpl;

/**
 * Guice module that configures dependency injection bindings for the OSRS Hiscores API implementation.
 * This module provides the necessary bindings to create a working instance of the OsrsHiscoresPlayerFetcher.
 * 
 * <p>The module sets up two main bindings:
 * <ul>
 *   <li>{@link HttpService} → {@link HttpServiceImpl}: Handles HTTP requests to the OSRS Hiscores API</li>
 *   <li>{@link OsrsHiscoresPlayerFetcher} → {@link OsrsHiscoresPlayerFetcherImpl}: Main interface implementation</li>
 * </ul>
 * 
 * <p>Usage with Guice:
 * <pre>{@code
 * Injector injector = Guice.createInjector(new OsrsHiscoresModule());
 * OsrsHiscoresPlayerFetcher fetcher = injector.getInstance(OsrsHiscoresPlayerFetcher.class);
 * }</pre>
 * 
 * <p>This module is thread-safe and can be used to create multiple instances of the fetcher
 * that can be safely shared between threads.
 */
public class OsrsHiscoresModule extends AbstractModule {
    /**
     * Creates a new OsrsHiscoresModule instance.
     * This module is used to configure Guice bindings for the OSRS Hiscores API implementation.
     * The module is stateless and can be safely reused across multiple injectors.
     */
    public OsrsHiscoresModule() {
    }

    /**
     * Configures the dependency injection bindings for the OSRS Hiscores API.
     * 
     * <p>This method sets up the following bindings:
     * <ul>
     *   <li>{@link HttpService} is bound to {@link HttpServiceImpl} for making HTTP requests</li>
     *   <li>{@link OsrsHiscoresPlayerFetcher} is bound to {@link OsrsHiscoresPlayerFetcherImpl} 
     *       for fetching player data</li>
     * </ul>
     * 
     * <p>Both implementations are bound in singleton scope, making them thread-safe and reusable
     * across multiple requests.
     */
    @Override
    protected void configure() {
        bind(HttpService.class).to(HttpServiceImpl.class);
        bind(OsrsHiscoresPlayerFetcher.class).to(OsrsHiscoresPlayerFetcherImpl.class);
    }
}
