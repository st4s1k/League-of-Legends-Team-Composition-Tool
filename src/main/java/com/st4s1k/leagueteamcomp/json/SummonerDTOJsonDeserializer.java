package com.st4s1k.leagueteamcomp.json;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.st4s1k.leagueteamcomp.model.champion.select.ChampionSlotDTO;
import com.st4s1k.leagueteamcomp.model.champion.select.SlotDTO;
import com.st4s1k.leagueteamcomp.model.champion.select.SummonerDTO;
import com.st4s1k.leagueteamcomp.model.interfaces.ChampionProvider;
import com.st4s1k.leagueteamcomp.model.interfaces.SlotItem;
import com.st4s1k.leagueteamcomp.service.LeagueTeamCompService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SummonerDTOJsonDeserializer implements JsonDeserializer<SummonerDTO> {

    private final LeagueTeamCompService leagueTeamCompService;

    @Override
    public SummonerDTO deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        Long deserializedSummonerId = Optional.ofNullable(jsonObject.get("summonerId")).map(JsonElement::getAsLong).orElse(null);
        String deserializedSummonerName = Optional.ofNullable(jsonObject.get("summonerName")).map(JsonElement::getAsString).orElse(null);
        Integer deserializedChampionId = Optional.ofNullable(jsonObject.get("championId")).map(JsonElement::getAsInt).orElse(null);
        List<ChampionSlotDTO> deserializedChampionSuggestions = context.deserialize(
            jsonObject.get("championSuggestions").getAsJsonArray(),
            getChampionSuggestionsType()
        );
        SummonerDTO summonerDTO = new SummonerDTO();
        summonerDTO.setSummonerId(deserializedSummonerId);
        summonerDTO.setSummonerName(deserializedSummonerName);
        leagueTeamCompService.findChampionById(deserializedChampionId).ifPresent(summonerDTO::setChampion);
        populateSlots(deserializedChampionSuggestions, summonerDTO.getChampionSuggestions());
        return summonerDTO;
    }

    private <T extends SlotItem, S extends SlotDTO<T>> void populateSlots(List<S> deserializedSlots, List<S> slots) {
        deserializedSlots.forEach(deserializedSlot -> slots.stream()
            .filter(ChampionProvider::isChampionNotSelected)
            .findFirst()
            .ifPresent(slot -> deserializedSlot.getItem().ifPresent(slot::setItem)));
    }

    private Type getChampionSuggestionsType() {
        return new TypeToken<List<ChampionSlotDTO>>() {
        }.getType();
    }
}
