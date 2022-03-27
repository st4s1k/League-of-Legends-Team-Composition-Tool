package com.st4s1k.leagueteamcomp.repository;

import com.st4s1k.leagueteamcomp.model.champion.Champion;
import com.st4s1k.leagueteamcomp.model.champion.Champions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class ChampionRepository {

    private static ChampionRepository INSTANCE;
    private final CompletableFuture<Champions> champions;

    public static ChampionRepository getInstance() {
        return INSTANCE;
    }

    private ChampionRepository(CompletableFuture<Champions> champions) {
        this.champions = champions;
    }

    public static void init(CompletableFuture<Champions> champions) {
        if (INSTANCE == null) {
            INSTANCE = new ChampionRepository(champions);
        }
    }

    public List<String> getAllChampionKeys() {
        return champions
            .thenApply(Champions::getChampions)
            .thenApply(Map::keySet)
            .thenApply(ArrayList::new)
            .getNow(new ArrayList<>());
    }

    public Optional<Champion> findChampionDataByKey(String championKey) {
        return champions
            .thenApply(Champions::getChampions)
            .thenApply(map -> Optional.ofNullable(map.get(championKey)))
            .getNow(Optional.empty());
    }

    public Optional<Champion> findChampionDataByName(String championName) {
        return champions
            .thenApply(Champions::getChampions)
            .thenApply(Map::values)
            .thenApply(values -> values.stream()
                .filter(championData -> championData.getName().equals(championName))
                .findFirst())
            .getNow(Optional.empty());
    }

    public boolean existsChampionDataByName(String championName) {
        return findChampionDataByName(championName).isPresent();
    }
}
