package com.example.yueshi.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.yueshi.PostTopicActivity;
import com.example.yueshi.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

public class cmFragment extends Fragment {

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private MyFragmentPagerAdapter myFragmentPagerAdapter1;
    private TabLayout.Tab one;
    private TabLayout.Tab two;
    private TabLayout.Tab three;
    private TabLayout.Tab four;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_community, container, false);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mTabLayout =(TabLayout) getActivity(). findViewById(R.id.tab_main1);
        mViewPager = (ViewPager) getActivity().findViewById(R.id.vp_main1);
        myFragmentPagerAdapter1 = new MyFragmentPagerAdapter(getChildFragmentManager());
        //使用适配器将ViewPager与Fragment绑定在一起
        mViewPager.setAdapter(myFragmentPagerAdapter1);
        //将TabLayout和ViewPager绑定在一起，相互影响，解放了开发人员对双方变动事件的监听
        mTabLayout.setupWithViewPager(mViewPager);
        //指定Tab的位置
        one = mTabLayout.getTabAt(0);
        two = mTabLayout.getTabAt(1);
        three = mTabLayout.getTabAt(2);
        four = mTabLayout.getTabAt(3);

        //发表帖子（使用悬浮按钮）
        FloatingActionButton fab = getActivity().findViewById(R.id.fabtn_post);

        //        发帖
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PostTopicActivity.class);
                startActivity(intent);
            }
        });

    }

}
