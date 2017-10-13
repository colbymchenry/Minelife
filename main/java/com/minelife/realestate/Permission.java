package com.minelife.realestate;

import com.google.common.collect.Lists;

import java.util.List;

public enum Permission {

    PVP(true),
    PVE(true),
    BREAK(false),
    PLACE(false),
    INTERACT(false),
    ESTATE_CREATION(false),
    EXPLOSION(true),
    FIRE_SPREAD(true),
    MONSTER_SPAWN(true),
    CREATURE_SPAWN(true),
    ENTER(false),
    EXIT(false),
    MODIFY_INTRO(false),
    MODIFY_OUTRO(false),
    HEAL(true),
    PLAYER_DEATH(true),
    CREATURE_DEATH(true),
    MONSTER_DEATH(true),
    FALL_DAMAGE(true),
    SELL(false),
    RENT(false),
    MODIFY_RENT_PERIOD(false),
    MODIFY_PURCHASE_PRICE(false),
    MODIFY_RENT_PRICE(false);

    private boolean isEstatePermission = false;

    Permission(boolean isEstatePermission) {
        this.isEstatePermission = isEstatePermission;
    }

    public boolean isEstatePermission() {
        return isEstatePermission;
    }

    public static List<Permission> getEstatePermissions() {
        List<Permission> permissions = Lists.newArrayList();
        for (Permission permission : values())
            if(permission.isEstatePermission()) permissions.add(permission);
        return permissions;
    }
}
