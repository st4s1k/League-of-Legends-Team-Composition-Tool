package com.st4s1k.leagueteamcomp.model.champion.select;

import com.st4s1k.leagueteamcomp.model.champion.ChampionDTO;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

@ToString(callSuper = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class SummonerSlotDTO extends SlotDTO<SummonerDTO> {
    private SummonerSlotDTO(
        Function<SummonerDTO, Optional<ChampionDTO>> championGetter,
        BiConsumer<ChampionDTO, SlotDTO<SummonerDTO>> championSetter
    ) {
        super(championGetter, championSetter);
    }

    public static SummonerSlotDTO newSlot() {
        return of(new SummonerDTO());
    }

    public static SummonerSlotDTO of(SummonerDTO summoner) {
        SummonerSlotDTO championSlot = new SummonerSlotDTO(
            SummonerDTO::getChampion,
            (champion, slot) -> slot.getItem().orElseThrow().setChampion(champion)
        );
        championSlot.setItem(summoner);
        return championSlot;
    }
}
