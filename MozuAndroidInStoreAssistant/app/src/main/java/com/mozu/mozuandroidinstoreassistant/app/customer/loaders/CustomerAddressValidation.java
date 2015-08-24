package com.mozu.mozuandroidinstoreassistant.app.customer.loaders;

import com.mozu.api.MozuApiContext;
import com.mozu.api.contracts.core.Address;
import com.mozu.api.contracts.customer.AddressValidationRequest;
import com.mozu.api.contracts.customer.AddressValidationResponse;
import com.mozu.api.resources.commerce.customer.AddressValidationRequestResource;

import rx.Observable;
import rx.Subscriber;

public class CustomerAddressValidation  {

    private Integer mTenantApi;
    private Integer mSiteId;

    public CustomerAddressValidation( Integer tenantApi, Integer siteId) {
        this.mTenantApi = tenantApi;
        this.mSiteId = siteId;
    }

    public Observable<AddressValidationResponse> getAddressValidationObservable(final Address address) {
        return Observable.create(new Observable.OnSubscribe<AddressValidationResponse>() {
            @Override
            public void call(Subscriber<? super AddressValidationResponse> subscriber) {
                if(mSiteId == null || mTenantApi == null) {
                    subscriber.onError(new Exception("Tenant and Site id must not be null"));
                }
                if(address == null) {
                    subscriber.onError(new Exception("Address must not be null"));

                } else {
                    AddressValidationRequest request = new AddressValidationRequest();
                    request.setAddress(address);
                    try {
                        AddressValidationResponse response = new AddressValidationRequestResource(new MozuApiContext(mTenantApi, mSiteId))
                                .validateAddress(request);
                        subscriber.onNext(response);
                        subscriber.onCompleted();
                    } catch (Exception e) {
                        subscriber.onError(e);
                    }
                }
            }
        });
    }

}
