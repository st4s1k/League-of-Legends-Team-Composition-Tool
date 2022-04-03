package com.st4s1k.leagueteamcomp.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.st4s1k.leagueteamcomp.model.champion.select.ChampSelectDTO;
import com.st4s1k.leagueteamcomp.model.champion.select.TeamDTO;

import java.lang.reflect.Type;

public class ChampSelectDTOJsonSerializer implements JsonSerializer<ChampSelectDTO> {

    @Override
    public JsonElement serialize(ChampSelectDTO champSelectDTO, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonChampSelectDTO = new JsonObject();
        jsonChampSelectDTO.add("allyTeam", context.serialize(champSelectDTO.getAllyTeam(), TeamDTO.class).getAsJsonObject());
        jsonChampSelectDTO.add("enemyTeam", context.serialize(champSelectDTO.getEnemyTeam(), TeamDTO.class).getAsJsonObject());
        return jsonChampSelectDTO;
    }
}
