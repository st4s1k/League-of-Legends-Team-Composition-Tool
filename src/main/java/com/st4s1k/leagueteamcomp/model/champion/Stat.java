package com.st4s1k.leagueteamcomp.model.champion;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class Stat {
    @SerializedName("flat")
    private double flat;
    @SerializedName("percent")
    private double percent;
    @SerializedName("perLevel")
    private double perLevel;
    @SerializedName("percentPerLevel")
    private double percentPerLevel;
}
