package com.st4s1k.leagueteamcomp.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.st4s1k.leagueteamcomp.model.champion.ChampionDTO;
import com.st4s1k.leagueteamcomp.model.champion.select.ChampionSlotDTO;

import java.lang.reflect.Type;

public class ChampionSlotDTOJsonSerializer implements JsonSerializer<ChampionSlotDTO> {

    @Override
    public JsonElement serialize(ChampionSlotDTO championSlotDTO, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonChampionSlotDTO = new JsonObject();
        jsonChampionSlotDTO.addProperty("championId", championSlotDTO.getChampion().map(ChampionDTO::getId).orElse(-1));
        return jsonChampionSlotDTO;
    }
}
