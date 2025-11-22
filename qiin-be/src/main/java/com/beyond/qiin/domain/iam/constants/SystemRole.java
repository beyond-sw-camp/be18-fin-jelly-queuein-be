package com.beyond.qiin.domain.iam.constants;

import java.util.List;

public final class SystemRole {

    private SystemRole() {}

    public static final List<String> SYSTEM_ROLES = List.of("MASTER", "ADMIN", "MANAGER", "GENERAL");
}
