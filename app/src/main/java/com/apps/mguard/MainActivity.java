package com.apps.mguard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;

import com.apps.mguard.claender.CalenderFragment;
import com.apps.mguard.detaillist.DetailListFragment;
import com.apps.mguard.home.HomeFragment;
import com.apps.mguard.statement.StatementFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNav;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNav=findViewById(R.id.bottomNav);
        viewPager=findViewById(R.id.viewPager);

        ActionBar bar = getSupportActionBar();
        bar.hide();

        setUpViewPager();

        bottomNav.setOnNavigationItemSelectedListener((item -> {
            switch (item.getItemId()){
                case R.id.home:
                    viewPager.setCurrentItem(0);
                    break;
                case R.id.calender:
                    viewPager.setCurrentItem(1);
                    break;
                case R.id.statement:
                    viewPager.setCurrentItem(2);
                    break;
                case R.id.list:
                    viewPager.setCurrentItem(3);
                    break;
            }
            return true;
        }));

    }

    void setUpViewPager(){
        MyPageAdapter myPageAdapter = new MyPageAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPager.setAdapter(myPageAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        bottomNav.getMenu().findItem(R.id.home).setChecked(true);
                        break;
                    case 1:
                        bottomNav.getMenu().findItem(R.id.calender).setChecked(true);
                        break;
                    case 2:
                        bottomNav.getMenu().findItem(R.id.statement).setChecked(true);
                        break;
                    case 3:
                        bottomNav.getMenu().findItem(R.id.list).setChecked(true);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


}