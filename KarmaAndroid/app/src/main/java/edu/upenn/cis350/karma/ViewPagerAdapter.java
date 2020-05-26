package edu.upenn.cis350.karma;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private final List<Fragment> firstFrag = new ArrayList<>();
    private final List<String> firstTitles = new ArrayList<>();

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return firstFrag.get(position);
    }

    @Override
    public int getCount() {
        return firstTitles.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return firstTitles.get(position);
    }

    public void AddFragment (Fragment frag, String title) {
        firstFrag.add(frag);
        firstTitles.add(title);
    }
}
