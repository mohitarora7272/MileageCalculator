package com.mileagecalculator.viewpager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

class TabsPagerAdapter extends FragmentStatePagerAdapter {
    private int mNumOfTabs;
    private String vehicleId;

    TabsPagerAdapter(FragmentManager fm, int NumOfTabs, String vehicleId) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.vehicleId = vehicleId;
        Log.e("vehicleId","vehicleId>>"+vehicleId);
    }

    @Override
    public Fragment getItem(int index) {
        Fragment fragment = null;
        switch (index) {
            case 0:
                fragment = new ExpensesFragment(vehicleId);
                break;

            case 1:
                fragment = new FuelConsumptionFragment(vehicleId);
                break;

            case 2:
                fragment = new FuelPriceFragment(vehicleId);
                break;
            default:
                break;

        }
        return fragment;
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

}