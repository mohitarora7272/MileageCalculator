package com.mileagecalculator.viewpager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jjoe64.graphview.BarGraphView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewDataInterface;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewSeries.GraphViewSeriesStyle;
import com.jjoe64.graphview.ValueDependentColor;
import com.mileagecalculator.R;
import com.mileagecalculator.database.PetrolDB;
import com.mileagecalculator.fuelefficiency.Utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

@SuppressLint("ValidFragment")
@SuppressWarnings({"WeakerAccess", "UnusedAssignment", "RedundantCast"})
public class FuelPriceFragment extends Fragment {
    private PetrolDB db;
    private String id_Vehicle;
    private ArrayList<HashMap<String, String>> arrayHashList;
    private ArrayList<HashMap<String, String>> arrayHashListThisMonth;
    private ArrayList<HashMap<String, String>> arrayHashListLastMonth;
    private ArrayList<HashMap<String, String>> arrayHashListTotal;
    private ArrayList<Utils> arrayList;
    private ArrayList<Utils> arrayListThisMonth;
    private ArrayList<Utils> arrayListLastMonth;
    private ArrayList<Utils> arrayListTotal;
    private String month;
    private String lastmonth;
    private String monthThis, monthLast;
    private double thisMonth, lastMonth, totalMonth;
    private View rootView;
    private GraphViewData[] data;
    private TextView tv_NotFound;
    private LinearLayout layout;

