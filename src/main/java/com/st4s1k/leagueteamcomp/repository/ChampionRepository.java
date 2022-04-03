package com.st4s1k.leagueteamcomp.repository;

import com.st4s1k.leagueteamcomp.model.champion.ChampionDTO;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static java.util.Collections.emptyMap;

@Component
public class ChampionRepository {

    private static Map<String, ChampionDTO> champions;

    public static void init(Map<String, ChampionDTO> champions) {
        if (ChampionRepository.champions == null) {
            ChampionRepository.champions = champions;
        }
    }

    private Map<String, ChampionDTO> getChampionsMap() {
        return Optional.ofNullable(champions).orElse(emptyMap());
    }

    public Collection<ChampionDTO> getAllChampions() {
        return getChampionsMap().values();
    }

    public Set<String> getAllChampionKeys() {
        return getChampionsMap().keySet();
    }

    public Optional<ChampionDTO> findChampionById(Integer championId) {
        return getAllChampions().stream()
            .filter(championData -> championData.getId().equals(championId))
            .findFirst();
    }

    public Optional<ChampionDTO> findChampionByKey(String championKey) {
        return Optional.ofNullable(getChampionsMap().get(championKey));
    }

    public Optional<ChampionDTO> findChampionByName(String championName) {
        return getAllChampions().stream()
            .filter(championData -> championData.getName().equals(championName))
            .findFirst();
    }

    public boolean existsChampionByName(String championName) {
        return findChampionByName(championName).isPresent();
    }
}
