package com.mozu.mozuandroidinstoreassistant.app.serialization;

import com.crashlytics.android.Crashlytics;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.mozu.api.security.Scope;
import com.mozu.api.utils.JsonUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TenantListJsonConverter {

    public String getTenantJsonFromListOfTenants(List<Scope> tenants) {
        ObjectMapper mapper = JsonUtils.initObjectMapper();
        String json = "";

        try {
            json = mapper.writeValueAsString(tenants);
        } catch (JsonProcessingException e) {
            Crashlytics.logException(e);
        }

        return json;
    }

    public List<Scope> getTenantsFromJson(String tenantJson) {
        ObjectMapper mapper = JsonUtils.initObjectMapper();

        List<Scope> prefsList = null;
        try {
            prefsList = mapper.readValue(tenantJson, TypeFactory.defaultInstance().constructCollectionType(ArrayList.class, Scope.class));
        } catch (IOException e) {
            Crashlytics.logException(e);
        }

        return prefsList;
    }

}
