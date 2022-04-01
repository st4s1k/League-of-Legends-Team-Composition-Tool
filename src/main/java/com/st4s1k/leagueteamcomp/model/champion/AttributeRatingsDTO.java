package com.st4s1k.leagueteamcomp.model.champion;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class AttributeRatingsDTO {
    @SerializedName("damage")
    private Double damage;
    @SerializedName("toughness")
    private Double toughness;
    @SerializedName("control")
    private Double control;
    @SerializedName("mobility")
    private Double mobility;
    @SerializedName("utility")
    private Double utility;
    @SerializedName("abilityReliance")
    private Double abilityReliance;
    @SerializedName("attack")
    private Double attack;
    @SerializedName("defense")
    private Double defense;
    @SerializedName("magic")
    private Double magic;
    @SerializedName("difficulty")
    private Double difficulty;
}
