package com.mileagecalculator.fuelefficiency;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.mileagecalculator.R;
import com.mileagecalculator.database.PetrolDB;
import com.mileagecalculator.fuelefficiency.FuelEntry.SpinrAdapter;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

@SuppressWarnings({"WeakerAccess", "CanBeFinal"})
public class ServiceActivity extends AppCompatActivity implements OnClickListener {

    private EditText edt_current_reading, edt_next_reading;
    private TextView tv_date;
    private int day;
    private int month;
    private int year;
    private PetrolDB db;
    private SimpleDateFormat sdf;
    private Spinner spnr;
    static final int DATE_PICKER_ID = 1222;
    private String date, next_reading, current_reading, id_shr, currentDate, v_Id,
            v_name, v_model;
    private ArrayList<HashMap<String, String>> arrayHashList;
    private ArrayList<Utils> arrayList;
    private AdView mAdView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.service_layout);
        initializeView();
        SharedPreferences prefs = getSharedPreferences("PETROL", MODE_PRIVATE);
        id_shr = prefs.getString("id", null);
        getDatabase(id_shr);
    }

    private void initializeView() {
        db = new PetrolDB(getApplicationContext());
        TextView textView1 = (TextView) findViewById(R.id.textView1);
        tv_date = (TextView) findViewById(R.id.tv__service_Date);
        TextView tv_DateServiceNew = (TextView) findViewById(R.id.tv_DateServiceNew);
        TextView tv_CurrentServiceNew = (TextView) findViewById(R.id.tv_CurrentServiceNew);
        TextView tv_NewReadingService = (TextView) findViewById(R.id.tv_NewReadingService);
        tv_date.setOnClickListener(this);
        edt_current_reading = (EditText) findViewById(R.id.edt_current_Reading);
        edt_next_reading = (EditText) findViewById(R.id.edt_next_Reading);
        Button btn_submit = (Button) findViewById(R.id.btn_service_submit);
        btn_submit.setOnClickListener(this);
        Button btn_date = (Button) findViewById(R.id.btn_service_Date);
        btn_date.setOnClickListener(this);
        spnr = (Spinner) findViewById(R.id.spinner_service);
        Calendar cal = Calendar.getInstance();
        day = cal.get(Calendar.DAY_OF_MONTH);
        month = cal.get(Calendar.MONTH);
        year = cal.get(Calendar.YEAR);
        arrayHashList = new ArrayList<>();
        arrayList = new ArrayList<>();
        sdf = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
        currentDate = sdf.format(new Date());
        tv_date.setText(currentDate);
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

    @SuppressWarnings("deprecation")
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (R.id.btn_service_submit == id) {
            try {
                try {
                    Date d = sdf.parse(tv_date.getText().toString());
                    String dates = new SimpleDateFormat("dd-MMM-yyyy",
                            Locale.ENGLISH).format(d);
                    date = URLEncoder.encode(dates, "utf-8");
                    Log.e("Date", "" + date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                current_reading = URLEncoder.encode(edt_current_reading
                        .getText().toString(), "utf-8");
                next_reading = URLEncoder.encode(edt_next_reading.getText()
                        .toString(), "utf-8");

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            Animation anm;
            if (date == null) {
                anm = AnimationUtils.loadAnimation(ServiceActivity.this,
                        R.anim.shake);
                tv_date.setAnimation(anm);
                showToast("Enter Date");

            } else if (current_reading.equals("")) {
                anm = AnimationUtils.loadAnimation(ServiceActivity.this,
                        R.anim.shake);
                edt_current_reading.setAnimation(anm);
                showToast("Enter Reading");

            } else if (next_reading.equals("")) {
                anm = AnimationUtils.loadAnimation(ServiceActivity.this,
                        R.anim.shake);
                edt_next_reading.setAnimation(anm);
                showToast("Enter Next Reading");

            } else {
                double c = Double.parseDouble(current_reading);
                double n = Double.parseDouble(next_reading);
                if (c >= n) {
                    showToast("Enter Greater Reading");
                } else {

                    if (db.isExistVN(v_Id)) {

                        Utils u = new Utils();
                        u.setId(id_shr);
                        u.setVehcleId(v_Id);
                        u.setVehcleName(v_name);
                        u.setVehcleModel(v_model);
                        u.setDate(date);
                        u.setCurrent_Reading(current_reading);
                        u.setNext_Reading(next_reading);
                        db.updateRecord(u);
                        showToast("Service Record Update");
                        Intent intent = new Intent();
                        setResult(20, intent);
                        finish();
                        overridePendingTransition(R.anim.slide_in_left,
                                R.anim.slide_out_right);

                    } else {
                        Utils u = new Utils();
                        u.setId(id_shr);
                        u.setVehcleId(v_Id);
                        u.setVehcleName(v_name);
                        u.setVehcleModel(v_model);
                        u.setDate(date);
                        u.setCurrent_Reading(current_reading);
                        u.setNext_Reading(next_reading);
                        db.insertServiceRecord(u);
                        showToast("Service Record Insert");
                        Intent intent = new Intent();
                        setResult(20, intent);
                        finish();
                        overridePendingTransition(R.anim.slide_in_left,
                                R.anim.slide_out_right);
                    }
                }
            }
        } else if (R.id.btn_service_Date == id) {

            showDialog(DATE_PICKER_ID);

        } else if (R.id.tv__service_Date == id) {

            showDialog(DATE_PICKER_ID);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_PICKER_ID:
                return new DatePickerDialog(this, datePickerListener, year, month,
                        day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            date = selectedDay + "-" + (selectedMonth + 1) + "-" + selectedYear;
            date = parseDateToddMMyyyy(date);
            sdf = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
            currentDate = sdf.format(new Date());

            try {

                Date date_db = sdf.parse(currentDate);
                Date date_Str = sdf.parse(date);

                if (date_Str.equals(date_db)) {
                    tv_date.setText(date);

                } else if (date_Str.after(date_db) || date_Str.equals(date_db)) {
                    tv_date.setText(date);

                } else if (date_Str.before(date_db)) {
                    tv_date.setText("");
                    showToast("Invalid Date");
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
    };

    /* Change Date Formate */
    public String parseDateToddMMyyyy(String time) {
        String inputPattern = "dd-MM-yyyy";
        String outputPattern = "dd-MMM-yyyy";
        @SuppressLint("SimpleDateFormat") SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    /* get All Data From database */
    private void getDatabase(String id_shr2) {
        if (db.isExistRegisterId(id_shr2)) {
            arrayHashList = db.getRegistrationRecord(id_shr2);
            for (int i = 0; i < arrayHashList.size(); i++) {
                v_Id = arrayHashList.get(i).get("vehcle_id");
                v_name = arrayHashList.get(i).get("vehcle_name");
                v_model = arrayHashList.get(i).get("vehcle_model");
                Utils u = new Utils();
                u.setVehcleId(v_Id);
                u.setVehcleName(v_name);
                u.setVehcleModel(v_model);
                arrayList.add(u);
            }

            if (arrayHashList.size() > 0) {
                if (arrayList.size() > 0) {
                    for (int i = 0; i < arrayList.size(); i++) {
                        if (getIntent().getStringExtra("VehicleName").equals(arrayList.get(i).getVehcleName())) {
                            setVehcleSpinner(arrayList, i);
                            return;
                        }
                    }
                    setVehcleSpinner(arrayList, 0);
                }
            }
        }
    }

    /* Set the Name Of the Vehicle on Spinner And model */
    private void setVehcleSpinner(final ArrayList<Utils> arrayList2, int pos) {

        SpinrAdapter adapter = new SpinrAdapter(ServiceActivity.this,
                R.layout.custom_spinner_text, arrayList2);
        spnr.setAdapter(adapter);
        spnr.setSelection(pos);
        spnr.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View arg1,
                                       int pos, long arg3) {
                int position = (int) parent.getItemIdAtPosition(pos);
                v_Id = arrayList2.get(position).getVehcleId();
                v_name = arrayList2.get(position).getVehcleName();
                v_model = arrayList2.get(position).getVehcleModel();

                String old_reading = db.getLastValue(v_Id);
                if (old_reading.equals("")) {
                    edt_current_reading.setText("0");
                    edt_current_reading.setSelection(edt_current_reading.getText().length());
                } else {
                    edt_current_reading.setText(old_reading);
                    edt_current_reading.setSelection(edt_current_reading.getText().length());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
    }

    protected void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
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