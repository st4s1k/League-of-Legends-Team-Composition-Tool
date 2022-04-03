package com.st4s1k.leagueteamcomp.json;

import com.google.gson.*;
import com.st4s1k.leagueteamcomp.model.champion.select.SummonerDTO;
import com.st4s1k.leagueteamcomp.model.champion.select.SummonerSlotDTO;

import java.lang.reflect.Type;

public class SummonerSlotDTOJsonDeserializer implements JsonDeserializer<SummonerSlotDTO> {

    @Override
    public SummonerSlotDTO deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        SummonerDTO deserializedSummonerDTO = context.deserialize(jsonObject.get("summoner").getAsJsonObject(), SummonerDTO.class);
        SummonerSlotDTO summonerSlotDTO = SummonerSlotDTO.newSlot();
        summonerSlotDTO.setItem(deserializedSummonerDTO);
        return summonerSlotDTO;
    }
}
