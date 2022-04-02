package com.st4s1k.leagueteamcomp.model.interfaces;

import com.st4s1k.leagueteamcomp.model.champion.ChampionDTO;

public interface ChampionHolder extends ChampionProvider {
    void setChampion(ChampionDTO champion);
}
