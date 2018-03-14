package com.minelife.util.server;

import java.util.UUID;

public interface NameUUIDCallback {

    void callback(UUID id, String name, Object... objects);

}
