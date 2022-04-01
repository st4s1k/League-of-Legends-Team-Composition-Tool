package com.st4s1k.leagueteamcomp.model.javafx;

import com.st4s1k.leagueteamcomp.model.champion.select.SlotDTO;
import com.st4s1k.leagueteamcomp.model.interfaces.SlotItem;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;

public class LTCChampionListCell<T extends SlotItem> extends ListCell<SlotDTO<T>> {

    private final ImageView imageView = new ImageView();
    private final double imageSize;

    public LTCChampionListCell(double imageSize) {
        this.imageSize = imageSize;
    }

    @Override
    public void updateItem(SlotDTO<T> item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setGraphic(null);
        } else {
            imageView.setSmooth(true);
            imageView.setCache(true);
            imageView.setFitWidth(imageSize);
            imageView.setFitHeight(imageSize);
            imageView.setImage(item.getImage());
            setGraphic(imageView);
        }
    }
}
