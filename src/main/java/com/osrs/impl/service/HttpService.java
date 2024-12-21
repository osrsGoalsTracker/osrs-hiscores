package com.osrs.impl.service;

import java.io.IOException;

public interface HttpService {
    String get(String url) throws IOException;
} 