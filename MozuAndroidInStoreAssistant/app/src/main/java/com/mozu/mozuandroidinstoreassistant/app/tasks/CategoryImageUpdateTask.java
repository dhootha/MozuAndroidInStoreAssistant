package com.mozu.mozuandroidinstoreassistant.app.tasks;

import android.os.AsyncTask;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mozu.api.ApiContext;
import com.mozu.api.ApiException;
import com.mozu.api.Headers;
import com.mozu.api.MozuApiContext;
import com.mozu.api.MozuConfig;
import com.mozu.api.MozuUrl;
import com.mozu.api.Version;
import com.mozu.api.contracts.productadmin.Category;
import com.mozu.api.contracts.productadmin.CategoryLocalizedImage;
import com.mozu.api.contracts.productruntime.Product;
import com.mozu.api.contracts.tenant.Tenant;
import com.mozu.api.resources.commerce.catalog.admin.CategoryResource;
import com.mozu.api.resources.commerce.catalog.storefront.ProductResource;
import com.mozu.api.contracts.productruntime.ProductCollection;
import com.mozu.api.resources.platform.TenantResource;
import com.mozu.api.security.AppAuthenticator;
import com.mozu.api.security.AuthTicket;
import com.mozu.api.security.AuthenticationScope;
import com.mozu.api.security.CustomerAuthenticator;
import com.mozu.api.security.UserAuthenticator;
import com.mozu.api.utils.HttpHelper;
import com.mozu.api.utils.JsonUtils;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CategoryImageUpdateTask extends AsyncTask<Integer,Void,Void> {

    public static final String FILTER_BY = "categoryId eq ";
    public static final String SORT_BY = "productname asc";

    private Integer mTenantId;
    private  Integer mSiteId;
    public  CategoryImageUpdateTask(Integer tenantId, Integer siteId){
        mTenantId = tenantId;
        mSiteId = siteId;
    }
    @Override
    protected Void doInBackground(Integer... categoryIds) {
        Integer categoryId = categoryIds[0];
        CategoryResource categoryResource = new CategoryResource(new MozuApiContext(mTenantId,mSiteId));
        Category category = null;
        try {
            category = categoryResource.getCategory(categoryId);
            Category tempCat = category;
            while(tempCat.getChildCount() >0 ){
                tempCat = categoryResource.getChildCategories(categoryId).getItems().get(0);
            }

            ProductResource productResource = new ProductResource(new MozuApiContext(mTenantId, mSiteId));
            ProductCollection productCollection = productResource.getProducts(FILTER_BY + String.valueOf(tempCat.getId()), 1, 1, SORT_BY, null);
            if (productCollection != null && !productCollection.getItems().isEmpty()) {
                Product product = productCollection.getItems().get(0);
                String imageUrl = product.getContent().getProductImages().get(0).getImageUrl();

                List<CategoryLocalizedImage> categoryImageList = category.getContent().getCategoryImages();
                if(categoryImageList.size() >0 ) {
                    categoryImageList.get(0).setImageUrl(imageUrl);
                }else{
                    categoryImageList = new ArrayList<CategoryLocalizedImage>();
                    CategoryLocalizedImage image = new CategoryLocalizedImage();
                    image.setImageUrl(imageUrl);
                    categoryImageList.add(image);
                }
                category.getContent().setCategoryImages(categoryImageList);
                updateCategory(new MozuApiContext(mTenantId,mSiteId),category, categoryId,null,null);
            }

        }catch (Exception e) {
            return null;
        }

        return null;
    }


    public com.mozu.api.contracts.productadmin.Category updateCategory(ApiContext apiContext,com.mozu.api.contracts.productadmin.Category category, Integer categoryId, Boolean cascadeVisibility, String responseFields) throws Exception
    {

        MozuUrl url = com.mozu.api.urls.commerce.catalog.admin.CategoryUrl.updateCategoryUrl(cascadeVisibility, categoryId, responseFields);
        String verb = "PUT";
        Class<?> clz = com.mozu.api.contracts.productadmin.Category.class;
        FakeMozuClient<com.mozu.api.contracts.productadmin.Category> mozuClient = new FakeMozuClient(clz);
        mozuClient.setVerb(verb);
        mozuClient.setResourceUrl(url);
        mozuClient.setBody(category);

        mozuClient.setContext(apiContext);
        mozuClient.executeRequest();
        return mozuClient.getResult();

    }


     class FakeMozuClient<TResult> {
        private final ObjectMapper mapper = JsonUtils.initObjectMapper();

         private HttpHost proxyHttpHost = HttpHelper.getProxyHost();

        private ApiContext apiContext = null;
        private String baseAddress = null;
        private HttpResponse httpResponseMessage = null;
        private String httpContent = null;
        private InputStream streamContent = null;
        private String verb = null;
        private MozuUrl resourceUrl = null;
        private HashMap<String, String> headers = new HashMap<String, String>();
        private final Class<TResult> responseType;

        public FakeMozuClient() {
            this.responseType = null;
        }

        public FakeMozuClient(Class<TResult> responseType) throws Exception {
            this.responseType = responseType;
        }

        public void setContext(ApiContext apiContext) {
            this.apiContext = apiContext;

            if (apiContext != null) {
                if (apiContext.getTenantId() > 0) {
                    addHeader(Headers.X_VOL_TENANT, String.valueOf(apiContext.getTenantId()));
                }

                if (apiContext.getSiteId() != null && apiContext.getSiteId() > 0) {
                    addHeader(Headers.X_VOL_SITE, String.valueOf(apiContext.getSiteId()));
                }

                if (apiContext.getMasterCatalogId() != null && apiContext.getMasterCatalogId() > 0) {
                    addHeader(Headers.X_VOL_MASTER_CATALOG, String.valueOf(apiContext.getMasterCatalogId()));
                }

                if (apiContext.getCatalogId() != null && apiContext.getCatalogId() > 0) {
                    addHeader(Headers.X_VOL_CATALOG, String.valueOf(apiContext.getCatalogId()));
                }

                if (apiContext.getLocale() != null) {
                    addHeader(Headers.X_VOL_LOCALE, String.valueOf(apiContext.getLocale()));
                }

                if (apiContext.getCurrency() != null) {
                    addHeader(Headers.X_VOL_CURRENCY, String.valueOf(apiContext.getCurrency()));
                }

            }
        }

        public void setBaseAddress(String baseAddress) {
            this.baseAddress = baseAddress;
        }

        public void addHeader(String header, String value) {
            headers.put(header, value);
        }

        public void setVerb(String verb) {
            this.verb = verb;
        }

        public void setResourceUrl(MozuUrl resourceUrl) {
            this.resourceUrl = resourceUrl;
        }

        public <TBody> void setBody(TBody body) throws JsonProcessingException {
            httpContent = mapper.writeValueAsString(body);
        }

        public void setBody(InputStream body) throws JsonProcessingException {
            streamContent = body;
        }

        public void setBody(String body) {
            httpContent = body;
        }

        public String getStringResult() throws Exception {
            return stringContent();
        }

        @SuppressWarnings("unchecked")
        public TResult getResult() throws Exception {
            TResult tResult = null;
            if (responseType != null) {
                String className = responseType.getName();
                if (className.equals(java.io.InputStream.class.getName())) {
                    tResult = (TResult) httpResponseMessage.getEntity().getContent();
                } else if (className.equals(com.fasterxml.jackson.databind.JsonNode.class.getName())) {
                    tResult = (TResult)deserializeJObject ();
                } else {
                    tResult = deserialize(getStringResult(), responseType);
                }
            }
            return tResult;
        }

        private JsonNode deserializeJObject() throws Exception {
            HttpEntity httpEntity = httpResponseMessage.getEntity();
            InputStream stream = (InputStream) httpEntity.getContent();
            return mapper.readTree(stream);
        }

        public HttpResponse getResponse() {
            return httpResponseMessage;
        }

        public BasicHttpEntityEnclosingRequest getRequest() throws Exception {
            return buildRequest();
        }

        protected void validateContext() throws Exception {
            if (resourceUrl.getLocation() == MozuUrl.UrlLocation.TENANT_POD) {
                if (apiContext == null || apiContext.getTenantId() <= 0)
                    throw new ApiException("TenantId is missing");

                if (StringUtils.isBlank(apiContext.getTenantUrl())) {
                    TenantResource tenantResource = new TenantResource();
                    Tenant tenant = tenantResource.getTenant(apiContext.getTenantId());

                    if (tenant == null)
                        throw new ApiException("Tenant " + apiContext.getTenantId() + " Not found");
                    baseAddress = HttpHelper.getUrl(tenant.getDomain());
                } else {
                    baseAddress = apiContext.getTenantUrl();
                }
            } else {
                AppAuthenticator appAuthenticator = AppAuthenticator.getInstance();
                if (appAuthenticator == null) {
                    throw new ApiException("Application has not been authorized to access Mozu.");
                } else if (StringUtils.isBlank(MozuConfig.getBaseUrl())) {
                    throw new ApiException("Authentication.Instance.BaseUrl is missing");
                }

                baseAddress = MozuConfig.getBaseUrl();
            }
        }

        public void executeRequest() throws Exception {
            validateContext();

            HttpClient client = new DefaultHttpClient();
            BasicHttpEntityEnclosingRequest request = buildRequest();
            URL url = new URL(baseAddress);
            String hostNm = url.getHost();
            int port = url.getPort();
            String sche = url.getProtocol();
            HttpHost httpHost = new HttpHost(hostNm, port, sche);

            if (proxyHttpHost != null && StringUtils.isNotBlank(proxyHttpHost.getHostName())) {
                client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxyHttpHost);
            }

            httpResponseMessage = client.execute(httpHost, request);
            HttpHelper.ensureSuccess(httpResponseMessage, mapper);
        }

        private TResult deserialize(String jsonString, Class<TResult> cls) throws Exception {
            return mapper.readValue(jsonString, cls);
        }

        private String stringContent() throws Exception {
            HttpEntity httpEntity = httpResponseMessage.getEntity();
            InputStream stream = (InputStream) httpEntity.getContent();
            return org.apache.commons.io.IOUtils.toString(stream, "UTF-8");
        }

        private BasicHttpEntityEnclosingRequest buildRequest() throws Exception {
            String url = baseAddress + resourceUrl.getUrl();
            BasicHttpEntityEnclosingRequest request = new BasicHttpEntityEnclosingRequest(verb, url);

            if (verb.equals("POST") || verb.equals("PUT")) {
                if (StringUtils.isNotBlank(httpContent)) {
                    org.apache.http.entity.StringEntity entity = new org.apache.http.entity.StringEntity(httpContent, "UTF-8");
                    request.setEntity(entity);
                } else if (this.streamContent != null) {
                    InputStreamEntity entity = new InputStreamEntity(this.streamContent, -1);
                    request.setEntity(entity);
                }
            }

            request.setHeader("Accept", "application/json");
            if (!headers.containsKey(Headers.CONTENT_TYPE)) {
                request.setHeader("Content-type", "application/json; charset=utf-8");
            }
            if (apiContext != null && apiContext.getUserAuthTicket() != null) {
                setUserClaims();
            }

            Iterator<Map.Entry<String, String>> i = headers.entrySet().iterator();
            while (i.hasNext()) {
                Map.Entry<String, String> header = i.next();
                request.addHeader(header.getKey(), header.getValue());
            }

            AppAuthenticator.addAuthHeader(request);
            request.addHeader(Headers.X_VOL_VERSION, Version.API_VERSION);

            return request;
        }

        private void setUserClaims() {
            AuthTicket newAuthTicket = null;
            if (apiContext.getUserAuthTicket().getScope() == AuthenticationScope.Customer)
                newAuthTicket = CustomerAuthenticator.ensureAuthTicket(apiContext.getUserAuthTicket());
            else
                newAuthTicket = UserAuthenticator.ensureAuthTicket(apiContext.getUserAuthTicket());
            if (newAuthTicket != null) {
                apiContext.getUserAuthTicket().setAccessToken(newAuthTicket.getAccessToken());
                apiContext.getUserAuthTicket().setAccessTokenExpiration(
                        newAuthTicket.getAccessTokenExpiration());
            }

            addHeader(Headers.X_VOL_USER_CLAIMS, apiContext.getUserAuthTicket().getAccessToken());
        }

        protected Map<String, String> getHeaders() {
            return headers;
        }
    }



}
