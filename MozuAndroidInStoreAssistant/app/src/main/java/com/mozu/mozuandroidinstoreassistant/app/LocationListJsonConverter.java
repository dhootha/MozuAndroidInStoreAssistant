package com.mozu.mozuandroidinstoreassistant.app;

import com.crashlytics.android.Crashlytics;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.mozu.api.contracts.location.Location;
import com.mozu.api.utils.JsonUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LocationListJsonConverter {

    public String getJsonFromListOfLocations(List<Location> location) {
        ObjectMapper mapper = JsonUtils.initObjectMapper();
        String json = "";

        try {
            json = mapper.writeValueAsString(location);
        } catch (JsonProcessingException e) {
            Crashlytics.logException(e);
        }

        return json;
    }

    public List<Location> getLocationsFromJson(String locationJson) {
        ObjectMapper mapper = JsonUtils.initObjectMapper();

        List<Location> prefsList = null;
        try {
            prefsList = mapper.readValue(locationJson, TypeFactory.defaultInstance().constructCollectionType(ArrayList.class, Location.class));
        } catch (IOException e) {
            Crashlytics.logException(e);
        }

        return prefsList;
    }
}
