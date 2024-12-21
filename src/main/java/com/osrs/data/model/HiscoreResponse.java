package com.osrs.data.model;

import lombok.Data;

@Data
public class HiscoreResponse {
    private HiscoreEntry[] skills;
    private HiscoreEntry[] activities;
} 