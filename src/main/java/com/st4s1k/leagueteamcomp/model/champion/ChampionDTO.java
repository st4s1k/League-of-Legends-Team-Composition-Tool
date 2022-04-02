package com.st4s1k.leagueteamcomp.model.champion;

import com.google.gson.annotations.SerializedName;
import com.st4s1k.leagueteamcomp.model.enums.AdaptiveTypeEnum;
import com.st4s1k.leagueteamcomp.model.enums.AttackTypeEnum;
import com.st4s1k.leagueteamcomp.model.enums.ResourceEnum;
import com.st4s1k.leagueteamcomp.model.enums.RoleEnum;
import com.st4s1k.leagueteamcomp.model.interfaces.SlotItem;
import javafx.scene.image.Image;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.Optional;

@Getter
@ToString(onlyExplicitlyIncluded = true)
public class ChampionDTO implements SlotItem {
    @SerializedName("id")
    private Integer id;
    @SerializedName("key")
    private String key;
    @ToString.Include
    @SerializedName("name")
    private String name;
    @SerializedName("attributeRatings")
    private AttributeRatingsDTO attributeRatings;
    @SerializedName("roles")
    private List<RoleEnum> roles;
    @SerializedName("resource")
    private ResourceEnum resource;
    @SerializedName("attackType")
    private AttackTypeEnum attackType;
    @SerializedName("adaptiveType")
    private AdaptiveTypeEnum adaptiveType;
    @SerializedName("stats")
    private StatsDTO stats;
    @SerializedName("icon")
    private String iconUrl;

    private transient Image image;

    @Override
    public Optional<ChampionDTO> getChampion() {
        return Optional.of(this);
    }
}
