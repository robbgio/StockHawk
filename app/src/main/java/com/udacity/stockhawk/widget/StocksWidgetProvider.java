package com.udacity.stockhawk.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.ui.MainActivity;


public class StocksWidgetProvider extends AppWidgetProvider {

    private static final String ACTION_DATA_UPDATED = "com.udacity.stockhawk.ACTION_DATA_UPDATED";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        for (int i = 0; i < appWidgetIds.length; ++i) {

            Intent intent = new Intent(context, StocksWidgetService.class);

            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_stocks);

            rv.setRemoteAdapter(appWidgetIds[i], R.id.widget_list, intent);

            rv.setEmptyView(R.id.widget_list, R.id.empty_view);

            Intent launchIntent = new Intent(context, MainActivity.class);

            PendingIntent pendingIntent = PendingIntent.getActivity(context,0,
                    launchIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            rv.setPendingIntentTemplate(R.id.widget_list, pendingIntent);
            rv.setOnClickPendingIntent(R.id.empty_view,pendingIntent);

            appWidgetManager.updateAppWidget(appWidgetIds[i], rv);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ACTION_DATA_UPDATED.equals(intent.getAction())) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                    new ComponentName(context, getClass()));
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list);
        }
        super.onReceive(context, intent);
    }
}
