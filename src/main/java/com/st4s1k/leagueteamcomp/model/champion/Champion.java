package com.st4s1k.leagueteamcomp.model.champion;

import com.google.gson.annotations.SerializedName;
import com.st4s1k.leagueteamcomp.model.champion.enums.AdaptiveType;
import com.st4s1k.leagueteamcomp.model.champion.enums.AttackType;
import com.st4s1k.leagueteamcomp.model.champion.enums.Resource;
import com.st4s1k.leagueteamcomp.model.champion.enums.Role;
import javafx.scene.image.Image;
import lombok.Data;

import java.util.List;

@Data
public class Champion {

    @SerializedName("id")
    private Long id;
    @SerializedName("key")
    private String key;
    @SerializedName("name")
    private String name;
    @SerializedName("attributeRatings")
    private AttributeRatings attributeRatings;
    @SerializedName("roles")
    private List<Role> roles;
    @SerializedName("resource")
    private Resource resource;
    @SerializedName("attackType")
    private AttackType attackType;
    @SerializedName("adaptiveType")
    private AdaptiveType adaptiveType;
    @SerializedName("stats")
    private Stats stats;
    @SerializedName("icon")
    private String iconUrl;

    private transient Image image;
}
