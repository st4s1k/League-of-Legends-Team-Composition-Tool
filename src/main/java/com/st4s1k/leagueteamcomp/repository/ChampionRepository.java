package com.st4s1k.leagueteamcomp.repository;

import com.st4s1k.leagueteamcomp.model.champion.ChampionDTO;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static java.util.Collections.emptyMap;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class ChampionRepository {
    private static ChampionRepository INSTANCE;

    private static Map<String, ChampionDTO> champions;

    public static void init(Map<String, ChampionDTO> champions) {
        if (ChampionRepository.champions == null) {
            ChampionRepository.champions = champions;
        }
    }

    public static ChampionRepository getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ChampionRepository();
        }
        return INSTANCE;
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
