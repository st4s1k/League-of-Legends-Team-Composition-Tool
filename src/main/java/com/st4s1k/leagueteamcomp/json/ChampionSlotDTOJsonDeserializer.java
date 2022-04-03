package com.st4s1k.leagueteamcomp.json;

import com.google.gson.*;
import com.st4s1k.leagueteamcomp.model.champion.select.ChampionSlotDTO;
import com.st4s1k.leagueteamcomp.service.LeagueTeamCompService;

import java.lang.reflect.Type;
import java.util.Optional;

public class ChampionSlotDTOJsonDeserializer implements JsonDeserializer<ChampionSlotDTO> {

    @Override
    public ChampionSlotDTO deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        Integer deserializedChampionId = Optional.ofNullable(jsonObject.get("championId")).map(JsonElement::getAsInt).orElse(null);
        return LeagueTeamCompService.getInstance().findChampionById(deserializedChampionId)
            .map(championDTO -> {
                ChampionSlotDTO championSlotDTO = ChampionSlotDTO.newSlot();
                championSlotDTO.setChampion(championDTO);
                return championSlotDTO;
            }).orElseGet(ChampionSlotDTO::newSlot);
    }
}
