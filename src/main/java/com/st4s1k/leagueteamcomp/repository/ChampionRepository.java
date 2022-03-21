package com.st4s1k.leagueteamcomp.repository;

import com.st4s1k.leagueteamcomp.model.champion.Champion;
import com.st4s1k.leagueteamcomp.model.champion.Champions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ChampionRepository {

    private static ChampionRepository INSTANCE;
    private final Champions champions;

    public static ChampionRepository getInstance() {
        return INSTANCE;
    }

    private ChampionRepository(Champions champions) {
        this.champions = champions;
    }

    public static void init(Champions champions) {
        if (INSTANCE == null) {
            INSTANCE = new ChampionRepository(champions);
        }
    }

    public List<String> getAllChampionKeys() {
        return new ArrayList<>(champions.getChampions().keySet());
    }

    public Optional<Champion> findChampionDataByKey(String championKey) {
        return Optional.ofNullable(champions.getChampions().get(championKey));
    }

    public Optional<Champion> findChampionDataByName(String championName) {
        return champions.getChampions().values().stream()
            .filter(championData -> championData.getName().equals(championName))
            .findFirst();
    }

    public boolean existsChampionDataByName(String championName) {
        return findChampionDataByName(championName).isPresent();
    }
}
