package com.udacity.stockhawk.ui;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StockHistoryDetailActivity extends AppCompatActivity {
    String[] months;
    String [] xAxisLabels;
    Cursor mStocksCursor;
    int mPosition;
    String symbol;
    String priceString;
    Float priceFloat;
    String history;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        months = new String[] {
                getString(R.string.January),
                getString(R.string.February),
                getString(R.string.March),
                getString(R.string.April),
                getString(R.string.May),
                getString(R.string.June),
                getString(R.string.July),
                getString(R.string.August),
                getString(R.string.September),
                getString(R.string.October),
                getString(R.string.November),
                getString(R.string.December),
        };
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        DecimalFormat dollarFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);

        Intent intent = getIntent();
        mPosition = intent.getExtras().getInt("Position");

        mStocksCursor = getContentResolver().query(Contract.Quote.URI, null,null,null,Contract.Quote.COLUMN_SYMBOL);
        int symbolIndex;
        String[] historyMonths = new String[0];
        int historyNumberOfMonths=0;

        if (mStocksCursor != null && mStocksCursor.moveToPosition(mPosition)) {
            symbolIndex = mStocksCursor.getColumnIndex(Contract.Quote.COLUMN_SYMBOL);
            symbol = mStocksCursor.getString(symbolIndex);

            history = mStocksCursor.getString(Contract.Quote.POSITION_HISTORY);
            priceFloat = mStocksCursor.getFloat(Contract.Quote.POSITION_PRICE);
            priceString = dollarFormat.format(priceFloat);
            historyMonths = history.split("\n");
            historyNumberOfMonths = historyMonths.length;
            xAxisLabels = new String[historyNumberOfMonths + 1];
        }

        if (mStocksCursor!=null) mStocksCursor.close();

        setContentView(R.layout.stock_history_detail);

        LineChart chart = (LineChart) findViewById(R.id.chart);
        TextView titleView = (TextView) findViewById(R.id.title);
        titleView.setText(symbol +" - " + priceString);

        List<Entry> entries = new ArrayList<>();
        if (historyNumberOfMonths>12) historyNumberOfMonths = 12;
        for (int i=historyNumberOfMonths-1; i >= 0; i--) {
            int monthInteger;
            int year;
            String[] historyOneMonth = historyMonths[i].split(",");
            entries.add(new Entry((float) historyNumberOfMonths - i, Float.valueOf(historyOneMonth[1])));
            Date date = new Date (Long.valueOf(historyOneMonth[0]));
            monthInteger = date.getMonth();
            year = date.getYear()-100;
            xAxisLabels[historyNumberOfMonths - i] = months[monthInteger] + "-" + year;
        }
        xAxisLabels[historyNumberOfMonths] = getString(R.string.Current); // Label for current
        entries.add(new Entry((float) historyNumberOfMonths, priceFloat));

        LineDataSet dataSet = new LineDataSet(entries, symbol);
        LineData lineData = new LineData(dataSet);
        dataSet.setLineWidth(4.0f);
        dataSet.setColor(R.color.graph_line);
        dataSet.setCircleColor(R.color.graph_line);
        dataSet.setValueTextSize(10.0f);

        IAxisValueFormatter formatter = new IAxisValueFormatter() {

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return xAxisLabels[(int) value];
            }
            public int getDecimalDigits() {  return 0; }
        };


        Description desc = new Description();
        desc.setText(getString(R.string.Stock_Prices) + " - " + historyNumberOfMonths + " " + getString(R.string.months));
        chart.setDescription(desc);

        XAxis xAxis = chart.getXAxis();
        chart.getAxisRight().setDrawGridLines(false);
        chart.getAxisRight().setDrawAxisLine(false);
        chart.getAxisLeft().setDrawGridLines(false);
        chart.getAxisLeft().setDrawAxisLine(true);

        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.getLegend().setEnabled(false);
        chart.getAxisRight().setDrawLabels(false);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(formatter);

        chart.setData(lineData);
        chart.invalidate(); // refresh
    }
}
