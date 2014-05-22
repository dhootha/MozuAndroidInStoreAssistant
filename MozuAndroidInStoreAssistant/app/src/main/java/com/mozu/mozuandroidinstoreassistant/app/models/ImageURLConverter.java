package com.mozu.mozuandroidinstoreassistant.app.models;

import com.crashlytics.android.Crashlytics;
import com.mozu.api.contracts.tenant.Site;
import com.mozu.api.contracts.tenant.Tenant;
import com.mozu.api.resources.platform.TenantResource;
import com.mozu.api.security.AppAuthenticator;

public class ImageURLConverter {

    private String mSiteDomain;
    private String mHttpString;

    public ImageURLConverter(Integer tenantId, Integer siteId) {

        mSiteDomain = getSiteDomain(tenantId, siteId);
        mHttpString = (AppAuthenticator.isUseSSL()) ? "https:" : "http:";
    }

    public String getFullImageUrl(String imageUrl) {
        StringBuilder imageUrlStringBuffer;

        if (imageUrl.startsWith("//cdn")) {
            imageUrlStringBuffer = new StringBuilder(mHttpString).append(imageUrl);
        } else {
            imageUrlStringBuffer = new StringBuilder(mSiteDomain).append(imageUrl);
        }

        return imageUrlStringBuffer.toString();
    }

    private String getSiteDomain(Integer tenantId, Integer siteId) {
        String httpString = (AppAuthenticator.isUseSSL()) ? "https:" : "http:";

        TenantResource tenantResource = new TenantResource();
        Tenant tenant = null;
        String imageBaseUrl = null;
        try {
            tenant = tenantResource.getTenant(tenantId);
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
        if (tenant != null) {
            for (Site site : tenant.getSites()) {
                if (siteId.equals(site.getId())) {
                    imageBaseUrl = site.getDomain();
                    break;
                }
            }

        }

        StringBuilder siteDomainBuilder = new StringBuilder(httpString).append("//").append(imageBaseUrl);

        return siteDomainBuilder.toString();
    }
}
