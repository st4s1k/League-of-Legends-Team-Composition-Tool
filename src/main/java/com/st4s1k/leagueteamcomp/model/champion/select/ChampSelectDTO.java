package com.st4s1k.leagueteamcomp.model.champion.select;

import com.st4s1k.leagueteamcomp.model.champion.ChampionDTO;
import com.st4s1k.leagueteamcomp.model.interfaces.Clearable;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.st4s1k.leagueteamcomp.model.enums.TeamSideEnum.ALLY_TEAM;
import static com.st4s1k.leagueteamcomp.model.enums.TeamSideEnum.ENEMY_TEAM;

@Getter
@NoArgsConstructor
public class ChampSelectDTO implements Clearable {

    private final TeamDTO allyTeam = new TeamDTO(ALLY_TEAM);
    private final TeamDTO enemyTeam = new TeamDTO(ENEMY_TEAM);

    public List<ChampionDTO> getChampionPool() {
        return Stream.of(
                allyTeam.getSlots().stream()
                    .map(SlotDTO::getChampion)
                    .flatMap(Optional::stream)
                    .toList(),
                enemyTeam.getSlots().stream()
                    .map(SlotDTO::getChampion)
                    .flatMap(Optional::stream)
                    .toList(),
                allyTeam.getBans().stream()
                    .map(SlotDTO::getChampion)
                    .flatMap(Optional::stream)
                    .toList(),
                enemyTeam.getBans().stream()
                    .map(SlotDTO::getChampion)
                    .flatMap(Optional::stream)
                    .toList()
            ).flatMap(Collection::stream)
            .toList();
    }

    public void clear() {
        allyTeam.clear();
        enemyTeam.clear();
    }
}
