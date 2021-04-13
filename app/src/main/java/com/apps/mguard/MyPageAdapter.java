package com.apps.mguard;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.apps.mguard.claender.CalenderFragment;
import com.apps.mguard.detaillist.DetailListFragment;
import com.apps.mguard.home.HomeFragment;
import com.apps.mguard.statement.StatementFragment;

public class MyPageAdapter extends FragmentStatePagerAdapter {


    public MyPageAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return  new HomeFragment();
            case 1:
                return  new CalenderFragment();
            case 2:
                return  new StatementFragment();
            case 3:
                return new DetailListFragment();
            default:
                return  new HomeFragment();
        }
    }

    @Override
    public int getCount() {
        return 4;
    }
}
