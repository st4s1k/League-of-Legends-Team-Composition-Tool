package com.st4s1k.leagueteamcomp.model.champion.select;

import com.st4s1k.leagueteamcomp.model.champion.ChampionDTO;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

@ToString(callSuper = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class ChampionSlotDTO extends SlotDTO<ChampionDTO> {

    private ChampionSlotDTO(
        Function<ChampionDTO, Optional<ChampionDTO>> championGetter,
        BiConsumer<ChampionDTO, SlotDTO<ChampionDTO>> championSetter
    ) {
        super(championGetter, championSetter);
    }

    public static ChampionSlotDTO newSlot() {
        return new ChampionSlotDTO(Optional::ofNullable, (item, slot) -> slot.setItem(item));
    }

    public static ChampionSlotDTO of(ChampionDTO champion) {
        ChampionSlotDTO championSlot = new ChampionSlotDTO(Optional::ofNullable, (item, slot) -> slot.setItem(item));
        championSlot.setChampion(champion);
        return championSlot;
    }
}
