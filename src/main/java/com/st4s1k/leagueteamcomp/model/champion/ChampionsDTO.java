package com.st4s1k.leagueteamcomp.model.champion;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

import java.util.Map;

@Getter
public class ChampionsDTO {
    @SerializedName("champions")
    private Map<String, ChampionDTO> champions;
}
