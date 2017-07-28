package com.mileagecalculator.fuelefficiency;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.mileagecalculator.R;
import com.mileagecalculator.database.PetrolDB;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"WeakerAccess", "CanBeFinal"})
public class Registration extends AppCompatActivity implements OnClickListener, Constant {

    private EditText edt_vehclemodel, edt_vehclename;
    private Spinner spnnr_fuelType, spnnr_mileage;
    private String id_shr, vehclename, vieclemodel, fuelType, mileage;
    private PetrolDB db;
    private SQLiteDatabase sqliteDb;
    private RelativeLayout layout;
    private RuntimePermission runtimePermission;
    private AdView mAdView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_layout);
        initializeView();
    }

    /* initializeView */
    private void initializeView() {
        runtimePermission = new RuntimePermission(this);
        db = new PetrolDB(this);
        TextView tv_Registeration = (TextView) findViewById(R.id.tv_Registeration);
        TextView tv_TypeTank = (TextView) findViewById(R.id.tv_TypeTank);
        TextView tv_TypeViecle = (TextView) findViewById(R.id.tv_TypeViecle);
        edt_vehclename = (EditText) findViewById(R.id.edt_V_Name);
        edt_vehclemodel = (EditText) findViewById(R.id.edt_V_Model);
        spnnr_fuelType = (Spinner) findViewById(R.id.spnr_Tank);
        spnnr_mileage = (Spinner) findViewById(R.id.spnr_Mileage);
        Button btn_register = (Button) findViewById(R.id.btn_Registration);
        btn_register.setOnClickListener(this);
        addItemsOnSpinner();

        if (!runtimePermission.readPhoneState()) {
            runtimePermission.requestPermissionForReadPhoneState();
            return;
        }
        getDevideId();
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


    /* Get Device ID */
    @SuppressWarnings("deprecation")
    @SuppressLint("HardwareIds")
    private void getDevideId() {
        final TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(TELEPHONY_SERVICE);
        id_shr = tm.getDeviceId();
    }

    // Request Call Back Method To check permission is granted by user or not for MarshMallow
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case READ_PHONE_STATE_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getDevideId();
                } else {
                    showToast("Please enable permission of phone calls in device settings");
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (!runtimePermission.readPhoneState()) {
            runtimePermission.requestPermissionForReadPhoneState();
            return;
        }

        int id = v.getId();
        if (id == R.id.btn_Registration) {
            try {
                vehclename = URLEncoder.encode(edt_vehclename.getText().toString(), "utf-8");
                vieclemodel = URLEncoder.encode(edt_vehclemodel.getText().toString(), "utf-8");
                fuelType = URLEncoder.encode(spnnr_fuelType.getSelectedItem().toString(), "utf-8");
                mileage = URLEncoder.encode(spnnr_mileage.getSelectedItem().toString(), "utf-8");
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }
            Animation anm;
            if (vehclename.equals("")) {
                anm = AnimationUtils.loadAnimation(Registration.this, R.anim.shake);
                edt_vehclename.setAnimation(anm);
                showToast("Enter Vehicle Name");

            } else if (vieclemodel.equals("")) {
                anm = AnimationUtils.loadAnimation(Registration.this, R.anim.shake);
                edt_vehclemodel.setAnimation(anm);
                showToast("Enter Vehicle Model");

            } else if (fuelType.equals("")) {

                showToast("Select Fuel Type");

            } else if (mileage.equals("")) {

                showToast("Enter Mileage");

            } else {
                if (db.isExistVehcleName(vehclename)) {

                    showToast("Vehicle Already Exists");

                } else {
                    Utils u = new Utils();
                    u.setId(id_shr);
                    u.setVehcleName(vehclename);
                    u.setVehcleModel(vieclemodel);
                    u.setFuelType(fuelType);
                    u.setMileage(mileage);
                    db.insertRegistrationRecord(u);
                    showToast("Your Vehicle Registered Successfully");

                    SharedPreferences preferences = getSharedPreferences("PETROL", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("id", id_shr);
                    editor.apply();

                    Intent intent = new Intent(Registration.this, FuelEntry.class);
                    intent.putExtra(FuelEntry.Flag, getIntent().getStringExtra(FuelEntry.Flag));
                    intent.putExtra("VehicleName", vehclename);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            }
        }
    }

    /* Add Spinner Items */
    private void addItemsOnSpinner() {
        List<String> list = new ArrayList<>();
        list.add("Petrol");
        list.add("Diesel");
        list.add("Gasoline");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
                R.layout.custom_spinner_text, list);
        dataAdapter.setDropDownViewResource(R.layout.custom_spinner_text);
        spnnr_fuelType.setAdapter(dataAdapter);

        List<String> list2 = new ArrayList<>();
        list2.add("KM");
        list2.add("Miles");
        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<>(this,
                R.layout.custom_spinner_text, list2);
        dataAdapter2.setDropDownViewResource(R.layout.custom_spinner_text);
        spnnr_mileage.setAdapter(dataAdapter2);
    }

    /* Clear All Data */
    private void clearAll() {
        SharedPreferences shared = getSharedPreferences("PETROL", MODE_PRIVATE);
        shared.edit().remove("id").apply();
        shared.edit().remove("v_name").apply();
    }

    protected void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    /* Close Activity Show Dialog */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (!getIntent().getStringExtra(FuelEntry.Flag).equals("1")) {
            //clearAll();
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            return true;
        }

        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            AlertDialog.Builder alertBox = new AlertDialog.Builder(Registration.this);
            alertBox.setIcon(R.drawable.ic_exit_to_app_black_24dp);
            alertBox.setTitle("Are you sure you want to exit?");
            alertBox.setPositiveButton("Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            //clearAll();
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