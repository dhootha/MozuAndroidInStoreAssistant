package com.mozu.mozuandroidinstoreassistant.app.models;

import com.crashlytics.android.Crashlytics;
import com.mozu.api.contracts.tenant.Site;
import com.mozu.api.contracts.tenant.Tenant;
import com.mozu.api.resources.platform.TenantResource;
import com.mozu.api.security.AppAuthenticator;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachineProducer;

public class ImageURLConverter {

    private String mSiteDomain;
    private String mHttpString;

    public ImageURLConverter(Integer tenantId, Integer siteId,String siteDomain) {

        mSiteDomain = getCompleteSiteDomain(tenantId, siteId,siteDomain);
        mHttpString = (AppAuthenticator.isUseSSL()) ? "https:" : "http:";
    }

    public String getFullImageUrl(String imageUrl) {
        if (imageUrl == null) {
            return "";
        }

        StringBuilder imageUrlStringBuffer;

        if (imageUrl.startsWith("//cdn")) {
            imageUrlStringBuffer = new StringBuilder(mHttpString).append(imageUrl);
        } else {
            imageUrlStringBuffer = new StringBuilder(mSiteDomain).append(imageUrl);
        }

        return imageUrlStringBuffer.toString();
    }

    private String getCompleteSiteDomain(Integer tenantId, Integer siteId,String siteDomain) {
        String httpString = (AppAuthenticator.isUseSSL()) ? "https:" : "http:";
        StringBuilder siteDomainBuilder = new StringBuilder(httpString).append("//").append(siteDomain);
        return siteDomainBuilder.toString();
    }
}
