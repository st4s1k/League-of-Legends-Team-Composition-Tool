package com.st4s1k.leagueteamcomp.model.champion.select;

import com.st4s1k.leagueteamcomp.model.champion.AttributeRatings;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class TeamDTO {
    enum Team {
        UNDEFINED,
        ALLY_TEAM,
        ENEMY_TEAM
    }

    private final List<SlotDTO> slots = List.of(
        new SlotDTO(),
        new SlotDTO(),
        new SlotDTO(),
        new SlotDTO(),
        new SlotDTO()
    );
    private Team team = Team.UNDEFINED;

    public TeamDTO(Team team) {
        this.team = team;
    }

    private final AttributeRatings attributeRatings = new AttributeRatings();

    public SlotDTO getSlot(int slotId) {
        return slots.get(slotId);
    }
}