    public FuelPriceFragment(String id_Vehicle) {
        this.id_Vehicle = id_Vehicle;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fuelpricefrgment, container, false);
        initializeView();
        return rootView;
    }

    /* initializeView */
    private void initializeView() {
        db = new PetrolDB(getActivity());
        SharedPreferences prefs = getActivity().getSharedPreferences("PETROL", Context.MODE_PRIVATE);
        String id = prefs.getString("id", null);
        Calendar cal = Calendar.getInstance();
        String day = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
        month = String.valueOf(cal.get(Calendar.MONTH) + 1);
        lastmonth = String.valueOf((cal.get(Calendar.MONTH) + 1) - 1);
        String year = String.valueOf(cal.get(Calendar.YEAR));
        tv_NotFound = (TextView) rootView.findViewById(R.id.tv_NotFound);
        layout = (LinearLayout) rootView.findViewById(R.id.charts);
        arrayHashList = new ArrayList<>();
        arrayHashListThisMonth = new ArrayList<>();
        arrayHashListLastMonth = new ArrayList<>();
        arrayHashListTotal = new ArrayList<>();
        arrayList = new ArrayList<>();
        arrayListThisMonth = new ArrayList<>();
        arrayListLastMonth = new ArrayList<>();
        arrayListTotal = new ArrayList<>();
        getReport(id_Vehicle);
        //getDatabase(id);
    }

    /* get All Data From database */
    private void getDatabase(String id_shr2) {
        if (db.isExistRegisterId(id_shr2)) {
            arrayHashList = db.getRegistrationRecord(id_shr2);
            for (int i = 0; i < arrayHashList.size(); i++) {
                Utils u = new Utils();
                u.setVehcleId(arrayHashList.get(i).get("vehcle_id"));
                arrayList.add(u);
            }

            for (int i = 0; i < arrayList.size(); i++) {
                id_Vehicle = arrayList.get(i).getVehcleId();
            }
            getReport(id_Vehicle);
        }
    }

    /* Get Vehicle Reports */
    private void getReport(String id_Vehicle2) {
        if (db.isExistReport(id_Vehicle2)) {
            if (db.isExistThisMonthReport(month)) {
                arrayHashListThisMonth = db.getThisMonthRecord(month, id_Vehicle2);
                if (arrayHashListThisMonth != null) {
                    for (int i = 0; i < arrayHashListThisMonth.size(); i++) {
                        Utils u = new Utils();
                        // u.setVehcleId(arrayHashListThisMonth.get(i).get("vehcle_id"));
                        // u.setDay_Date(arrayHashListThisMonth.get(i).get("day_date"));
                        // u.setMonth_Date(arrayHashListThisMonth.get(i).get("month_date"));
                        // u.setYear_Date(arrayHashListThisMonth.get(i).get("year_date"));
                        // u.setReading(arrayHashListThisMonth.get(i).get("reading"));
                        u.setFuel(arrayHashListThisMonth.get(i).get("fuel_report"));
                        u.setPrice(arrayHashListThisMonth.get(i).get("price_report"));
                        arrayListThisMonth.add(u);
                    }
                }
            } else {
                Log.e("FuelPrice", "No This Month Data Found");
            }
            if (db.isExistLastMonthReport(lastmonth)) {
                arrayHashListLastMonth = db.getLastMonthRecord(lastmonth, id_Vehicle2);
                if (arrayHashListLastMonth != null) {
                    for (int i = 0; i < arrayHashListLastMonth.size(); i++) {
                        Utils u = new Utils();
                        // u.setVehcleId(arrayHashListLastMonth.get(i).get("vehcle_id"));
                        // u.setDay_Date(arrayHashListLastMonth.get(i).get("day_date"));
                        // u.setMonth_Date(arrayHashListLastMonth.get(i).get("month_date"));
                        // u.setYear_Date(arrayHashListLastMonth.get(i).get("year_date"));
                        // u.setReading(arrayHashListLastMonth.get(i).get("reading"));
                        u.setFuel(arrayHashListLastMonth.get(i).get("fuel_report"));
                        u.setPrice(arrayHashListLastMonth.get(i)
                                .get("price_report"));
                        arrayListLastMonth.add(u);
                    }
                }
            } else {
                Log.e("FuelPrice", "No Last Month Data Found");
            }

            arrayHashListTotal = db.getTotalRecord(id_Vehicle2);
            if (arrayHashListTotal != null) {
                for (int i = 0; i < arrayHashListTotal.size(); i++) {
                    Utils u = new Utils();
                    // u.setVehcleId(arrayHashListTotal.get(i).get("vehcle_id"));
                    // u.setDay_Date(arrayHashListTotal.get(i).get("day_date"));
                    // u.setMonth_Date(arrayHashListTotal.get(i).get("month_date"));
                    // u.setYear_Date(arrayHashListTotal.get(i).get("year_date"));
                    // u.setReading(arrayHashListTotal.get(i).get("reading"));
                    u.setFuel(arrayHashListTotal.get(i).get("fuel_report"));
                    u.setPrice(arrayHashListTotal.get(i).get("price_report"));
                    arrayListTotal.add(u);
                }
            }
        } else {
            Log.e("FuelPrice", "No Data Found");
            tv_NotFound.setVisibility(View.VISIBLE);
            layout.setVisibility(View.GONE);
            //getActivity().invalidateOptionsMenu();
        }

        getArrayListValue(arrayListThisMonth, arrayListLastMonth, arrayListTotal);
    }

    /* Get Array List Value */
    private void getArrayListValue(ArrayList<Utils> arrayListThisMonth,
                                   ArrayList<Utils> arrayListLastMonth, ArrayList<Utils> arrayListTotal) {

        float totalSumP = 0;
        float totalSumFP = 0;

        float lastTotalSumP = 0;
        float lastTotalSumFP = 0;

        float totalP = 0;
        float totalFP = 0;

        if (arrayListThisMonth.size() > 0) {
            for (int i = 0; i < arrayListThisMonth.size(); i++) {
                Float f = Float
                        .parseFloat(arrayListThisMonth.get(i).getPrice());
                Float f1 = Float
                        .parseFloat(arrayListThisMonth.get(i).getFuel());
                totalSumP += (f / f1);
            }
            totalSumFP = (totalSumP / arrayListThisMonth.size());
            thisMonth = (double) totalSumFP;
            Log.e("Sum1=>", "thisMonth>>" + thisMonth);
        }
        if (arrayListLastMonth.size() > 0) {
            for (int i = 0; i < arrayListLastMonth.size(); i++) {
                Float f = Float
                        .parseFloat(arrayListLastMonth.get(i).getPrice());
                Float f1 = Float
                        .parseFloat(arrayListLastMonth.get(i).getFuel());
                lastTotalSumP += (f / f1);
            }
            lastTotalSumFP = (lastTotalSumP / arrayListLastMonth.size());
            lastMonth = (double) lastTotalSumFP;
            Log.e("Sum2=>", "lastMonth>>" + lastMonth);

        }
        if (arrayListTotal.size() > 0) {
            for (int i = 0; i < arrayListTotal.size(); i++) {
                Float f = Float.parseFloat(arrayListTotal.get(i).getPrice());
                Float f1 = Float.parseFloat(arrayListTotal.get(i).getFuel());
                totalP += (f / f1);
            }
            totalFP = (totalP / arrayListTotal.size());
            totalMonth = (double) totalFP;
            Log.e("Sum3=>", "Total>>" + totalMonth);
        }
        openChart();
    }

    /* Open Chart */
    private void openChart() {
        String[] mMonth = new String[]{"Last Month", "This Month", "Total Average"};
        GraphViewSeriesStyle seriesStyle = new GraphViewSeriesStyle();
        seriesStyle.setValueDependentColor(new ValueDependentColor() {

            @Override
            public int get(GraphViewDataInterface data) {

                return Color.rgb((int) (150 + ((data.getX() / 3) * 100)),
                        (int) (150 - ((data.getX() / 3) * 150)),
                        (int) (150 - ((data.getX() / 3) * 150)));
            }
        });
        GraphViewSeries series = new GraphViewSeries("Average", seriesStyle,
                new GraphViewData[]{new GraphViewData(0, 0d),
                        new GraphViewData(1, lastMonth),
                        new GraphViewData(2, 0d),
                        new GraphViewData(3, thisMonth),
                        new GraphViewData(4, 0d),
                        new GraphViewData(5, totalMonth),
                        new GraphViewData(6, 0d)});

        GraphView graphView = new BarGraphView(getActivity(), "Fuel Price Average Graph");
        graphView.addSeries(series);
        graphView.getGraphViewStyle().setTextSize(25);
        graphView.getGraphViewStyle().setNumVerticalLabels(5);
        graphView.getGraphViewStyle().setVerticalLabelsWidth(50);
        graphView.getGraphViewStyle().setNumHorizontalLabels(6);
        graphView.getGraphViewStyle().useTextColorFromTheme(getActivity());

        if (thisMonth > lastMonth || thisMonth > totalMonth) {
            graphView.setManualYAxisBounds(thisMonth + 2, 0);

        } else if (lastMonth > thisMonth || lastMonth > totalMonth) {
            graphView.setManualYAxisBounds(lastMonth + 2, 0);

        } else {
            graphView.setManualYAxisBounds(totalMonth + 2, 0);
        }

        graphView.setHorizontalLabels(mMonth);
        layout.addView(graphView);
    }
}