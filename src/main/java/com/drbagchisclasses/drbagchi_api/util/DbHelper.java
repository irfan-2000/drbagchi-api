package com.drbagchisclasses.drbagchi_api.util;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

public class DbHelper {

    /**
     * Adds parameter to MapSqlParameterSource only if value is not null and not empty (if string).
     */
    public static void addParameter(MapSqlParameterSource params, String name, Object value) {
        if (value != null) {
            if (value instanceof String && ((String) value).trim().isEmpty()) {
                // Ignore empty strings
                return;
            }
            params.addValue(name, value);
        }
    }
}
