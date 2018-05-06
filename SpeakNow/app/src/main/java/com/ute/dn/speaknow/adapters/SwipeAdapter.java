package com.ute.dn.speaknow.adapters;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class SwipeAdapter extends FragmentStatePagerAdapter{

    List<Fragment> lstFragment = new ArrayList<>();

    public SwipeAdapter(FragmentManager fm, List<Fragment> lst) {
        super(fm);
        lstFragment.addAll(lst);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        try {
            fragment = lstFragment.get(position);
        }
        catch (Exception e){

        }
        return fragment;
    }

    @Override
    public int getCount() {
        return lstFragment.size();
    }
}
