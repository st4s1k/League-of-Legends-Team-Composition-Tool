package com.st4s1k.leagueteamcomp.model.champion;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.Map;

@Data
public class ChampionsDTO {
    @SerializedName("champions")
    private Map<String, ChampionDTO> champions;
}
