package com.mozu.mozuandroidinstoreassistant.app;

import com.mozu.api.contracts.tenant.Tenant;

public interface TenantResourceAsyncListener {

    void retrievedTenant(Tenant tenant);

}

