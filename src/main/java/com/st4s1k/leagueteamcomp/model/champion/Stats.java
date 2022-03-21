package com.st4s1k.leagueteamcomp.model.champion;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class Stats {
    @SerializedName("health")
    private Stat health;
    @SerializedName("healthRegen")
    private Stat healthRegen;
    @SerializedName("mana")
    private Stat mana;
    @SerializedName("manaRegen")
    private Stat manaRegen;
    @SerializedName("armor")
    private Stat armor;
    @SerializedName("magicResistance")
    private Stat magicResistance;
    @SerializedName("attackDamage")
    private Stat attackDamage;
    @SerializedName("movespeed")
    private Stat moveSpeed;
    @SerializedName("acquisitionRadius")
    private Stat acquisitionRadius;
    @SerializedName("selectionRadius")
    private Stat selectionRadius;
    @SerializedName("pathingRadius")
    private Stat pathingRadius;
    @SerializedName("gameplayRadius")
    private Stat gameplayRadius;
    @SerializedName("criticalStrikeDamage")
    private Stat criticalStrikeDamage;
    @SerializedName("criticalStrikeDamageModifier")
    private Stat criticalStrikeDamageModifier;
    @SerializedName("attackSpeed")
    private Stat attackSpeed;
    @SerializedName("attackSpeedRatio")
    private Stat attackSpeedRatio;
    @SerializedName("attackCastTime")
    private Stat attackCastTime;
    @SerializedName("attackTotalTime")
    private Stat attackTotalTime;
    @SerializedName("attackDelayOffset")
    private Stat attackDelayOffset;
    @SerializedName("attackRange")
    private Stat attackRange;
    @SerializedName("aramDamageTaken")
    private Stat aramDamageTaken;
    @SerializedName("aramDamageDealt")
    private Stat aramDamageDealt;
    @SerializedName("aramHealing")
    private Stat aramHealing;
    @SerializedName("aramShielding")
    private Stat aramShielding;
    @SerializedName("urfDamageTaken")
    private Stat urfDamageTaken;
    @SerializedName("urfDamageDealt")
    private Stat urfDamageDealt;
    @SerializedName("urfHealing")
    private Stat urfHealing;
    @SerializedName("urfShielding")
    private Stat urfShielding;
}
