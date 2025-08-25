package com.drbagchisclasses.drbagchi_api.util;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class SPHelper
{

    private final JdbcTemplate jdbcTemplate;

    public SPHelper(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Map<String, Object> executeSP(String procedureName, Map<String, Object> params) {
        SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName(procedureName);

        return call.execute(params);
    }
}
