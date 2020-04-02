package com.suvidha.Adapters;

import android.view.View;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    List<Fragment> list = new ArrayList<>();

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }
    @Override
    public int getCount() {
        return list.size();
    }
    @Override
    public Fragment getItem(int position) {
        return list.get(position);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return false;
    }
    public void addFragment(Fragment f){
        list.add(f);
    }
}
