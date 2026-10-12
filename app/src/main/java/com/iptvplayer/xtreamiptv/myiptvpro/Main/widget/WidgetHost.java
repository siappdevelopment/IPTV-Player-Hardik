package com.iptvplayer.xtreamiptv.myiptvpro.Main.widget;

import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;

public class WidgetHost extends AppWidgetHost {
    public WidgetHost(Context context, int i) {
        super(context, i);
    }

    @Override // android.appwidget.AppWidgetHost
    protected AppWidgetHostView onCreateView(Context context, int i, AppWidgetProviderInfo appWidgetProviderInfo) {
        return new WidgetView(context);
    }
}
