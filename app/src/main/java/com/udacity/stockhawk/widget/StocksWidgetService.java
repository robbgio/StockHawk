package com.udacity.stockhawk.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by rgiordano on 12/22/2016.
 */

public class StocksWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new StocksRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}

class StocksRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    Context mContext;
    int mAppWidgetId;
    int mCount;
    Cursor mStocksCursor;
    private List<String> mWidgetStrings = new ArrayList<String>();
    private final DecimalFormat dollarFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);

    public StocksRemoteViewsFactory(Context context, Intent intent){
        mContext = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate() {
        mStocksCursor = mContext.getContentResolver().query(Contract.Quote.URI, null,null,null,null);
        mCount = mStocksCursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        String symbol="";
        Float price=0f;
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_item);

        if (mStocksCursor.moveToPosition(position)) {
            symbol = mStocksCursor.getString(mStocksCursor.getColumnIndex(Contract.Quote.COLUMN_SYMBOL));
            price = mStocksCursor.getFloat(mStocksCursor.getColumnIndex(Contract.Quote.COLUMN_PRICE));
        }
        rv.setTextViewText(R.id.widget_symbol, symbol);
        rv.setTextViewText(R.id.widget_price, dollarFormat.format(price));

        Intent launchIntent = new Intent();
        rv.setOnClickFillInIntent(R.id.widget_item, launchIntent);

        return rv;
    }

    @Override
    public int getCount() {
        return mCount;
    }
    @Override
    public void onDataSetChanged() {
        Log.d("Data Changed","Data Changed");
        if (mStocksCursor != null) {
            mStocksCursor.close();
        }
        mStocksCursor = mContext.getContentResolver().query(Contract.Quote.URI, null, null,
                null, Contract.Quote.COLUMN_SYMBOL);
        mCount = mStocksCursor.getCount();
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void onDestroy() {
        mWidgetStrings.clear();
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
