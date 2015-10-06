package com.mozu.mozuandroidinstoreassistant.app.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import com.mozu.mozuandroidinstoreassistant.app.R;

import java.util.List;

public class ContactIntentUtil {

    public static void launchEmailIntent(Context context, String email) {
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{email});
        if (canIntentBeHandled(context, intent))
            context.startActivity(Intent.createChooser(intent, context.getString(R.string.send_email)));
    }

    private static boolean canIntentBeHandled(Context context, Intent intent) {
        final PackageManager mgr;
        mgr = context.getPackageManager();
        List<ResolveInfo> apps = mgr.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return apps.size() > 0;
    }
}
