package com.mileagecalculator.fuelefficiency;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewSeries.GraphViewSeriesStyle;
import com.jjoe64.graphview.LineGraphView;
import com.mileagecalculator.R;
import com.mileagecalculator.database.PetrolDB;
import com.mileagecalculator.fuelefficiency.FuelEntry.SpinrAdapter;
import com.mileagecalculator.viewpager.StatisticsActivity;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;


@SuppressWarnings({"ResultOfMethodCallIgnored", "WeakerAccess", "CanBeFinal"})
public class GraphViewShow extends AppCompatActivity implements OnClickListener, Constant {

    private Button btn_Add;
    private Button btn_Up;
    private Button btn_share;
    private ListView lv;
    private LinearLayout linear, linearUpper;
    private RelativeLayout layout;
    private String v_Id;
    private String ids;
    private String v_name;
    private String v_mileageType;
    private String totalAvgStr;
    private String v_model;
    private PetrolDB db;
    private TextView tv_Info;
    private TextView tv_count;
    private TextView tv_average;
    private SQLiteDatabase sqlitedb;
    private ArrayList<HashMap<String, String>> arrayHashList;
    private ArrayList<Utils> arrayList, arrayList2;
    private String selectpath, coverpath;
    public static final int PIC_CROP = 2;
    private Bitmap pro_photo, bmp;
    private RuntimePermission runtimePermission;
    private AdView mAdView;

