package com.mileagecalculator.viewpager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.mileagecalculator.R;
import com.mileagecalculator.database.PetrolDB;
import com.mileagecalculator.fuelefficiency.Constant;
import com.mileagecalculator.fuelefficiency.RuntimePermission;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

@SuppressWarnings({"deprecation", "WeakerAccess", "CanBeFinal"})
public class StatisticsActivity extends AppCompatActivity implements Constant {
    private ViewPager viewPager;
    private RelativeLayout layout;
    private LinearLayout linearUpper;
    private PetrolDB db;
    private RuntimePermission runtimePermission;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_tabs);
        linearUpper = (LinearLayout) findViewById(R.id.linearUpper);
        db = new PetrolDB(this);
        runtimePermission = new RuntimePermission(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Expenses"));
        tabLayout.addTab(tabLayout.newTab().setText("Fuel Consumption"));
        tabLayout.addTab(tabLayout.newTab().setText("Fuel Price"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);

        viewPager = (ViewPager) findViewById(R.id.pager);
        TabsPagerAdapter mAdapter = new TabsPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount(), getIntent().getStringExtra("VehicleID"));
        viewPager.setAdapter(mAdapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

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

    private void setTitle() {
        try {
            getSupportActionBar().setTitle(URLDecoder.decode(getIntent().getStringExtra("VehicleName")
                    + " "
                    + "("
                    + getIntent().getStringExtra("VehicleModel") + ")" + " " + "Report", "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        if (db.isExistReport(getIntent().getStringExtra("VehicleID"))) {
            setTitle();
            showShareMenu(menu);
        } else {
            hideShareMenu(menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_share) {
            share();
        }
        return super.onOptionsItemSelected(item);
    }

    // Show Keyboard Menu List Item
    private void showShareMenu(Menu menu) {
        MenuItem menuList = menu.findItem(R.id.action_share);
        menuList.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menuList.setVisible(true);
    }

    // Hide Keyboard Menu List Item
    private void hideShareMenu(Menu menu) {
        MenuItem menuList = menu.findItem(R.id.action_share);
        menuList.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menuList.setVisible(false);
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
                    shareRecord(StatisticsActivity.this, finalResultBitmap, getString(R.string.app_name) + System.currentTimeMillis());
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