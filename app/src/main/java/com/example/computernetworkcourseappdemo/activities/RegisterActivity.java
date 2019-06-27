package com.example.computernetworkcourseappdemo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import com.example.computernetworkcourseappdemo.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends AppCompatActivity {
    @BindView(R.id.editText_register_phonenumber)
    TextView viewPhoneNumber;
    @BindView(R.id.editText_register_nickname)
    TextView viewNickname;
    @BindView(R.id.editText_register_password)
    TextView viewPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().hide();

        ButterKnife.bind(this);
    }

    @OnClick(R.id.button_return)
    public void button_return(Button button) {
        finish();
    }

    @OnClick(R.id.button_register_register)
    public void button_register_register() {
        Bundle bundle = new Bundle();
        bundle.putString("PhoneNumber", viewPhoneNumber.getText().toString());
        bundle.putString("Nickname", viewNickname.getText().toString());
        bundle.putString("Password", viewPassword.getText().toString());
        Intent intent = new Intent();
        intent.putExtra("USER_DATA", bundle);
        setResult(MainActivity.RESULT_CODE_REGISTER_FILLED, intent);
        finish();
    }
}
