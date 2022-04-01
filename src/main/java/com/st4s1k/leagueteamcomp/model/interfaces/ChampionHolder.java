package com.st4s1k.leagueteamcomp.model.interfaces;

import com.st4s1k.leagueteamcomp.model.champion.ChampionDTO;

import java.util.Optional;

public interface ChampionHolder {

    Optional<ChampionDTO> getChampion();

    void setChampion(ChampionDTO champion);

    default boolean isChampionSelected() {
        return getChampion().isPresent();
    }

    default boolean isChampionNotSelected() {
        return getChampion().isEmpty();
    }
}
