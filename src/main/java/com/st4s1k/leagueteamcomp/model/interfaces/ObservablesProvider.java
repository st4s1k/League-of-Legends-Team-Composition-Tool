package com.st4s1k.leagueteamcomp.model.interfaces;

import javafx.beans.Observable;

public interface ObservablesProvider {
    default Observable[] getObservables() {
        return new Observable[0];
    }
}
