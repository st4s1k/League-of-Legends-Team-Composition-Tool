package com.st4s1k.leagueteamcomp.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.st4s1k.leagueteamcomp.model.champion.select.SummonerSlotDTO;

import java.lang.reflect.Type;

public class SummonerSlotDTOJsonSerializer implements JsonSerializer<SummonerSlotDTO> {

    @Override
    public JsonElement serialize(SummonerSlotDTO summonerSlotDTO, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonSummonerSlotDTO = new JsonObject();
        jsonSummonerSlotDTO.add("summoner", context.serialize(summonerSlotDTO.getItem().orElse(null)).getAsJsonObject());
        return jsonSummonerSlotDTO;
    }
}
