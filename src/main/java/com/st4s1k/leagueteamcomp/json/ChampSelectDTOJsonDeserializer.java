package com.st4s1k.leagueteamcomp.json;

import com.google.gson.*;
import com.st4s1k.leagueteamcomp.model.champion.select.ChampSelectDTO;
import com.st4s1k.leagueteamcomp.model.champion.select.SlotDTO;
import com.st4s1k.leagueteamcomp.model.champion.select.TeamDTO;
import com.st4s1k.leagueteamcomp.model.interfaces.ChampionProvider;
import com.st4s1k.leagueteamcomp.model.interfaces.SlotItem;

import java.lang.reflect.Type;
import java.util.List;

public class ChampSelectDTOJsonDeserializer implements JsonDeserializer<ChampSelectDTO> {

    @Override
    public ChampSelectDTO deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        ChampSelectDTO champSelectDTO = new ChampSelectDTO();
        TeamDTO deserializedAllyTeam = context.deserialize(jsonObject.get("allyTeam").getAsJsonObject(), TeamDTO.class);
        TeamDTO deserializedEnemyTeam = context.deserialize(jsonObject.get("enemyTeam").getAsJsonObject(), TeamDTO.class);
        populateChampSelectTeam(champSelectDTO.getAllyTeam(), deserializedAllyTeam);
        populateChampSelectTeam(champSelectDTO.getEnemyTeam(), deserializedEnemyTeam);
        return champSelectDTO;
    }

    private void populateChampSelectTeam(TeamDTO team, TeamDTO deserializedTeam) {
        team.setAttributeRatings(deserializedTeam.getAttributeRatings());
        populateSlots(deserializedTeam.getSlots(), team.getSlots());
        populateSlots(deserializedTeam.getBans(), team.getBans());
    }

    private <T extends SlotItem, S extends SlotDTO<T>> void populateSlots(List<S> deserializedSlots, List<S> slots) {
        deserializedSlots.forEach(deserializedSlot -> slots.stream()
            .filter(ChampionProvider::isChampionNotSelected)
            .findFirst()
            .ifPresent(slot -> deserializedSlot.getItem().ifPresent(slot::setItem)));
    }
}
