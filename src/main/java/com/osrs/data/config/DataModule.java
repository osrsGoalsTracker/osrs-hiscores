package com.osrs.data.config;

import com.google.inject.AbstractModule;
import com.osrs.OsrsPlayerFetcher;
import com.osrs.data.fetcher.OsrsPlayerFetcherImpl;
import com.osrs.data.service.HttpService;
import com.osrs.data.service.impl.HttpServiceImpl;

public class DataModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(HttpService.class).to(HttpServiceImpl.class);
        bind(OsrsPlayerFetcher.class).to(OsrsPlayerFetcherImpl.class);
    }
} 