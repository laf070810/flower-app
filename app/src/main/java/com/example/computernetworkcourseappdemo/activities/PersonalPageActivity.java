package com.example.computernetworkcourseappdemo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.example.computernetworkcourseappdemo.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PersonalPageActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_page);
        getSupportActionBar().hide();

        ButterKnife.bind(this);
    }

    @OnClick(R.id.button_personal_page_modify)
    public void modify_personal_data() {
        Intent it = new Intent(this, ModifyPersonalDataActivity.class);
        startActivity(it);
    }
}
