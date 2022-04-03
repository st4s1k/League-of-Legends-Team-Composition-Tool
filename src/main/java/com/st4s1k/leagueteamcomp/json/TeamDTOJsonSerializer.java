package com.st4s1k.leagueteamcomp.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import com.st4s1k.leagueteamcomp.model.champion.AttributeRatingsDTO;
import com.st4s1k.leagueteamcomp.model.champion.select.ChampionSlotDTO;
import com.st4s1k.leagueteamcomp.model.champion.select.SummonerSlotDTO;
import com.st4s1k.leagueteamcomp.model.champion.select.TeamDTO;

import java.lang.reflect.Type;
import java.util.List;

public class TeamDTOJsonSerializer implements JsonSerializer<TeamDTO> {

    @Override
    public JsonElement serialize(TeamDTO teamDTO, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonTeamDTO = new JsonObject();
        jsonTeamDTO.add("slots", context.serialize(teamDTO.getSlots(), getSlotsType()).getAsJsonArray());
        jsonTeamDTO.add("bans", context.serialize(teamDTO.getBans(), getBansType()).getAsJsonArray());
        jsonTeamDTO.addProperty("teamSide", teamDTO.getTeamSide().name());
        jsonTeamDTO.add("attributeRatings", context.serialize(teamDTO.getAttributeRatings(), AttributeRatingsDTO.class).getAsJsonObject());
        return jsonTeamDTO;
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
