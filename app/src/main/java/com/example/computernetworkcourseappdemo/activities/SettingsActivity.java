package com.example.computernetworkcourseappdemo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.computernetworkcourseappdemo.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().hide();

        ButterKnife.bind(this);
    }

    @OnClick(R.id.button_settings_reset_password)
    public void button_settings_reset_password() {
        Intent it = new Intent(getApplicationContext(), ChangePasswordActivity.class);
        startActivity(it);
    }
    @OnClick(R.id.button_settings_logout)
    public void button_settings_logout() {
        Intent intent = new Intent();
        setResult(MainActivity.RESULT_CODE_SETTINGS_LOGOUT, intent);
        finish();
    }
}
