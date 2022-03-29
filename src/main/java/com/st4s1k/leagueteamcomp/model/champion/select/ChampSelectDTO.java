package com.st4s1k.leagueteamcomp.model.champion.select;

import com.st4s1k.leagueteamcomp.model.champion.Champion;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.st4s1k.leagueteamcomp.model.champion.select.TeamDTO.Team.ALLY_TEAM;
import static com.st4s1k.leagueteamcomp.model.champion.select.TeamDTO.Team.ENEMY_TEAM;

@Getter
@NoArgsConstructor
public class ChampSelectDTO {

    private final TeamDTO allyTeam = new TeamDTO(ALLY_TEAM);
    private final TeamDTO allyBanList = new TeamDTO(ALLY_TEAM);

    private final TeamDTO enemyTeam = new TeamDTO(ENEMY_TEAM);
    private final TeamDTO enemyBanList = new TeamDTO(ENEMY_TEAM);

    public List<Champion> getChampionPool() {
        return Stream.of(
                allyTeam.getSlots(),
                allyBanList.getSlots(),
                enemyTeam.getSlots(),
                enemyBanList.getSlots()
            )
            .flatMap(Collection::stream)
            .map(SlotDTO::getChampion)
            .flatMap(Optional::stream)
            .toList();
    }
}
