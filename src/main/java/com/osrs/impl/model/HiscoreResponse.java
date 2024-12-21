package com.osrs.impl.model;

import lombok.Data;

@Data
public class HiscoreResponse {
    private HiscoreEntry[] skills;
    private HiscoreEntry[] activities;
} 