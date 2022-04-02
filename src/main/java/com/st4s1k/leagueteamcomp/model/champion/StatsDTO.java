package com.st4s1k.leagueteamcomp.model.champion;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

@Getter
public class StatsDTO {
    @SerializedName("health")
    private StatDTO health;
    @SerializedName("healthRegen")
    private StatDTO healthRegen;
    @SerializedName("mana")
    private StatDTO mana;
    @SerializedName("manaRegen")
    private StatDTO manaRegen;
    @SerializedName("armor")
    private StatDTO armor;
    @SerializedName("magicResistance")
    private StatDTO magicResistance;
    @SerializedName("attackDamage")
    private StatDTO attackDamage;
    @SerializedName("movespeed")
    private StatDTO moveSpeed;
    @SerializedName("acquisitionRadius")
    private StatDTO acquisitionRadius;
    @SerializedName("selectionRadius")
    private StatDTO selectionRadius;
    @SerializedName("pathingRadius")
    private StatDTO pathingRadius;
    @SerializedName("gameplayRadius")
    private StatDTO gameplayRadius;
    @SerializedName("criticalStrikeDamage")
    private StatDTO criticalStrikeDamage;
    @SerializedName("criticalStrikeDamageModifier")
    private StatDTO criticalStrikeDamageModifier;
    @SerializedName("attackSpeed")
    private StatDTO attackSpeed;
    @SerializedName("attackSpeedRatio")
    private StatDTO attackSpeedRatio;
    @SerializedName("attackCastTime")
    private StatDTO attackCastTime;
    @SerializedName("attackTotalTime")
    private StatDTO attackTotalTime;
    @SerializedName("attackDelayOffset")
    private StatDTO attackDelayOffset;
    @SerializedName("attackRange")
    private StatDTO attackRange;
    @SerializedName("aramDamageTaken")
    private StatDTO aramDamageTaken;
    @SerializedName("aramDamageDealt")
    private StatDTO aramDamageDealt;
    @SerializedName("aramHealing")
    private StatDTO aramHealing;
    @SerializedName("aramShielding")
    private StatDTO aramShielding;
    @SerializedName("urfDamageTaken")
    private StatDTO urfDamageTaken;
    @SerializedName("urfDamageDealt")
    private StatDTO urfDamageDealt;
    @SerializedName("urfHealing")
    private StatDTO urfHealing;
    @SerializedName("urfShielding")
    private StatDTO urfShielding;
}
