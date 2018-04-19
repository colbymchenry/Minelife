package com.minelife.gangs;

import com.google.common.collect.Lists;

import java.util.List;

public enum GangRoles {

    TEENIE(GangPermissions.DEPOSIT_MONEY, GangPermissions.JOIN_FIGHTS),
    RUNNER(GangPermissions.DEPOSIT_MONEY, GangPermissions.JOIN_FIGHTS, GangPermissions.TELEPORT_HOME, GangPermissions.REGROUP),
    YOUNGER(GangPermissions.DEPOSIT_MONEY, GangPermissions.JOIN_FIGHTS, GangPermissions.TELEPORT_HOME, GangPermissions.REGROUP, GangPermissions.INVITE),
    ELDER(GangPermissions.DEPOSIT_MONEY, GangPermissions.JOIN_FIGHTS, GangPermissions.TELEPORT_HOME, GangPermissions.REGROUP, GangPermissions.SETHOME_DELHOME,
            GangPermissions.TOGGLE_FRIENDLY_FIRE, GangPermissions.LEVEL_UP, GangPermissions.CHALLENGE, GangPermissions.ACCEPT_CHALLENGES),
    OLDER(GangPermissions.values());

    public List<GangPermissions> permissions;

    GangRoles(GangPermissions... permissions) {
        this.permissions = Lists.newArrayList(permissions);
    }
}
