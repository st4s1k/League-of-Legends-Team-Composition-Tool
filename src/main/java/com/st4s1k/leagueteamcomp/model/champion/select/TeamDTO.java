package com.st4s1k.leagueteamcomp.model.champion.select;

import com.st4s1k.leagueteamcomp.model.champion.AttributeRatingsDTO;
import com.st4s1k.leagueteamcomp.model.champion.ChampionDTO;
import com.st4s1k.leagueteamcomp.model.enums.TeamSideEnum;
import com.st4s1k.leagueteamcomp.model.interfaces.ChampionProvider;
import com.st4s1k.leagueteamcomp.model.interfaces.Clearable;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Optional;

@Data
@NoArgsConstructor
public class TeamDTO implements Clearable {

    private final List<SlotDTO<SummonerDTO>> slots = List.of(
        SummonerSlotDTO.newSlot(),
        SummonerSlotDTO.newSlot(),
        SummonerSlotDTO.newSlot(),
        SummonerSlotDTO.newSlot(),
        SummonerSlotDTO.newSlot()
    );
    private final List<SlotDTO<ChampionDTO>> bans = List.of(
        ChampionSlotDTO.newSlot(),
        ChampionSlotDTO.newSlot(),
        ChampionSlotDTO.newSlot(),
        ChampionSlotDTO.newSlot(),
        ChampionSlotDTO.newSlot()
    );

    private TeamSideEnum teamSide = TeamSideEnum.UNDEFINED;

    public TeamDTO(TeamSideEnum teamSide) {
        this.teamSide = teamSide;
    }

    private AttributeRatingsDTO attributeRatings;

    public SlotDTO<SummonerDTO> getSlot(int slotId) {
        return slots.get(slotId);
    }

    public List<ChampionDTO> getChampions() {
        return slots.stream()
            .map(SlotDTO::getItem)
            .flatMap(Optional::stream)
            .map(ChampionProvider::getChampion)
            .flatMap(Optional::stream)
            .toList();
    }

    public List<ChampionDTO> getBannedChampions() {
        return bans.stream().map(SlotDTO::getItem).flatMap(Optional::stream).toList();
    }

    public List<Integer> getBannedChampionIds() {
        return bans.stream().map(SlotDTO::getItem).flatMap(Optional::stream).map(ChampionDTO::getId).toList();
    }

    @Override
    public void clear() {
        slots.forEach(SlotDTO::clear);
        bans.forEach(SlotDTO::clear);
        teamSide = TeamSideEnum.UNDEFINED;
    }
}
