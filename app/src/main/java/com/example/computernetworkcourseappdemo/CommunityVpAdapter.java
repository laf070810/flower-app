package com.example.computernetworkcourseappdemo;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;


public class CommunityVpAdapter extends PagerAdapter {
    private List<View> ViewList;
    private List<String> TitleList;

    public CommunityVpAdapter(List<View> viewList, List<String> titleList) {
        ViewList = viewList;
        TitleList = titleList;
    }

    @Override
    public int getCount() { return ViewList == null ? 0 : ViewList.size(); }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        container.addView(ViewList.get(position));
        return ViewList.get(position);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView(ViewList.get(position));
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return TitleList.get(position);
    }
}
