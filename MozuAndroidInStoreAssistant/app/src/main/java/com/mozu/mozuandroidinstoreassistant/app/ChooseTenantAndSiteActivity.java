package com.mozu.mozuandroidinstoreassistant.app;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.mozu.api.contracts.tenant.Site;
import com.mozu.api.contracts.tenant.Tenant;
import com.mozu.api.security.Scope;
import com.mozu.mozuandroidinstoreassistant.app.fragments.SetDefaultFragment;
import com.mozu.mozuandroidinstoreassistant.app.fragments.SetDefaultFragmentListener;
import com.mozu.mozuandroidinstoreassistant.app.fragments.SiteFragment;
import com.mozu.mozuandroidinstoreassistant.app.fragments.SiteSelectionFragmentListener;
import com.mozu.mozuandroidinstoreassistant.app.fragments.TenantFragment;
import com.mozu.mozuandroidinstoreassistant.app.fragments.TenantSelectionFragmentListener;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachine;
import com.mozu.mozuandroidinstoreassistant.app.models.authentication.UserAuthenticationStateMachineProducer;
import com.mozu.mozuandroidinstoreassistant.app.tasks.RetrieveTenantAsyncTask;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class ChooseTenantAndSiteActivity extends Activity implements TenantResourceAsyncListener, TenantSelectionFragmentListener, SiteSelectionFragmentListener, SetDefaultFragmentListener, Observer {

    private static final String TENANT_FRAGMENT_TAG = "tenants";
    private static final String SITE_FRAGMENT_TAG = "sites";
    private static final String SET_DEFAULT_FRAGMENT_TAG = "set_default_tag";

    private TenantFragment mTenantFragment;
    private SiteFragment mSiteFragment;
    private SetDefaultFragment mSetDefaultFragment;

    private UserAuthenticationStateMachine mUserAuthStateMachine;

    private boolean mTenantOrSiteNotChosenAuto = false;
    private boolean isLaunchedFromSettings = false;
    public static final String LAUNCH_FROM_SETTINGS = "launchfromSettings";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_tenant_and_site);
        isLaunchedFromSettings = getIntent().getBooleanExtra(LAUNCH_FROM_SETTINGS,false);
        mUserAuthStateMachine = UserAuthenticationStateMachineProducer.getInstance(getApplicationContext());

        mUserAuthStateMachine.addObserver(this);

        if (mUserAuthStateMachine.getTenantId() != null && mUserAuthStateMachine.getSiteId() != null) {
            showTenantChooser();
        } else {
            updateAuthTicketToDefaults();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mUserAuthStateMachine.deleteObserver(this);

        super.onDestroy();
    }

    @Override
    public void tenantWasChosen(Scope chosenScope) {
        mUserAuthStateMachine.getAuthProfile().setActiveScope(chosenScope);
        mUserAuthStateMachine.updateScope(chosenScope);

        //hide tenant dialog
        if (mTenantFragment != null) {
            mTenantFragment.dismiss();
        }
    }

    private void updateAuthTicketToDefaults() {
        if (mUserAuthStateMachine.getCurrentUsersPreferences().getDefaultTenantId() == null || mUserAuthStateMachine.getCurrentUsersPreferences().getDefaultTenantId().equalsIgnoreCase("") || mUserAuthStateMachine.getCurrentUsersPreferences().getDefaultTenantId().equalsIgnoreCase("null")) {
            showTenantChooser();
            return;
        }

        Scope scope = new Scope();
        scope.setId(Integer.getInteger(mUserAuthStateMachine.getCurrentUsersPreferences().getDefaultTenantId()));

        mUserAuthStateMachine.getAuthProfile().setActiveScope(scope);
        mUserAuthStateMachine.updateScope(scope);
    }

    public void showTenantChooser() {
        //only show tenant chooser if there is more than one tenant
        if (mUserAuthStateMachine.getAuthProfile().getAuthorizedScopes().size() == 1) {
            tenantWasChosen(mUserAuthStateMachine.getAuthProfile().getAuthorizedScopes().get(0));
            return;
        }

        mTenantOrSiteNotChosenAuto = true;

        FragmentManager fragmentManger = getFragmentManager();
        mTenantFragment = (TenantFragment) fragmentManger.findFragmentByTag(TENANT_FRAGMENT_TAG);

        if (mTenantFragment == null) {

            List<Scope> tenants = mUserAuthStateMachine.getAuthProfile().getAuthorizedScopes();

            Collections.sort(tenants, new ScopeComparator());

            mTenantFragment = new TenantFragment();
            mTenantFragment.setTenants(tenants);
            mTenantFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogMozu);
            mTenantFragment.show(getFragmentManager(), TENANT_FRAGMENT_TAG);
        }
    }

    private class ScopeComparator implements Comparator<Scope> {

        @Override
        public int compare(Scope lhs, Scope rhs) {

            return lhs.getName().compareTo(rhs.getName());
        }

        @Override
        public boolean equals(Object object) {
            return false;
        }

    }

    private class SiteComparator implements Comparator<Site> {

        @Override
        public int compare(Site lhs, Site rhs) {

            return lhs.getName().compareTo(rhs.getName());
        }

        @Override
        public boolean equals(Object object) {
            return false;
        }

    }

    private void showSiteChooser(List<Site> sites) {
        //if only one site then dont show site chooser
        if (sites.size() == 1) {
            siteWasChosen(sites.get(0));

            return;
        }

        mTenantOrSiteNotChosenAuto = true;

        FragmentManager fragmentManger = getFragmentManager();
        mSiteFragment = (SiteFragment) fragmentManger.findFragmentByTag(SITE_FRAGMENT_TAG);

        if (mSiteFragment == null) {
            mSiteFragment = new SiteFragment();

            Collections.sort(sites, new SiteComparator());

            mSiteFragment.setSites(sites);
            mSiteFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogMozu);
            mSiteFragment.show(getFragmentManager(), SITE_FRAGMENT_TAG);
        }
    }

    @Override
    public void update(Observable observable, Object data) {

        if (observable instanceof UserAuthenticationStateMachine) {

            UserAuthenticationStateMachine stateMachine = (UserAuthenticationStateMachine)observable;

            if (!stateMachine.getCurrentUserAuthState().isAuthenticatedState() && stateMachine.getCurrentUserAuthState().isErrorState()) {

                startActivity(new Intent(this, LoginActivity.class));
                finish();
            }

            if (stateMachine.getCurrentUserAuthState().isAuthenticatedState() && stateMachine.getCurrentUserAuthState().isTenantSelectedState()) {

                new RetrieveTenantAsyncTask(this, stateMachine.getAuthProfile().getActiveScope(), this).execute();
            }

        }

    }

    @Override
    public void retrievedTenant(Tenant tenant) {
        if (tenant == null) {
            Toast.makeText(this, getString(R.string.tenant_unavailable_for_account), Toast.LENGTH_LONG).show();
            showTenantChooser();
            return;
        }

        showSiteChooser(tenant.getSites());
    }

    @Override
    public void siteWasChosen(Site chosenSite) {
        mUserAuthStateMachine.setCurrentSiteId(chosenSite);
        if (mSiteFragment != null) {
            mSiteFragment.dismiss();
        }
        showAskToSetDefaultDialog();
    }

    @Override
    public void siteNotChosenButExited() {
        showTenantChooser();
    }

    private void showAskToSetDefaultDialog() {

        if (isLaunchedFromSettings) {
            mUserAuthStateMachine.persistSiteTenantId();
            Intent mainIntent = new Intent(this, MainActivity.class);
            mainIntent.putExtra(MainActivity.LAUNCH_SETTINGS,true);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(mainIntent);
            finish();
            return;
        }
        if (!mTenantOrSiteNotChosenAuto) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        FragmentManager fragmentManger = getFragmentManager();
        mSetDefaultFragment = (SetDefaultFragment) fragmentManger.findFragmentByTag(SET_DEFAULT_FRAGMENT_TAG);

        if (mSetDefaultFragment == null) {

            mSetDefaultFragment = new SetDefaultFragment();
            mSetDefaultFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogMozu);
            mSetDefaultFragment.show(getFragmentManager(), SET_DEFAULT_FRAGMENT_TAG);
        }
    }

    @Override
    public void setChosenTenantSiteAsDefault() {
        mUserAuthStateMachine.persistSiteTenantId();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    public void doNotSetDefault() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
