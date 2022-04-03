package com.st4s1k.leagueteamcomp.json;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.st4s1k.leagueteamcomp.model.champion.AttributeRatingsDTO;
import com.st4s1k.leagueteamcomp.model.champion.select.ChampionSlotDTO;
import com.st4s1k.leagueteamcomp.model.champion.select.SlotDTO;
import com.st4s1k.leagueteamcomp.model.champion.select.SummonerSlotDTO;
import com.st4s1k.leagueteamcomp.model.champion.select.TeamDTO;
import com.st4s1k.leagueteamcomp.model.enums.TeamSideEnum;
import com.st4s1k.leagueteamcomp.model.interfaces.ChampionProvider;
import com.st4s1k.leagueteamcomp.model.interfaces.SlotItem;

import java.lang.reflect.Type;
import java.util.List;

public class TeamDTOJsonDeserializer implements JsonDeserializer<TeamDTO> {

    @Override
    public TeamDTO deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        List<SummonerSlotDTO> deserializedSlots = context.deserialize(jsonObject.get("slots").getAsJsonArray(), getSlotsType());
        List<ChampionSlotDTO> deserializedBans = context.deserialize(jsonObject.get("bans").getAsJsonArray(), getBansType());
        TeamSideEnum deserializedTeamSide = TeamSideEnum.valueOf(jsonObject.get("teamSide").getAsString());
        AttributeRatingsDTO deserializedAttributeRatings = context.deserialize(jsonObject.get("attributeRatings").getAsJsonObject(), AttributeRatingsDTO.class);
        TeamDTO teamDTO = new TeamDTO(deserializedTeamSide);
        teamDTO.setAttributeRatings(deserializedAttributeRatings);
        populateSlots(deserializedSlots, teamDTO.getSlots());
        populateSlots(deserializedBans, teamDTO.getBans());
        return teamDTO;
    }

    private <T extends SlotItem, S extends SlotDTO<T>> void populateSlots(List<S> deserializedSlots, List<S> slots) {
        deserializedSlots.forEach(deserializedSlot -> slots.stream()
            .filter(ChampionProvider::isChampionNotSelected)
            .findFirst()
            .ifPresent(slot -> deserializedSlot.getItem().ifPresent(slot::setItem)));
    }

    private Type getSlotsType() {
        return new TypeToken<List<SummonerSlotDTO>>() {
        }.getType();
    }

    private Type getBansType() {
        return new TypeToken<List<ChampionSlotDTO>>() {
        }.getType();
    }
}
