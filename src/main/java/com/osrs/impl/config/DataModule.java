package com.osrs.impl.config;

import com.google.inject.AbstractModule;
import com.osrs.OsrsPlayerFetcher;
import com.osrs.impl.fetcher.OsrsPlayerFetcherImpl;
import com.osrs.impl.service.HttpService;
import com.osrs.impl.service.impl.HttpServiceImpl;

public class DataModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(HttpService.class).to(HttpServiceImpl.class);
        bind(OsrsPlayerFetcher.class).to(OsrsPlayerFetcherImpl.class);
    }
} 