    @SuppressLint("SetTextI18n")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.graph_show);
        initializeView();
    }

    /* initializeView */
    @SuppressLint("SetTextI18n")
    private void initializeView() {
        db = new PetrolDB(getApplicationContext());
        runtimePermission = new RuntimePermission(this);
        btn_Add = (Button) findViewById(R.id.btn_Add);
        btn_Add.setOnClickListener(this);
        btn_share = (Button) findViewById(R.id.share);
        btn_share.setOnClickListener(this);
        lv = (ListView) findViewById(R.id.lv_Graph);
        linear = (LinearLayout) findViewById(R.id.graph_Show);
        linearUpper = (LinearLayout) findViewById(R.id.linearUpper);
        TextView textView1 = (TextView) findViewById(R.id.textView1);
        tv_Info = (TextView) findViewById(R.id.tv_InfoGraph);
        tv_count = (TextView) findViewById(R.id.count);
        tv_average = (TextView) findViewById(R.id.netavg);
        arrayHashList = new ArrayList<>();
        arrayList = new ArrayList<>();

        SharedPreferences prefs = getSharedPreferences("PETROL", MODE_PRIVATE);
        String id_shr = prefs.getString("id", null);
        getDatabase(id_shr);
        if (arrayList2 == null) {
            linear.setVisibility(View.GONE);
            tv_Info.setVisibility(View.VISIBLE);
            tv_Info.setText("Atlest Three Entries Required To View Graph");
        }

        showBannerAd();
    }

    // Show Banner Ads
    private void showBannerAd() {
        mAdView = (AdView) findViewById(R.id.adView);
        // For Testing Purpose
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                // Check the LogCat to get your test device ID
                .addTestDevice("BA4B473C2A586E01BDC375AA6AC98A7D")
                .build();
        mAdView.loadAd(new AdRequest.Builder().build());
        mAdView.setAdListener(new AdListener() {
            public void onAdLoaded() {
                Log.e("Banner", "onAdLoaded");
            }

            @Override
            public void onAdClosed() {
                Log.e("Banner", "onAdClosed");
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                Log.e("Banner", "onAdFailedToLoad>>" + errorCode);
            }

            @Override
            public void onAdLeftApplication() {
                Log.e("Banner", "onAdLeftApplication");
            }

            @Override
            public void onAdOpened() {
                Log.e("Banner", "onAdOpened");

            }
        });
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_Add) {

            showPopUp();

        } else if (id == R.id.share) {

            share();
        }
    }

    private void showPopUp() {
        PopupMenu popup = new PopupMenu(GraphViewShow.this, btn_Add);
        popup.getMenuInflater().inflate(R.menu.show_popup, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getTitle().equals("Fuel Entry")) {

                    Intent intent = new Intent(GraphViewShow.this, FuelEntry.class);
                    intent.putExtra(FuelEntry.Flag, "2");
                    intent.putExtra("VehicleName", v_name);
                    startActivityForResult(intent, 200);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                } else if (item.getTitle().equals("Service Entry")) {

                    Intent intent = new Intent(GraphViewShow.this, ServiceActivity.class);
                    intent.putExtra("VehicleName", v_name);
                    startActivityForResult(intent, 100);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                } else if (item.getTitle().equals("Add New Vehicle")) {

                    Intent intent = new Intent(GraphViewShow.this, Registration.class);
                    intent.putExtra(FuelEntry.Flag, "2");
                    startActivityForResult(intent, 400);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                } else if (item.getTitle().equals("Show All Records")) {

                    Intent intent = new Intent(GraphViewShow.this, ShowRecordsActivity.class);
                    startActivityForResult(intent, 500);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                } else {
                    Intent intent = new Intent(GraphViewShow.this, StatisticsActivity.class);
                    intent.putExtra("VehicleID", v_Id);
                    intent.putExtra("VehicleName", v_name);
                    intent.putExtra("VehicleModel", v_model);
                    startActivityForResult(intent, 300);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
                return true;
            }
        });
        popup.show();
    }

    // Share Screenshot...
    private void share() {
        if (!runtimePermission.checkPermissionForWriteExternalStorage()) {
            runtimePermission.requestPermissionForExternalStorage();
            return;
        }

        try {
            if (!linearUpper.isShown()) {
                return;
            }
            View v1 = linearUpper;
            v1.setDrawingCacheEnabled(true);
            Bitmap resultBitmap = Bitmap.createBitmap(v1.getDrawingCache(true));
            try {
                resultBitmap = Bitmap.createScaledBitmap(resultBitmap, v1.getMeasuredWidth(), v1.getMeasuredHeight(), false);
            } catch (Exception e) {
                e.printStackTrace();
            }
            v1.setDrawingCacheEnabled(false);

            final Bitmap finalResultBitmap = resultBitmap;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    shareRecord(GraphViewShow.this, finalResultBitmap, getString(R.string.app_name) + System.currentTimeMillis());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Share Bitmap Image
    public static void shareRecord(Context ctx, Bitmap bitmap, String fileName) {
        try {
            String pathofBmp = MediaStore.Images.Media.insertImage(ctx.getContentResolver(), bitmap, fileName, null);
            Uri bmpUri = Uri.parse(pathofBmp);

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Intent.EXTRA_STREAM, bmpUri);
            intent.putExtra(Intent.EXTRA_SUBJECT, fileName);
            intent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=" + ctx.getPackageName());
            intent.setType("image/png");
            ctx.startActivity(Intent.createChooser(intent, "Send via..."));
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Exception", "Exception>>>" + e.getMessage());
        }
    }

    // Request Call Back Method To check permission is granted by user or not for MarshMallow
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case WRITE_EXTERNAL_STORAGE_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    share();
                } else {
                    Toast.makeText(this, "Sorry you can't share record, Please enable permission first", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /* get All Data From database */
    private void getDatabase(String id_shr2) {
        if (db.isExistRegisterId(id_shr2)) {
            arrayHashList = db.getRegistrationRecord(id_shr2);
            for (int i = 0; i < arrayHashList.size(); i++) {
                Utils u = new Utils();
                u.setVehcleId(arrayHashList.get(i).get("vehcle_id"));
                u.setId(arrayHashList.get(i).get("id"));
                u.setVehcleName(arrayHashList.get(i).get("vehcle_name"));
                u.setVehcleModel(arrayHashList.get(i).get("vehcle_model"));
                u.setFuelType(arrayHashList.get(i).get("vehcle_fuel_type"));
                u.setMileage(arrayHashList.get(i).get("vehcle_mileage_type"));
                arrayList.add(u);
            }
            if (arrayList.size() > 0) {
                setVehcleSpinner(arrayList);
            }
        }
    }

    /* Set the Name Of the Vehicle on Spinner And model */
    private void setVehcleSpinner(final ArrayList<Utils> arrayList2) {
        Spinner spnnrGraph = (Spinner) findViewById(R.id.spinner_Graph);
        SpinrAdapter adapter = new SpinrAdapter(GraphViewShow.this,
                R.layout.custom_text_spinner, arrayList2);
        spnnrGraph.setAdapter(adapter);
        spnnrGraph.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View arg1,
                                       int pos, long arg3) {
                linear.removeAllViews();
                int position = (int) parent.getItemIdAtPosition(pos);
                v_Id = arrayList2.get(position).getVehcleId();
                v_mileageType = arrayList2.get(position).getMileage();
                v_name = arrayList2.get(position).getVehcleName();
                v_model = arrayList2.get(position).getVehcleModel();
                getRecordVehicle(v_Id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

    }

    /* Get Record According to Vehicle iD and show on Graph And List Also */
    @SuppressLint("SetTextI18n")
    public void getRecordVehicle(String v_Id) {
        if (db.isExistVehicleID(v_Id)) {
            arrayHashList = new ArrayList<>();
            arrayList2 = new ArrayList<>();
            arrayHashList = db.getLastFiveEntry(v_Id);
            for (int i = 0; i < arrayHashList.size(); i++) {
                Utils u = new Utils();
                u.setVehcleId(arrayHashList.get(i).get("vehcle_id"));
                u.setId(arrayHashList.get(i).get("id"));
                u.setVehcleName(arrayHashList.get(i).get("vehcle_name"));
                u.setVehcleModel(arrayHashList.get(i).get("vehcle_model"));
                u.setFuelType(arrayHashList.get(i).get("vehcle_fuel_type"));
                u.setMileage(arrayHashList.get(i).get("vehcle_mileage_type"));
                u.setDate(arrayHashList.get(i).get("date"));
                u.setNew_reading(arrayHashList.get(i).get("new_reading"));
                u.setFuel(arrayHashList.get(i).get("fuel"));
                u.setPrice(arrayHashList.get(i).get("price"));
                u.setAverage(arrayHashList.get(i).get("average"));
                u.setPriceAvg(arrayHashList.get(i).get("price_average"));
                arrayList2.add(u);
            }
            if (arrayList2.size() > 0) {
                showOnGraph(arrayList2);
                //showOnList(arrayList2);
            }

            if (arrayList2.size() >= 2) {

                linear.setVisibility(View.VISIBLE);
                tv_Info.setVisibility(View.GONE);
                btn_share.setVisibility(View.VISIBLE);

            } else {

                linear.setVisibility(View.GONE);
                tv_Info.setVisibility(View.VISIBLE);
                tv_Info.setText("Atleast Three Entries Required To View Graph");
                btn_share.setVisibility(View.INVISIBLE);

            }
        } else {
            linear.setVisibility(View.GONE);
            tv_Info.setVisibility(View.VISIBLE);
            tv_Info.setText("Atleast Three Entries Required To View Graph");
            tv_count.setText("N/A");
            tv_average.setText("N/A");
            btn_share.setVisibility(View.INVISIBLE);
        }
    }

    /* Show Value On Graph */
    @SuppressLint({"SetTextI18n", "NewApi"})
    private void showOnGraph(ArrayList<Utils> arrayList2) {
        tv_count.setText("Fuel Entries: " + arrayList2.size());
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH);
        String[] dates = new String[arrayList2.size()];
        GraphViewData[] data = new GraphViewData[arrayList2.size()];
        GraphViewData[] data_avg = new GraphViewData[arrayList2.size()];
        ArrayList<Double> list = new ArrayList<>();
        Collections.reverse(arrayList2);

        double total = 0;
        for (int i = 0; i < arrayList2.size(); i++) {
            try {
                String dateStr;
                // double ad = 0;
                dateStr = URLDecoder.decode(arrayList2.get(i).getDate(),
                        "UTF-8");

                Date d;
                try {
                    d = sdf.parse(dateStr);
                    String date2 = new SimpleDateFormat("dd-MMM",
                            Locale.ENGLISH).format(d);
                    String date1 = URLEncoder.encode(date2, "utf-8");

                    dates[i] = date1;
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                String avg = URLDecoder
                        .decode(arrayList2.get(i).getAverage(), "UTF-8");
                double c_avg;

                if (avg.equals("N/A")) {
                    avg = "0";
                    c_avg = Double.valueOf(avg);

                } else {

                    c_avg = Double.valueOf(avg);
                    // ad = Double.valueOf(arrayList2.get(i).getAverage());
                    list.add(c_avg);

                }
                data[i] = new GraphViewData(i, c_avg);
                total = total + c_avg / arrayList2.size();
                totalAvgStr = String.valueOf(total);

                if (totalAvgStr.length() >= 5) {

                    totalAvgStr = String.valueOf(total).substring(0, 5);

                } else {

                    totalAvgStr = String.valueOf(total);

                }

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        for (int i = 0; i < arrayList2.size(); i++) {

            data_avg[i] = new GraphViewData(i, total);
        }

        GraphView graphView = new LineGraphView(GraphViewShow.this, "");
        tv_average.setText("Net Avg: " + totalAvgStr + " " + v_mileageType
                + "/" + "L");
        GraphViewSeries series = new GraphViewSeries("Periodic Avg", new GraphViewSeriesStyle(Color.BLACK, 5), data);
        GraphViewSeries seriesSin = new GraphViewSeries("Net Avg : " + totalAvgStr, new GraphViewSeriesStyle(Color.RED, 5), data_avg);
        graphView.addSeries(series);
        graphView.addSeries(seriesSin);
        graphView.getGraphViewStyle().setNumVerticalLabels(5);
        graphView.getGraphViewStyle().setVerticalLabelsWidth(50);
        graphView.setHorizontalLabels(dates);
        ((LineGraphView) graphView).setDrawDataPoints(false);
        graphView.getGraphViewStyle().setTextSize(25);
        double max;
        double min;

        if (list.size() == 0) {
        } else {

            if (Objects.equals(Collections.max(list), Collections.min(list))) {
                max = Collections.max(list);
                min = 0.0;
            } else {
                max = Collections.max(list);
                min = Collections.min(list);
            }
            if (total <= min) {
                max = Collections.max(list);
                min = total - 100;
            }
            if (min > 2)
                min = min - 2;
            else
                min = 0;
            graphView.setManualYAxisBounds(max + 2, min);
        }
        linear.addView(graphView);
    }

    /* Show Value On List */
    @SuppressWarnings("unused")
    private void showOnList(ArrayList<Utils> arrayList22) {

        RecordAdapter rcAdapter = new RecordAdapter(this, R.layout.list_custom_value, arrayList22);
        lv.setAdapter(rcAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        initializeView();
    }

    @SuppressWarnings("unused")
    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        @SuppressWarnings("deprecation")
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    /* Close Activity Show Dialog */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            AlertDialog.Builder alertBox = new AlertDialog.Builder(GraphViewShow.this);
            alertBox.setIcon(R.drawable.ic_exit_to_app_black_24dp);
            alertBox.setTitle("Are you sure you want to exit?");
            alertBox.setPositiveButton("Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            finish();
                        }
                    });

            alertBox.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                        }
                    });

            alertBox.show();
        }
        return true;
    }

    @SuppressLint("NewApi")
    @Override
    protected void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    @SuppressLint("NewApi")
    @Override
    protected void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    @SuppressLint("NewApi")
    @Override
    protected void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }
}