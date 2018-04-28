package com.minelife.gangs.client;

import com.google.common.collect.Sets;
import com.minelife.MLProxy;

import java.util.Set;
import java.util.UUID;

public class ClientProxy extends MLProxy  {

    public static Set<UUID> gangMembers = Sets.newTreeSet();

}
