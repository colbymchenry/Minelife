package com.minelife.gangs.client;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.minelife.MLProxy;
import com.minelife.gangs.GangRole;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ClientProxy extends MLProxy  {

    public static Map<UUID, GangRole> gangMembers = Maps.newHashMap();

}
