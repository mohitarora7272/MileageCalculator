package com.mileagecalculator.fuelefficiency;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@SuppressWarnings({"WeakerAccess", "CanBeFinal"})
public class FuelEntry extends AppCompatActivity implements OnClickListener {
    public static String Flag = "flag";
    private EditText edt_price, edt_new_reading, edt_fuel;

    private TextView tv_date;

    private String date;
    private String price;
    private String new_reading;
    private String old_reading;
    private String fuel_quantity;
    private String v_Id;
    private String ids;
    private String v_name;
    private String v_model;
    private String v_fuelType;
    private String v_mileageType;
    private String currentDate;
    private String day_Date, month_Date, year_Date;
    private String lastDate;

    private Calendar cal;

    private int day;
    private int month;
    private int year;

    float newreadf, oldreadf, fuelf, pricef, averagef, priceAvgf;
    static final int DATE_PICKER_ID = 1111;
    public Spinner spnr;
    private PetrolDB db;
    private SimpleDateFormat sdf;
    private ArrayList<HashMap<String, String>> arrayHashList;
    private ArrayList<Utils> arrayList;
    private AdView mAdView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fuelentry_layout);
        initializeView();
        SharedPreferences prefs = getSharedPreferences("PETROL", MODE_PRIVATE);
        String id_shr = prefs.getString("id", null);
        getDatabase(id_shr);
    }

    private void initializeView() {
        db = new PetrolDB(getApplicationContext());
        sdf = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
        tv_date = (TextView) findViewById(R.id.tv_Date);
        TextView textView1 = (TextView) findViewById(R.id.textView1);
        TextView tv_DateMain = (TextView) findViewById(R.id.tv_DateMain);
        TextView tv_OldReadingMain = (TextView) findViewById(R.id.tv_OldReadingMain);
        TextView tv_NewReadingMain = (TextView) findViewById(R.id.tv_NewReadingMain);
        TextView tv_FuelMain = (TextView) findViewById(R.id.tv_FuelMain);
        TextView tv_PriceMain = (TextView) findViewById(R.id.tv_PriceMain);
        tv_date.setOnClickListener(this);
        edt_price = (EditText) findViewById(R.id.edt_Price);
        edt_price.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(5, 2)});
        edt_new_reading = (EditText) findViewById(R.id.edt_New_Reading);
        edt_fuel = (EditText) findViewById(R.id.edt_Fuel);
        Button btn_submit = (Button) findViewById(R.id.btn_Submit);
        btn_submit.setOnClickListener(this);
        Button btn_date = (Button) findViewById(R.id.btn_Date);
        btn_date.setOnClickListener(this);
        spnr = (Spinner) findViewById(R.id.spinner_entry);
        cal = Calendar.getInstance();
        day = cal.get(Calendar.DAY_OF_MONTH);
        month = cal.get(Calendar.MONTH);
        year = cal.get(Calendar.YEAR);
        arrayHashList = new ArrayList<>();
        arrayList = new ArrayList<>();
        currentDate = sdf.format(new Date());
        tv_date.setText(currentDate);
        //showBannerAd();
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

    /* Show text after decimal with in digits */
    @SuppressWarnings({"SameParameterValue", "CanBeFinal"})
    private class DecimalDigitsInputFilter implements InputFilter {

        Pattern mPattern;

        DecimalDigitsInputFilter(int digitsBeforeZero,
                                 int digitsAfterZero) {
            mPattern = Pattern.compile("[0-9]{0," + (digitsBeforeZero - 1)
                    + "}+((\\.[0-9]{0," + (digitsAfterZero - 1)
                    + "})?)||(\\.)?");
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {

            Matcher matcher = mPattern.matcher(dest);
            if (!matcher.matches())
                return "";
            return null;
        }

    }

    @SuppressWarnings("deprecation")
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (R.id.btn_Submit == id) {

            try {

                try {
                    Date d = sdf.parse(tv_date.getText().toString());
                    String dates = new SimpleDateFormat("dd-MMM-yyyy",
                            Locale.ENGLISH).format(d);
                    date = URLEncoder.encode(dates, "utf-8");
                    cal.setTime(d);
                    int currentDaycmp = cal.get(Calendar.DAY_OF_MONTH);
                    int currentMonthcmp = cal.get(Calendar.MONTH);
                    int currentYearcmp = cal.get(Calendar.YEAR);
                    day_Date = String.valueOf(currentDaycmp);
                    month_Date = String.valueOf((currentMonthcmp + 1));
                    year_Date = String.valueOf(currentYearcmp);

                } catch (ParseException e) {
                    e.printStackTrace();
                }

                new_reading = URLEncoder.encode(edt_new_reading.getText().toString(), "utf-8");
                fuel_quantity = URLEncoder.encode(edt_fuel.getText().toString(), "utf-8");
                price = URLEncoder.encode(edt_price.getText().toString(), "utf-8");

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            Animation anm;
            if (date == null) {
                anm = AnimationUtils.loadAnimation(FuelEntry.this, R.anim.shake);
                tv_date.setAnimation(anm);
                showToast("Enter Date");

            } else if (new_reading.equals("")) {
                anm = AnimationUtils.loadAnimation(FuelEntry.this, R.anim.shake);
                edt_new_reading.setAnimation(anm);
                showToast("Enter Reading");

            } else if (new_reading.equals("0")) {
                anm = AnimationUtils.loadAnimation(FuelEntry.this, R.anim.shake);
                edt_new_reading.setAnimation(anm);
                showToast("Enter Reading");

            } else if (fuel_quantity.equals("")) {
                anm = AnimationUtils.loadAnimation(FuelEntry.this, R.anim.shake);
                edt_fuel.setAnimation(anm);
                showToast("Enter Fuel Quantity");

            } else if (price.equals("")) {
                anm = AnimationUtils.loadAnimation(FuelEntry.this, R.anim.shake);
                edt_price.setAnimation(anm);
                showToast("Enter Price");

            } else {
                old_reading = db.getLastValue(v_Id);
                String last_fuel = db.getLastFuel(v_Id);
                String lastNextReading = db.getServiceReading(v_Id);

                if (!lastNextReading.equals("")) {
                    float diffread = Float.parseFloat(lastNextReading) - Float.parseFloat(new_reading);
                    String messageStr;
                    if (diffread > 0 && diffread <= 100) {
                        messageStr = v_name + diffread
                                + " km away from next Service";
                        getNotification(messageStr);
                    } else if (diffread <= 0) {
                        diffread = diffread * -1;
                        messageStr = v_name + " " + "service is overdue by " + diffread
                                + " km";
                        getNotification(messageStr);
                    }
                }

                String average;
                String priceAvg;
                if (old_reading.equals("")) {
                    // old_reading = "N/A";
                    average = "N/A";
                    priceAvg = "N/A";
                    // newreadf = Float.parseFloat(new_reading);
                    // oldreadf = Float.parseFloat(old_reading);
                    // fuelf = Float.parseFloat(fuel_quantity);
                    // pricef = Float.parseFloat(price);
                    // averagef = (newreadf - oldreadf) / fuelf;
                    // priceAvgf = (averagef / pricef);

                    Utils u = new Utils();
                    u.setVehcleId(v_Id);
                    u.setId(ids);
                    u.setVehcleName(v_name);
                    u.setVehcleModel(v_model);
                    u.setFuelType(v_fuelType);
                    u.setMileage(v_mileageType);
                    u.setDate(date);
                    u.setNew_reading(new_reading);
                    u.setFuel(fuel_quantity);
                    u.setPrice(price);
                    u.setAverage(average);
                    u.setPriceAvg(priceAvg);

					/*Set New Changes Here */
                    u.setDay_Date(day_Date);
                    u.setMonth_Date(month_Date);
                    u.setYear_Date(year_Date);

                    db.insertRecord(u);
                    db.insertReports(u);
                    showToast("Record Inserted");

                    if (getIntent().getStringExtra(Flag).equals("1")) {

                        Intent intent = new Intent(FuelEntry.this, GraphViewShow.class);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                    } else {

                        Intent intent = new Intent();
                        intent.putExtra("G", "");
                        setResult(30, intent);
                        finish();
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    }
                } else {

                    newreadf = Float.parseFloat(new_reading);
                    oldreadf = Float.parseFloat(old_reading);
                    fuelf = Float.parseFloat(last_fuel);
                    pricef = Float.parseFloat(price);

                    averagef = (newreadf - oldreadf) / fuelf;
                    priceAvgf = averagef / pricef;

                    new_reading = Float.toString(newreadf);
                    //last_fuel = Float.toString(fuelf);
                    price = Float.toString(pricef);
                    average = Float.toString(averagef);
                    priceAvg = Float.toString(priceAvgf);

                    if (newreadf > oldreadf) {
                        Utils u = new Utils();
                        u.setVehcleId(v_Id);
                        u.setId(ids);
                        u.setVehcleName(v_name);
                        u.setVehcleModel(v_model);
                        u.setFuelType(v_fuelType);
                        u.setMileage(v_mileageType);
                        u.setDate(date);
                        u.setNew_reading(new_reading);
                        u.setFuel(fuel_quantity);
                        u.setPrice(price);
                        u.setAverage(average);
                        u.setPriceAvg(priceAvg);

						/*Set New Changes Here */
                        u.setDay_Date(day_Date);
                        u.setMonth_Date(month_Date);
                        u.setYear_Date(year_Date);

                        db.insertRecord(u);
                        db.insertReports(u);
                        showToast("Record Inserted");

                        if (getIntent().getStringExtra(Flag).equals("1")) {

                            Intent intent = new Intent(FuelEntry.this, GraphViewShow.class);
                            startActivity(intent);
                            finish();
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                        } else {

                            Intent intent = new Intent();
                            setResult(30, intent);
                            finish();
                            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                        }

                    } else {
                        showToast("Enter Greater Reading");
                    }
                }
            }

        } else if (R.id.btn_Date == id) {

            showDialog(DATE_PICKER_ID);

        } else if (R.id.tv_Date == id) {

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

			/* Add New Changes Here with Date */
            day_Date = String.valueOf(selectedDay);
            month_Date = String.valueOf((selectedMonth + 1));
            year_Date = String.valueOf(selectedYear);
            String lastmonth = String.valueOf((cal.get(Calendar.MONTH) + 1) - 2);

            if ((selectedMonth + 1) <= (cal.get(Calendar.MONTH) + 1) - 2) {
                tv_date.setText("");
                showToast("Invalid Date");
                return;
            }

            if (selectedYear <= (cal.get(Calendar.YEAR)) - 1) {
                tv_date.setText("");
                showToast("Invalid Date");
                return;
            }

            sdf = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
            currentDate = sdf.format(new Date());

            lastDate = db.getLastDate(v_Id);

            if (lastDate.equals("")) {
                lastDate = currentDate;
                tv_date.setText(date);

            } else {
                try {
                    Date date_db = sdf.parse(currentDate);
                    Date date_Str = sdf.parse(date);
                    Date last_dates = sdf.parse(lastDate);

                    if (date_Str.equals(date_db)) {
                        tv_date.setText(date);

                    } else if (date_Str.after(date_db)) {
                        tv_date.setText("");
                        showToast("Invalid Date");

                    } else if (date_Str.before(last_dates)) {
                        tv_date.setText("");
                        showToast("Invalid Date");

                    } else if (date_Str.after(last_dates)
                            || date_Str.equals(last_dates)) {
                        tv_date.setText(date);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    /* get All Data From database */
    private void getDatabase(String id_shr2) {
        if (db.isExistRegisterId(id_shr2)) {
            arrayHashList = db.getRegistrationRecord(id_shr2);
            for (int i = 0; i < arrayHashList.size(); i++) {
                v_Id = arrayHashList.get(i).get("vehcle_id");
                ids = arrayHashList.get(i).get("id");
                v_name = arrayHashList.get(i).get("vehcle_name");
                v_model = arrayHashList.get(i).get("vehcle_model");
                v_fuelType = arrayHashList.get(i).get("vehcle_fuel_type");
                v_mileageType = arrayHashList.get(i).get("vehcle_mileage_type");
                Utils u = new Utils();
                u.setVehcleId(v_Id);
                u.setId(ids);
                u.setVehcleName(v_name);
                u.setVehcleModel(v_model);
                u.setFuelType(v_fuelType);
                u.setMileage(v_mileageType);
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

        SpinrAdapter adapter = new SpinrAdapter(FuelEntry.this, R.layout.custom_spinner_text, arrayList2);
        spnr.setAdapter(adapter);
        spnr.setSelection(pos);
        spnr.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View arg1, int pos, long arg3) {
                int position = (int) parent.getItemIdAtPosition(pos);
                sdf = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
                currentDate = sdf.format(new Date());
                v_Id = arrayList2.get(position).getVehcleId();
                ids = arrayList2.get(position).getId();
                v_name = arrayList2.get(position).getVehcleName();
                v_model = arrayList2.get(position).getVehcleModel();
                v_fuelType = arrayList2.get(position).getFuelType();
                v_mileageType = arrayList2.get(position).getMileage();

                lastDate = db.getLastDate(v_Id);

                if (lastDate.equals("")) {
                    lastDate = currentDate;
                    tv_date.setText(lastDate);
                } else {
                    tv_date.setText(lastDate);
                }

                getLastValues(v_Id);

                old_reading = db.getLastValue(v_Id);
                if (old_reading.equals("")) {
                    edt_new_reading.setText("0");
                    edt_new_reading.setSelection(edt_new_reading.getText().length());
                } else {
                    edt_new_reading.setText(old_reading);
                    edt_new_reading.setSelection(edt_new_reading.getText().length());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }

    /* Get Last Price And Fuel Quantity */
    private void getLastValues(String v_Id) {
        try {
            price = URLDecoder.decode(db.getLastPrice(v_Id), "UTF-8");
            fuel_quantity = URLDecoder.decode(db.getLastFuel(v_Id), "UTF-8");
            if (price.equals("")) {
                edt_price.setText("");
            } else {
                edt_price.setText(price);
            }
            if (fuel_quantity.equals("")) {
                edt_fuel.setText("");
            } else {
                edt_fuel.setText(fuel_quantity);
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /* Set Spinner Adapter */
    @SuppressWarnings({"CanBeFinal", "UnusedParameters"})
    public static class SpinrAdapter extends BaseAdapter {
        Context c;
        ArrayList<Utils> list;

        SpinrAdapter(Context ctx, int simpleSpinnerItem,
                     ArrayList<Utils> arrayList2) {
            c = ctx;
            list = arrayList2;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int pos) {
            return pos;
        }

        @Override
        public long getItemId(int pos) {
            return pos;
        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final holder h1;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                h1 = new holder();
                convertView = inflater.inflate(R.layout.custom_text_spinner, null);
                h1.tv = (TextView) convertView.findViewById(R.id.textView_spinner);
                convertView.setTag(h1);

            } else {
                h1 = (holder) convertView.getTag();
            }

            try {
                h1.tv.setText(URLDecoder.decode(list.get(position)
                        .getVehcleName()
                        + " "
                        + "("
                        + list.get(position).getVehcleModel() + ")", "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return convertView;
        }
    }

    private static class holder {
        TextView tv;
    }

    /* Change Date Format */
    public String parseDateToddMMyyyy(String time) {
        String inputPattern = "dd-MM-yyyy";
        String outputPattern = "dd-MMM-yyyy";
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

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

    protected void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    @SuppressWarnings("deprecation")
    public void getNotification(String message) {
        long[] vibrate = {100, 200, 300, 400};
        final NotificationManager mgr = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, new Intent(this, GraphViewShow.class), 0);
        Notification.Builder builder = new Notification.Builder(this);
        builder.setAutoCancel(true);
        builder.setTicker(getString(R.string.app_name));
        builder.setContentTitle(message);
        //builder.setContentText(message);
        builder.setSmallIcon(R.drawable.mc_icon);
        builder.setContentIntent(pendingIntent);
        builder.setOngoing(false);
        builder.setNumber(100);
        builder.setVibrate(vibrate);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            builder.build();
        }

        Random rNum = new Random();
        int num = rNum.nextInt(100);
        Notification note = builder.getNotification();
        mgr.notify(num, note);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (getIntent().getStringExtra(Flag).equals("2")) {
            Intent intent = new Intent();
            setResult(30, intent);
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            return;
        }
        finish();
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