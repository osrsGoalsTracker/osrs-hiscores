package com.osrs.impl.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class HiscoreEntry {
    private int id;
    private String name;
    private int rank;
    private int level;
    @JsonProperty("xp")
    private long experience;
    private int score;
} 