package com.st4s1k.leagueteamcomp.repository;

import com.st4s1k.leagueteamcomp.model.champion.Champion;
import com.st4s1k.leagueteamcomp.model.champion.Champions;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static java.util.Collections.emptySet;

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

    public Set<String> getAllChampionKeys() {
        return champions.thenApply(champions -> champions.getChampions().keySet())
            .getNow(emptySet());
    }

    public Optional<Champion> findChampionDataById(Integer championId) {
        return champions.thenApply(champions -> champions.getChampions().values().stream()
                .filter(championData -> championData.getId().equals(championId))
                .findFirst())
            .getNow(Optional.empty());
    }

    public Optional<Champion> findChampionDataByKey(String championKey) {
        return champions.thenApply(champions -> Optional.ofNullable(champions.getChampions().get(championKey)))
            .getNow(Optional.empty());
    }

    public Optional<Champion> findChampionDataByName(String championName) {
        return champions.thenApply(champions -> champions.getChampions().values().stream()
                .filter(championData -> championData.getName().equals(championName))
                .findFirst())
            .getNow(Optional.empty());
    }

    public boolean existsChampionDataByName(String championName) {
        return findChampionDataByName(championName).isPresent();
    }
}
