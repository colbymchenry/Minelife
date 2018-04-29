package com.minelife.gangs;

import com.google.common.collect.Lists;
import net.minecraft.util.text.TextFormatting;

import java.util.List;

public enum GangRole {

    TEENIE(TextFormatting.DARK_GRAY, GangPermission.DEPOSIT_MONEY, GangPermission.JOIN_FIGHTS),
    RUNNER(TextFormatting.DARK_GREEN, GangPermission.DEPOSIT_MONEY, GangPermission.JOIN_FIGHTS, GangPermission.TELEPORT_HOME, GangPermission.REGROUP),
    YOUNGER(TextFormatting.GREEN, GangPermission.OPEN_LOCKS, GangPermission.DEPOSIT_MONEY, GangPermission.JOIN_FIGHTS, GangPermission.TELEPORT_HOME, GangPermission.REGROUP, GangPermission.INVITE),
    ELDER(TextFormatting.GOLD, GangPermission.OPEN_LOCKS, GangPermission.DEPOSIT_MONEY, GangPermission.JOIN_FIGHTS, GangPermission.TELEPORT_HOME, GangPermission.REGROUP, GangPermission.SETHOME_DELHOME,
            GangPermission.TOGGLE_FRIENDLY_FIRE, GangPermission.LEVEL_UP, GangPermission.CHALLENGE, GangPermission.ACCEPT_CHALLENGES),
    OLDER(TextFormatting.RED, GangPermission.values()),
    OWNER(TextFormatting.DARK_RED, GangPermission.values());

    public List<GangPermission> permissions;
    public TextFormatting color;

    GangRole(TextFormatting color, GangPermission... permissions) {
        this.color = color;
        this.permissions = Lists.newArrayList(permissions);
    }
}
