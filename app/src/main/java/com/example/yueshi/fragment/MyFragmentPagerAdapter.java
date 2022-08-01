package com.example.yueshi.fragment;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;


public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
    private String[] mTitles = new String[]{"日常分享", "琐事杂谈","心情树洞","美文分享"};
    public MyFragmentPagerAdapter(FragmentManager fm) {
        super (fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 1)
            return new CommunityFragment2();
        else if(position==2)
            return new CommunityFragment3();
        else if(position==3)
            return new CommunityFragment4();
        else
            return new CommunityFragment1();
    }

    @Override
    public int getCount() {
        return mTitles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }
}
