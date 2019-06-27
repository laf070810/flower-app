package com.example.computernetworkcourseappdemo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.example.computernetworkcourseappdemo.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {
    @BindView(R.id.editText_login_username)
    TextView viewUsername;
    @BindView(R.id.editText_login_password)
    TextView viewPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        ButterKnife.bind(this);
    }

    @OnClick(R.id.button_login_login)
    public void button_login_login (View view) {
        Bundle bundle = new Bundle();
        bundle.putString("Username", viewUsername.getText().toString());
        bundle.putString("Password", viewPassword.getText().toString());
        Intent intent = new Intent();
        intent.putExtra("USER_DATA", bundle);
        setResult(MainActivity.RESULT_CODE_LOGIN_FILLED, intent);
        finish();
    }

    @OnClick(R.id.button_return)
    public void button_return(View view) {
        finish();
    }

    @OnClick(R.id.textView_login_register)
    public void text_register(View view) {
        setResult(MainActivity.RESULT_CODE_JUMP_TO_REGISTER);
        finish();
    }
}
