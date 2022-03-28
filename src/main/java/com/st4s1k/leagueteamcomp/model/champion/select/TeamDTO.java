package com.st4s1k.leagueteamcomp.model.champion.select;

import com.st4s1k.leagueteamcomp.model.champion.AttributeRatings;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class TeamDTO {

    private final List<SlotDTO> slots = List.of(
        new SlotDTO(),
        new SlotDTO(),
        new SlotDTO(),
        new SlotDTO(),
        new SlotDTO()
    );

    private final AttributeRatings attributeRatings = new AttributeRatings();

    public SlotDTO getSlot(int slotId) {
        return slots.get(slotId);
    }
}
