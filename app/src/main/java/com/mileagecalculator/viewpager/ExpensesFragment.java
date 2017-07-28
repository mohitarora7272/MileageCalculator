package com.mileagecalculator.viewpager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mileagecalculator.R;
import com.mileagecalculator.database.PetrolDB;
import com.mileagecalculator.fuelefficiency.Utils;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;


@SuppressLint("ValidFragment")
@SuppressWarnings({"WeakerAccess", "CanBeFinal", "UnusedAssignment"})
public class ExpensesFragment extends Fragment {
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
    private LinearLayout layout;
    private View rootView;
    private TextView tv_thisMonth, tv_lastMonth, tv_total;
    private TextView tv_NotFound;
    private String month;
    private String lastmonth;
    private String[] text = new String[]{"This month", "Last month", "Total"};

    /**
     * Colors to be used for the pie slices.
     */
    private static float[] hsv = new float[3];

    private static int[] COLORS = new int[]{Color.rgb(238, 61, 30),
            Color.rgb(140, 238, 30), Color.rgb(30, 140, 238)};
    private CategorySeries mSeries = new CategorySeries("");
    private DefaultRenderer mRenderer = new DefaultRenderer();
    private GraphicalView mChartView;

    public ExpensesFragment(String id_Vehicle) {
        this.id_Vehicle = id_Vehicle;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.totalfuelfragment, container,
                false);
        layout = (LinearLayout) rootView.findViewById(R.id.chart_TotalFuel);
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
        tv_thisMonth = (TextView) rootView.findViewById(R.id.tv_thisMonth);
        tv_thisMonth.setTextColor(Color.rgb(238, 61, 30));
        tv_lastMonth = (TextView) rootView.findViewById(R.id.tv_lastMonth);
        tv_lastMonth.setTextColor(Color.rgb(140, 238, 30));
        tv_total = (TextView) rootView.findViewById(R.id.tv_total);
        tv_total.setTextColor(Color.rgb(30, 140, 238));
        tv_NotFound = (TextView) rootView.findViewById(R.id.tv_NotFound);
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
    private void getDatabase(String id) {
        if (db.isExistRegisterId(id)) {
            arrayHashList = db.getRegistrationRecord(id);
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
    @SuppressLint("SetTextI18n")
    private void getReport(String id_Vehicle2) {
        if (db.isExistReport(id_Vehicle2)) {
            if (db.isExistThisMonthReport(month)) {
                arrayHashListThisMonth = db.getThisMonthRecord(month, id_Vehicle2);
                if (arrayHashListThisMonth != null) {
                    tv_thisMonth.setVisibility(View.VISIBLE);
                    tv_thisMonth.setText("■This Month");
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
                Log.e("ExpenseFragment", "No This Month Data Found");
            }

            if (db.isExistLastMonthReport(lastmonth)) {
                arrayHashListLastMonth = db.getLastMonthRecord(lastmonth, id_Vehicle2);
                if (arrayHashListLastMonth != null) {
                    tv_lastMonth.setVisibility(View.VISIBLE);
                    tv_lastMonth.setText("■Last Month");
                    for (int i = 0; i < arrayHashListLastMonth.size(); i++) {
                        Utils u = new Utils();
                        // u.setVehcleId(arrayHashListLastMonth.get(i).get("vehcle_id"));
                        // u.setDay_Date(arrayHashListLastMonth.get(i).get("day_date"));
                        // u.setMonth_Date(arrayHashListLastMonth.get(i).get("month_date"));
                        // u.setYear_Date(arrayHashListLastMonth.get(i).get("year_date"));
                        // u.setReading(arrayHashListLastMonth.get(i).get("reading"));
                        u.setFuel(arrayHashListLastMonth.get(i).get("fuel_report"));
                        u.setPrice(arrayHashListLastMonth.get(i).get("price_report"));
                        arrayListLastMonth.add(u);
                    }
                }
            } else {
                Log.e("ExpenseFragment", "No Last Month Data Found");
            }
            arrayHashListTotal = db.getTotalRecord(id_Vehicle2);
            if (arrayHashListTotal != null) {
                tv_total.setVisibility(View.VISIBLE);
                tv_total.setText("■Total");
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
            Log.e("ExpenseFragment", "No Data Found");
            tv_NotFound.setVisibility(View.VISIBLE);
            layout.setVisibility(View.GONE);
            tv_thisMonth.setVisibility(View.GONE);
            tv_lastMonth.setVisibility(View.GONE);
            tv_total.setVisibility(View.GONE);
        }

        getArrayListValue(arrayListThisMonth, arrayListLastMonth, arrayListTotal);
    }

    /* Get Array List Value for Show graph */
    private void getArrayListValue(ArrayList<Utils> arrayListThisMonth,
                                   ArrayList<Utils> arrayListLastMonth, ArrayList<Utils> arrayListTotal) {
        int total = 0;
        int totalSum = 0;
        int lastTotalSum = 0;

        if (arrayListThisMonth.size() > 0) {
            for (int i = 0; i < arrayListThisMonth.size(); i++) {
                String thisMonthValue = arrayListThisMonth.get(i).getPrice();
                Float f = Float.parseFloat(thisMonthValue);
                totalSum += (int) f.floatValue();
            }
            Log.e("ThisMonth=>", "Sum>>" + totalSum);
        }

        if (arrayListLastMonth.size() > 0) {
            for (int i = 0; i < arrayListLastMonth.size(); i++) {
                Float f = Float
                        .parseFloat(arrayListLastMonth.get(i).getPrice());
                lastTotalSum += (int) f.floatValue();
            }
            Log.e("LastMonth=>", "Sum>>" + lastTotalSum);
        }

        if (arrayListTotal.size() > 0) {
            for (int i = 0; i < arrayListTotal.size(); i++) {
                Float f = Float.parseFloat(arrayListTotal.get(i).getPrice());
                total += (int) f.floatValue();
            }
            Log.e("Total=>", "Sum>>" + total);
        }
        String thisMonthNew = String.valueOf(totalSum);
        String lastMonthNew = String.valueOf(lastTotalSum);
        String totalNew = String.valueOf(total);
        try {
            double thisMonthVal = Double.parseDouble(thisMonthNew);
            double lastMonthVal = Double.parseDouble(lastMonthNew);
            double totalVal = Double.parseDouble(totalNew);
            String lastMVal = String.valueOf(lastMonthVal);
            double[] val;
            if (lastMVal.equals("0.0")) {
                val = new double[]{thisMonthVal};
                showPieChart(val);
                tv_total.setVisibility(View.GONE);
                return;
            }

            double restVal = totalVal - (thisMonthVal + lastMonthVal);
            Log.e("RestValue=>", "Expenses>>" + restVal);
            String rest = String.valueOf(restVal);
            if (rest.equals("0.0")) {
                val = new double[]{thisMonthVal, lastMonthVal, totalVal};
                showPieChart(val);
                tv_total.setVisibility(View.VISIBLE);
            } else {
                val = new double[]{thisMonthVal, lastMonthVal, restVal};
                showPieChart(val);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("deprecation")
    private void showPieChart(double[] val2) {
        mRenderer.setZoomButtonsVisible(false);
        mRenderer.setStartAngle(180);
        mRenderer.setLabelsTextSize(25);
        mRenderer.setScale(1.2f);
        mRenderer.setShowLabels(false);
        mRenderer.setShowLegend(false);
        mRenderer.setLabelsColor(Color.rgb(255, 255, 255));
        mRenderer.setTextTypeface(Typeface.SANS_SERIF);
        mRenderer.setChartTitleTextSize(25);
        mRenderer.setLegendTextSize(25);
        mRenderer.setPanEnabled(false);
        mRenderer.setZoomEnabled(false);
        mRenderer.setFitLegend(true);
        mRenderer.setDisplayValues(true);
        mRenderer.setClickEnabled(false);
        if (mChartView == null) {
            for (int i = 0; i < val2.length; i++) {
                double values = val2[i];
                String txt = text[i];
                mSeries.add(txt, values);
                SimpleSeriesRenderer renderer = new SimpleSeriesRenderer();
                renderer.setColor(COLORS[(mSeries.getItemCount() - 1)
                        % COLORS.length]);
                mRenderer.addSeriesRenderer(renderer);
            }
            mChartView = ChartFactory.getPieChartView(getActivity(), mSeries,
                    mRenderer);
            layout.addView(mChartView, new LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        } else {
            mChartView.repaint();
            mChartView = ChartFactory.getPieChartView(getActivity(), mSeries,
                    mRenderer);
            layout.addView(mChartView, new LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        }
    }
}