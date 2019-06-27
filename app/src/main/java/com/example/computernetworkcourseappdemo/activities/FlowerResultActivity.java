package com.example.computernetworkcourseappdemo.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.computernetworkcourseappdemo.FlowerResultAdapter;
import com.example.computernetworkcourseappdemo.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FlowerResultActivity extends AppCompatActivity {
    private RequestQueue rqRequestQueue;

    @BindView(R.id.rv_flower_result)
    RecyclerView rvFlowerResult;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flower_result);
        getSupportActionBar().hide();
        ButterKnife.bind(this);

        rqRequestQueue = Volley.newRequestQueue(getApplicationContext());
        rqRequestQueue.start();

        FlowerResultAdapter adapter = new FlowerResultAdapter(rqRequestQueue);
        rvFlowerResult.setAdapter(adapter);
        rvFlowerResult.setLayoutManager(new LinearLayoutManager(FlowerResultActivity.this, LinearLayoutManager.VERTICAL, false));
        rvFlowerResult.addItemDecoration(new DividerItemDecoration(FlowerResultActivity.this, DividerItemDecoration.VERTICAL));
    }
}
