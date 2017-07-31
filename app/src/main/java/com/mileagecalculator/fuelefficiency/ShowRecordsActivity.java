package com.mileagecalculator.fuelefficiency;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.mancj.slideup.SlideUp;
import com.mancj.slideup.SlideUpBuilder;
import com.mileagecalculator.R;
import com.mileagecalculator.database.PetrolDB;

import java.util.ArrayList;
import java.util.HashMap;

import atownsend.swipeopenhelper.SwipeOpenItemTouchHelper;

public class ShowRecordsActivity extends AppCompatActivity implements SampleAdapter.ButtonCallbacks, View.OnClickListener {
    private RecyclerView mRecyclerView;
    private RecyclerView rVRecords;
    private PetrolDB db;
    private ArrayList<HashMap<String, String>> arrayHashList;
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private ArrayList<Utils> arrayList;
    private SwipeOpenItemTouchHelper helper;
    private SampleAdapter mAdapter;
    private TextView tv_notFound;
    private SlideUp slideUp;
    private View dim;
    private View sliderView;
    private AdView mAdView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_records);
        tv_notFound = (TextView) findViewById(R.id.notFound);
        sliderView = findViewById(R.id.slideView);
        dim = findViewById(R.id.dim);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycleRecords);
        rVRecords = (RecyclerView) findViewById(R.id.rVRecords);
        helper = new SwipeOpenItemTouchHelper(new SwipeOpenItemTouchHelper.SimpleCallback(
                SwipeOpenItemTouchHelper.START | SwipeOpenItemTouchHelper.END));

        db = new PetrolDB(getApplicationContext());
        SharedPreferences prefs = getSharedPreferences("PETROL", MODE_PRIVATE);
        String id_shr = prefs.getString("id", null);
        arrayHashList = new ArrayList<>();
        arrayList = new ArrayList<>();
        getDatabase(id_shr);
        initSlideView();

        if (savedInstanceState != null) {
            helper.restoreInstanceState(savedInstanceState);
        }

        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        helper.closeAllOpenPositions();
                    }
                })
        );

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

    private void initSlideView() {
        slideUp = new SlideUpBuilder(sliderView)
                .withListeners(new SlideUp.Listener.Events() {
                    @Override
                    public void onSlide(float percent) {
                        dim.setAlpha(1 - (percent / 100));
                    }

                    @Override
                    public void onVisibilityChanged(int visibility) {
                        if (visibility == View.GONE) {
                        }
                    }
                })
                .withStartGravity(Gravity.BOTTOM)
                .withLoggingEnabled(true)
                .withGesturesEnabled(true)
                .withStartState(SlideUp.State.HIDDEN)
                .build();
    }

    /* get All Data From database */
    private void getDatabase(String id_shr2) {
        if (db.isExistRegisterId(id_shr2)) {
            arrayHashList = db.getRegistrationRecord(id_shr2);
            for (int i = 0; i < arrayHashList.size(); i++) {
                Utils u = new Utils();
                u.setVehcleId(arrayHashList.get(i).get("vehcle_id"));
                u.setVehcleName(arrayHashList.get(i).get("vehcle_name"));
                u.setVehcleModel(arrayHashList.get(i).get("vehcle_model"));
                arrayList.add(u);
            }
        } else {
            tv_notFound.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }

        if (arrayList.size() > 0) {
            setAdapter(arrayList);
        }
    }

    private void setAdapter(ArrayList<Utils> arrayList) {
        mAdapter = new SampleAdapter(this, this, arrayList);
        // allow swiping with both directions (left-to-right and right-to-left)

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
        helper.attachToRecyclerView(mRecyclerView);
        helper.setCloseOnAction(false);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        helper.onSaveInstanceState(outState);
    }

    @Override
    public void removePosition(int position) {
        removePositionRecords(position);
    }

    private void removePositionRecords(int position) {
        if (arrayList.size() > 0) {
            db.deleteEntry(arrayList.get(position).getVehcleId());
            db.deleteRegistration(arrayList.get(position).getVehcleId());
            db.deleteReport(arrayList.get(position).getVehcleId());
            db.deleteService(arrayList.get(position).getVehcleId());
        }

        mAdapter.removePosition(position);
        mAdapter.notifyDataSetChanged();
        helper.closeAllOpenPositions();
        if (arrayList.size() == 0) {

            SharedPreferences prefs = getSharedPreferences("PETROL", MODE_PRIVATE);
            prefs.edit().putString("id", null).apply();

            Intent intent = new Intent(ShowRecordsActivity.this, Registration.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra(FuelEntry.Flag, "1");
            startActivityForResult(intent, 400);
            finish();
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }

    @Override
    public void showRecords(int position) {
        if (arrayList.size() > 0) {
            getRecordVehicle(arrayList.get(position).getVehcleId());
        } else {
            showToast(getString(R.string.record_not_found));
        }
        helper.closeAllOpenPositions();
    }

    /* Get Record According to Vehicle iD and show on Graph And List Also */
    @SuppressLint("SetTextI18n")
    public void getRecordVehicle(String v_Id) {
        //noinspection MismatchedQueryAndUpdateOfCollection
        ArrayList<Utils> arrayListRecords = new ArrayList<>();
        if (db.isExistVehicleID(v_Id)) {
            arrayHashList = new ArrayList<>();
            arrayHashList = db.getEntry(v_Id);
            if (arrayHashList != null && arrayHashList.size() > 0) {
                slideUp.show();
                for (int i = 0; i < arrayHashList.size(); i++) {
                    Utils u = new Utils();
                    u.setVehcleId(arrayHashList.get(i).get("vehcle_id"));
                    u.setId(arrayHashList.get(i).get("id"));
                    u.setVehcleName(arrayHashList.get(i).get("vehcle_name"));
                    u.setVehcleModel(arrayHashList.get(i).get("vehcle_model"));
                    u.setFuelType(arrayHashList.get(i).get("vehcle_fuel_type"));
                    u.setMileage(arrayHashList.get(i).get("vehcle_mileage_type"));
                    u.setDate(arrayHashList.get(i).get("date"));
                    String[] partsRead = arrayHashList.get(i).get("new_reading").split("\\.");
                    u.setNew_reading(partsRead[0]);
                    String[] partsFuel = arrayHashList.get(i).get("fuel").split("\\.");
                    u.setFuel(partsFuel[0]);
                    String[] partsPrice = arrayHashList.get(i).get("price").split("\\.");
                    u.setPrice(partsPrice[0]);
                    String[] partsAvg = arrayHashList.get(i).get("average").split("\\.");
                    u.setAverage(partsAvg[0]);
                    u.setPriceAvg(arrayHashList.get(i).get("price_average"));
                    arrayListRecords.add(u);
                }
                setAdapterRecords(arrayListRecords);
            } else {
                showToast(getString(R.string.record_not_found));
            }
        } else {
            showToast(getString(R.string.record_not_found));
        }
    }

    private void setAdapterRecords(ArrayList<Utils> arrayListRecords) {
        RecordsAdapter mRcAdapter = new RecordsAdapter(this, arrayListRecords);
        rVRecords.setLayoutManager(new LinearLayoutManager(this));
        rVRecords.setAdapter(mRcAdapter);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.slideView:
                break;

            default:
                break;
        }
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