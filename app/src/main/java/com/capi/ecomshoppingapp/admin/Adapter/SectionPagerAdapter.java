package com.capi.ecomshoppingapp.admin.Adapter;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.capi.ecomshoppingapp.Fragment.CancelledFragment;
import com.capi.ecomshoppingapp.Fragment.DeliveredFragment;
import com.capi.ecomshoppingapp.Fragment.PendingFragment;
import com.capi.ecomshoppingapp.Fragment.ProcessingFragment;

public class SectionPagerAdapter extends FragmentPagerAdapter
{
    private int numOfTabs;
    private String title[] = {"Pending", "Processing", "Delivered", "Cancelled"};

    public SectionPagerAdapter(Context context, FragmentManager fm, int numOfTabs) {
        super(fm);
        this.numOfTabs = numOfTabs;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return title[position];
    }

    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:
                return new PendingFragment();
            case 1:
                return new ProcessingFragment();
            case 2:
                return new DeliveredFragment();
            case 3:
                return new CancelledFragment();
            default:return null;
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}
