package com.example.jeonghun.heathcare.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.jeonghun.heathcare.View.Fragement.BalanceFragment;
import com.example.jeonghun.heathcare.View.Fragement.DistributionFragment;
import com.example.jeonghun.heathcare.View.Fragement.InformationFragment;
import com.example.jeonghun.heathcare.View.Fragement.MuscleFragment;
import com.example.jeonghun.heathcare.View.Fragement.RatioFragment;

/**
 * Created by JeongHun on 16. 5. 5..
 */
public class PagerAdapter extends FragmentStatePagerAdapter {

    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0: return new InformationFragment();
        }
        throw new IndexOutOfBoundsException();
    }

    @Override
    public int getCount() {
        return 1;
    }
}
