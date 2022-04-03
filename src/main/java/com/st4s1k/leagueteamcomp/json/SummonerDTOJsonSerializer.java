package com.st4s1k.leagueteamcomp.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.st4s1k.leagueteamcomp.model.champion.ChampionDTO;
import com.st4s1k.leagueteamcomp.model.champion.select.SummonerDTO;

import java.lang.reflect.Type;

public class SummonerDTOJsonSerializer implements JsonSerializer<SummonerDTO> {

    @Override
    public JsonElement serialize(SummonerDTO summonerDTO, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonSummonerDTO = new JsonObject();
        jsonSummonerDTO.addProperty("summonerId", summonerDTO.getSummonerId());
        jsonSummonerDTO.addProperty("summonerName", summonerDTO.getSummonerName());
        jsonSummonerDTO.addProperty("championId", summonerDTO.getChampion().map(ChampionDTO::getId).orElse(-1));
        jsonSummonerDTO.add("championSuggestions", context.serialize(summonerDTO.getChampionSuggestions()).getAsJsonArray());
        return jsonSummonerDTO;
    }
}
