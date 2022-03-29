package com.st4s1k.leagueteamcomp.service;

import com.st4s1k.leagueteamcomp.model.champion.AttributeRatings;
import com.st4s1k.leagueteamcomp.model.champion.Champion;
import com.st4s1k.leagueteamcomp.repository.ChampionRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.ToDoubleFunction;

public class LeagueTeamCompService {

    private static LeagueTeamCompService INSTANCE;
    private static final double CHAMPION_STAT_OUTPUT_SCALE = 10;

    private ChampionRepository championRepository;

    public static LeagueTeamCompService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new LeagueTeamCompService();
            INSTANCE.championRepository = ChampionRepository.getInstance();
        }
        return INSTANCE;
    }

    public Set<String> getAllChampionKeys() {
        return championRepository.getAllChampionKeys();
    }

    public Optional<Champion> findChampionDataById(Integer championId) {
        return championRepository.findChampionDataById(championId);
    }

    public Optional<Champion> findChampionDataByKey(String championKey) {
        return championRepository.findChampionDataByKey(championKey);
    }

    public Optional<Champion> findChampionDataByName(String championName) {
        return championRepository.findChampionDataByName(championName);
    }

    public boolean existsChampionDataByName(String championName) {
        return championRepository.existsChampionDataByName(championName);
    }

    public double getChampionInfoValue(
        List<? extends String> championList,
        ToDoubleFunction<AttributeRatings> getValue,
        double valueScale
    ) {
        double teamStatSum = championList.stream()
            .map(championRepository::findChampionDataByKey)
            .flatMap(Optional::stream)
            .map(Champion::getAttributeRatings)
            .mapToDouble(getValue)
            .reduce(0, Double::sum);
        double teamStat = teamStatSum / championList.size() / valueScale * CHAMPION_STAT_OUTPUT_SCALE;
        return championList.isEmpty() ? 0 : teamStat;
    }
}
