package com.st4s1k.leagueteamcomp.model.champion.select;

import com.st4s1k.leagueteamcomp.model.champion.ChampionDTO;
import com.st4s1k.leagueteamcomp.model.interfaces.*;
import javafx.beans.Observable;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.Image;
import lombok.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static com.st4s1k.leagueteamcomp.utils.Resources.EMPTY_SLOT_IMAGE;
import static lombok.AccessLevel.PACKAGE;

@Data
@RequiredArgsConstructor(access = PACKAGE)
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class SlotDTO<T extends SlotItem> implements
    ChampionHolder,
    ImageProvider,
    ObservablesProvider,
    Clearable {

    @Getter(PACKAGE)
    private final SimpleObjectProperty<T> itemProperty = new SimpleObjectProperty<>();
    @Getter(PACKAGE)
    private final Function<T, Optional<ChampionDTO>> championGetter;
    @Getter(PACKAGE)
    private final BiConsumer<ChampionDTO, SlotDTO<T>> championSetter;

    @ToString.Include
    @EqualsAndHashCode.Include
    public Optional<T> getItem() {
        return Optional.ofNullable(itemProperty.get());
    }

    public void setItem(T value) {
        itemProperty.set(value);
    }

    @Override
    public Observable[] getObservables() {
        List<Observable> observables = new ArrayList<>();
        observables.add(itemProperty);
        getItem()
            .map(ObservablesProvider::getObservables)
            .map(Arrays::asList)
            .ifPresent(observables::addAll);
        return observables.toArray(Observable[]::new);
    }

    @Override
    public Image getImage() {
        return getItem().map(ImageProvider::getImage).orElse(EMPTY_SLOT_IMAGE);
    }

    @Override
    @EqualsAndHashCode.Include
    public Optional<ChampionDTO> getChampion() {
        return getItem().flatMap(championGetter);
    }

    @Override
    public void setChampion(ChampionDTO champion) {
        championSetter.accept(champion, this);
    }

    public boolean isEmpty() {
        return isChampionNotSelected();
    }
}
