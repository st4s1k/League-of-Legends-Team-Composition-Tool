package com.st4s1k.leagueteamcomp.model.champion.select;

import com.st4s1k.leagueteamcomp.model.champion.Champion;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Getter
@NoArgsConstructor
public class ChampSelectDTO {

    private final TeamDTO allyTeam = new TeamDTO();
    private final TeamDTO enemyTeam = new TeamDTO();

    public List<Champion> getChampionPool() {
        return Stream.concat(
            allyTeam.getSlots().stream().map(SlotDTO::getChampion).flatMap(Optional::stream),
            enemyTeam.getSlots().stream().map(SlotDTO::getChampion).flatMap(Optional::stream)
        ).toList();
    }
}
