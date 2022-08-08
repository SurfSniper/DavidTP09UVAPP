package com.monash.paindiary.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.monash.paindiary.fragments.Report1Fragment;
import com.monash.paindiary.fragments.Report2Fragment;
import com.monash.paindiary.fragments.Report3Fragment;

public class ReportCollectionPagerAdapter extends FragmentStateAdapter {

    public ReportCollectionPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new Report1Fragment();
            case 1:
                return new Report2Fragment();
            case 2:
                return new Report3Fragment();
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
