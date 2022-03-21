package com.st4s1k.leagueteamcomp.model.champion;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.Map;

@Data
public class Champions {

    @SerializedName("champions")
    private Map<String, Champion> champions;
}
