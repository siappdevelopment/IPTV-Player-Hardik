package com.iptvplayer.xtreamiptv.myiptvpro.Main.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AppInstallReceiver extends BroadcastReceiver {
    @Override
    public final void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        String schemeSpecificPart = intent.getData().getSchemeSpecificPart();
        if ("android.intent.action.PACKAGE_ADDED".equals(action)) {
            Intent intent1 = new Intent(context, ScanActivity.class);
            intent1.addFlags(268435456);
            intent1.putExtra("package_name", schemeSpecificPart);
            context.startActivity(intent1);
        } else if ("android.intent.action.PACKAGE_REMOVED".equals(action)) {
            Intent intent2 = new Intent(context, UnActivity.class);
            intent2.addFlags(268435456);
            intent2.putExtra("package_name", schemeSpecificPart);
            context.startActivity(intent2);
        }
    }
}
