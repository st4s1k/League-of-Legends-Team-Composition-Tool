package com.st4s1k.leagueteamcomp.model.champion.select;

import com.st4s1k.leagueteamcomp.model.champion.ChampionDTO;
import com.st4s1k.leagueteamcomp.model.interfaces.ChampionHolder;
import com.st4s1k.leagueteamcomp.model.interfaces.Clearable;
import com.st4s1k.leagueteamcomp.model.interfaces.ImageProvider;
import com.st4s1k.leagueteamcomp.model.interfaces.SlotItem;
import com.st4s1k.leagueteamcomp.utils.Resources;
import javafx.beans.Observable;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.Image;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;
import java.util.Optional;

@Data
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SummonerDTO implements SlotItem, ChampionHolder, Clearable {

    @ToString.Include
    @EqualsAndHashCode.Include
    private String summonerName;

    private int slotId = -1;

    private final List<SlotDTO<ChampionDTO>> championSuggestions = List.of(
        ChampionSlotDTO.newSlot(),
        ChampionSlotDTO.newSlot(),
        ChampionSlotDTO.newSlot(),
        ChampionSlotDTO.newSlot(),
        ChampionSlotDTO.newSlot()
    );

    private SimpleObjectProperty<ChampionDTO> selectedChampionProperty = new SimpleObjectProperty<>();

    @Override
    public Observable[] getObservables() {
        return new Observable[]{selectedChampionProperty};
    }

    @Override
    public Image getImage() {
        return getChampion().map(ImageProvider::getImage).orElse(Resources.EMPTY_SLOT_IMAGE);
    }

    @Override
    @ToString.Include
    @EqualsAndHashCode.Include
    public Optional<ChampionDTO> getChampion() {
        return Optional.ofNullable(selectedChampionProperty.get());
    }

    @Override
    public void setChampion(ChampionDTO champion) {
        selectedChampionProperty.set(champion);
    }

    @Override
    public void clear() {
        slotId = -1;
        selectedChampionProperty.set(null);
    }
}
