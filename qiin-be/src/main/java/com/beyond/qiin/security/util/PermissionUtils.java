package com.beyond.qiin.security.util;

import com.beyond.qiin.domain.iam.constants.PermissionDomain;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class PermissionUtils {

    private PermissionUtils() {}

    public static Map<String, List<String>> groupPermissions(final List<String> permissions) {

        Map<String, List<String>> grouped = new HashMap<>();

        for (final String perm : permissions) {
            String domain = PermissionDomain.PREFIXES.stream()
                    .filter(perm::startsWith)
                    .findFirst()
                    .map(prefix -> prefix.replace("_", "")) // IAM_, BOOKING_ → IAM, BOOKING
                    .orElse("ETC");

            grouped.computeIfAbsent(domain, k -> new ArrayList<>()).add(perm);
        }

        return grouped;
    }
}
