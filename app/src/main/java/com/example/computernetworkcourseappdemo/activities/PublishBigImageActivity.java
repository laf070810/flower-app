package com.example.computernetworkcourseappdemo.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.celerysoft.imagepager.ImagePager;
import com.celerysoft.imagepager.adapter.SimpleImagePagerAdapter;
import com.celerysoft.imagepager.animation.ZoomOutPageTransformer;
import com.celerysoft.imagepager.view.indicator.DotIndicator;
import com.example.computernetworkcourseappdemo.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PublishBigImageActivity extends AppCompatActivity {
    @BindView(R.id.image_pager)
    ImagePager imagePager;

    SimpleImagePagerAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_big_image);
        getSupportActionBar().hide();

        ButterKnife.bind(this);

        adapter = new SimpleImagePagerAdapter(this);
        adapter.setScaleType(ImageView.ScaleType.FIT_CENTER);

        int imagePosition = getIntent().getExtras().getInt("ImagePosition");
        ArrayList<String> imagePaths = getIntent().getExtras().getStringArrayList("ImagePaths");
        ArrayList<SimpleImagePagerAdapter.Image> images = new ArrayList<>();
        for (String path : imagePaths) {
            SimpleImagePagerAdapter.Image image = new SimpleImagePagerAdapter.Image();
            image.setImagePath(path);
            images.add(image);
        }
        adapter.setImages(images);
        imagePager.setAdapter(adapter);
        setDotIndicator();
        imagePager.setIndicatorMargin(16);
        imagePager.setPageTransformer(true, new ZoomOutPageTransformer());
        imagePager.moveToImage(imagePosition);
    }

    private void setDotIndicator() {
        DotIndicator indicator = new DotIndicator(this);
        indicator.setSelectedImageResource(R.drawable.ic_selected);
        indicator.setUnselectedImageResource(R.drawable.ic_unselected);
        imagePager.setIndicator(indicator);
    }
}
