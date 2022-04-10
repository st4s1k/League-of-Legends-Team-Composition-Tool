package com.st4s1k.leagueteamcomp.json;

import com.google.gson.*;
import com.st4s1k.leagueteamcomp.model.champion.select.ChampionSlotDTO;
import com.st4s1k.leagueteamcomp.service.LeagueTeamCompService;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Type;
import java.util.Optional;

@RequiredArgsConstructor
public class ChampionSlotDTOJsonDeserializer implements JsonDeserializer<ChampionSlotDTO> {

    private final LeagueTeamCompService leagueTeamCompService;

    @Override
    public ChampionSlotDTO deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        Integer deserializedChampionId = Optional.ofNullable(jsonObject.get("championId")).map(JsonElement::getAsInt).orElse(null);
        return leagueTeamCompService.findChampionById(deserializedChampionId)
            .map(championDTO -> {
                ChampionSlotDTO championSlotDTO = ChampionSlotDTO.newSlot();
                championSlotDTO.setChampion(championDTO);
                return championSlotDTO;
            }).orElseGet(ChampionSlotDTO::newSlot);
    }
}
