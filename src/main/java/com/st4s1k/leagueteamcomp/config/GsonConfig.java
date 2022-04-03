package com.st4s1k.leagueteamcomp.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.st4s1k.leagueteamcomp.json.*;
import com.st4s1k.leagueteamcomp.model.champion.select.*;
import com.st4s1k.leagueteamcomp.service.LeagueTeamCompService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GsonConfig {

    @Bean
    public ChampSelectDTOJsonSerializer getChampSelectDTOJsonSerializer() {
        return new ChampSelectDTOJsonSerializer();
    }

    @Bean
    public ChampSelectDTOJsonDeserializer getChampSelectDTOJsonDeserializer() {
        return new ChampSelectDTOJsonDeserializer();
    }

    @Bean
    public TeamDTOJsonSerializer getTeamDTOJsonSerializer() {
        return new TeamDTOJsonSerializer();
    }

    @Bean
    public TeamDTOJsonDeserializer getTeamDTOJsonDeserializer() {
        return new TeamDTOJsonDeserializer();
    }

    @Bean
    public ChampionSlotDTOJsonSerializer getChampionSlotDTOJsonSerializer() {
        return new ChampionSlotDTOJsonSerializer();
    }

    @Bean
    public ChampionSlotDTOJsonDeserializer getChampionSlotDTOJsonDeserializer(
        LeagueTeamCompService leagueTeamCompService
    ) {
        return new ChampionSlotDTOJsonDeserializer(leagueTeamCompService);
    }

    @Bean
    public SummonerSlotDTOJsonSerializer getSummonerSlotDTOJsonSerializer() {
        return new SummonerSlotDTOJsonSerializer();
    }

    @Bean
    public SummonerSlotDTOJsonDeserializer getSummonerSlotDTOJsonDeserializer() {
        return new SummonerSlotDTOJsonDeserializer();
    }

    @Bean
    public SummonerDTOJsonSerializer getSummonerDTOJsonSerializer() {
        return new SummonerDTOJsonSerializer();
    }

    @Bean
    public SummonerDTOJsonDeserializer getSummonerDTOJsonDeserializer(
        LeagueTeamCompService leagueTeamCompService
    ) {
        return new SummonerDTOJsonDeserializer(leagueTeamCompService);
    }


    @Bean
    public Gson getGson(
        ChampSelectDTOJsonSerializer champSelectDTOJsonSerializer,
        ChampSelectDTOJsonDeserializer champSelectDTOJsonDeserializer,
        TeamDTOJsonSerializer teamDTOJsonSerializer,
        TeamDTOJsonDeserializer teamDTOJsonDeserializer,
        ChampionSlotDTOJsonSerializer championSlotDTOJsonSerializer,
        ChampionSlotDTOJsonDeserializer championSlotDTOJsonDeserializer,
        SummonerSlotDTOJsonSerializer summonerSlotDTOJsonSerializer,
        SummonerSlotDTOJsonDeserializer summonerSlotDTOJsonDeserializer,
        SummonerDTOJsonSerializer summonerDTOJsonSerializer,
        SummonerDTOJsonDeserializer summonerDTOJsonDeserializer
    ) {
        return new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(ChampSelectDTO.class, champSelectDTOJsonSerializer)
            .registerTypeAdapter(ChampSelectDTO.class, champSelectDTOJsonDeserializer)
            .registerTypeAdapter(TeamDTO.class, teamDTOJsonSerializer)
            .registerTypeAdapter(TeamDTO.class, teamDTOJsonDeserializer)
            .registerTypeAdapter(ChampionSlotDTO.class, championSlotDTOJsonSerializer)
            .registerTypeAdapter(ChampionSlotDTO.class, championSlotDTOJsonDeserializer)
            .registerTypeAdapter(SummonerSlotDTO.class, summonerSlotDTOJsonSerializer)
            .registerTypeAdapter(SummonerSlotDTO.class, summonerSlotDTOJsonDeserializer)
            .registerTypeAdapter(SummonerDTO.class, summonerDTOJsonSerializer)
            .registerTypeAdapter(SummonerDTO.class, summonerDTOJsonDeserializer)
            .create();
    }
}
