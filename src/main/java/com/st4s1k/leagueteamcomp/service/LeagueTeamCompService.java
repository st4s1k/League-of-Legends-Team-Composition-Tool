package com.st4s1k.leagueteamcomp.service;

import com.st4s1k.leagueteamcomp.model.champion.AttributeRatingsDTO;
import com.st4s1k.leagueteamcomp.model.champion.ChampionDTO;
import com.st4s1k.leagueteamcomp.repository.ChampionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.ToDoubleFunction;

@Service
@RequiredArgsConstructor
public class LeagueTeamCompService {

    private static final double CHAMPION_STAT_OUTPUT_SCALE = 10;

    private final ChampionRepository championRepository;

    public Set<String> getAllChampionKeys() {
        return championRepository.getAllChampionKeys();
    }

    public Optional<ChampionDTO> findChampionById(Integer championId) {
        return championRepository.findChampionById(championId);
    }

    public Optional<ChampionDTO> findChampionByKey(String championKey) {
        return championRepository.findChampionByKey(championKey);
    }

    public Optional<ChampionDTO> findChampionByName(String championName) {
        return championRepository.findChampionByName(championName);
    }

    public boolean existsChampionByName(String championName) {
        return championRepository.existsChampionByName(championName);
    }

    public double getChampionStatValue(
        List<? extends String> championList,
        ToDoubleFunction<AttributeRatingsDTO> getValue,
        double valueScale
    ) {
        double teamStatSum = championList.stream()
            .map(championRepository::findChampionByKey)
            .flatMap(Optional::stream)
            .map(ChampionDTO::getAttributeRatings)
            .mapToDouble(getValue)
            .reduce(0, Double::sum);
        double teamStat = teamStatSum / championList.size() / valueScale * CHAMPION_STAT_OUTPUT_SCALE;
        return championList.isEmpty() ? 0 : teamStat;
    }
}
