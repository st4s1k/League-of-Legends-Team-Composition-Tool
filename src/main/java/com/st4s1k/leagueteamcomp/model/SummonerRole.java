package com.st4s1k.leagueteamcomp.model;

public enum SummonerRole {
    TOP,
    JGL,
    MID,
    ADC,
    SUP;

    @Override
    public String toString() {
        return this.name();
    }
}
