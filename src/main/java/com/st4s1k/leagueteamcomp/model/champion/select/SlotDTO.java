package com.st4s1k.leagueteamcomp.model.champion.select;

import com.st4s1k.leagueteamcomp.LeagueTeamCompApplication;
import com.st4s1k.leagueteamcomp.model.champion.Champion;
import javafx.beans.Observable;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.Image;
import javafx.util.Callback;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Optional;

import static java.util.Objects.requireNonNull;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SlotDTO {
    private static final String DEFAULT_IMAGE_URL = "helmet_bro.png";
    private static final Image EMPTY_SLOT_IMAGE =
        new Image(requireNonNull(LeagueTeamCompApplication.class.getResourceAsStream(DEFAULT_IMAGE_URL)));

    private int slotId;

    @EqualsAndHashCode.Include
    private SimpleObjectProperty<Champion> championProperty = new SimpleObjectProperty<>();

    @EqualsAndHashCode.Include
    private String summonerName;

    public Optional<Champion> getChampion() {
        return Optional.ofNullable(championProperty.get());
    }

    public void setChampion(Champion champion) {
        this.championProperty.set(champion);
    }

    public Image getImage() {
        return getChampion().map(Champion::getImage).orElse(EMPTY_SLOT_IMAGE);
    }

    public boolean isFilled() {
        return getChampion().isPresent();
    }

    public void clear() {
        championProperty.set(null);
    }

    public static Callback<SlotDTO, Observable[]> extractor() {
        return param -> new Observable[]{param.championProperty, param.getImage().progressProperty()};
    }
}
