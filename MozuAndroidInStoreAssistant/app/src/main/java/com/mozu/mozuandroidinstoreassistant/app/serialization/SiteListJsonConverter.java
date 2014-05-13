package com.mozu.mozuandroidinstoreassistant.app.serialization;

import com.crashlytics.android.Crashlytics;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.mozu.api.contracts.tenant.Site;
import com.mozu.api.utils.JsonUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SiteListJsonConverter {

    public String getSitesJsonFromListOfSites(List<Site> sites) {
        ObjectMapper mapper = JsonUtils.initObjectMapper();
        String json = "";

        try {
            json = mapper.writeValueAsString(sites);
        } catch (JsonProcessingException e) {
            Crashlytics.logException(e);
        }

        return json;
    }

    public List<Site> getSitesFromJson(String siteJson) {
        ObjectMapper mapper = JsonUtils.initObjectMapper();

        List<Site> sitesList = null;
        try {
            sitesList = mapper.readValue(siteJson, TypeFactory.defaultInstance().constructCollectionType(ArrayList.class, Site.class));
        } catch (IOException e) {
            Crashlytics.logException(e);
        }

        return sitesList;
    }

}
