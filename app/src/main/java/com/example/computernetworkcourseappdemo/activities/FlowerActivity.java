package com.example.computernetworkcourseappdemo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.computernetworkcourseappdemo.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class FlowerActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flower);
        getSupportActionBar().hide();

//        Initialize ButterKnife.
        ButterKnife.bind(this);
    }

    @OnClick(R.id.button_flower_generate)
    public void button_flower_generate() {
        Intent it = new Intent(this, FlowerResultActivity.class);
        startActivity(it);
    }
}
