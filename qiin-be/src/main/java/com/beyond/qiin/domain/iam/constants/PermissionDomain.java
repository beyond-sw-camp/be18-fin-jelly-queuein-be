package com.beyond.qiin.domain.iam.constants;

import java.util.List;

public final class PermissionDomain {

    private PermissionDomain() {}

    public static final String IAM = "IAM";
    public static final String BOOKING = "BOOKING";
    public static final String INVENTORY = "INVENTORY";
    public static final String ACCOUNTING = "ACCOUNTING";
    public static final String ASSET = "ASSET";

    public static final List<String> PREFIXES =
            List.of(IAM + "_", BOOKING + "_", INVENTORY + "_", ACCOUNTING + "_", ASSET + "_");
}
