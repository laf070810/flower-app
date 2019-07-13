package com.example.computernetworkcourseappdemo.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.computernetworkcourseappdemo.LoginManager;
import com.example.computernetworkcourseappdemo.R;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChangePasswordActivity extends AppCompatActivity {
    @BindView(R.id.editText_changepassword_original)
    TextView OriginalPasswordView;
    @BindView(R.id.editText_changepassword_new)
    TextView NewPasswordView;

    private LoginManager lmLoginManager;
    private RequestQueue rqRequestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        rqRequestQueue = Volley.newRequestQueue(getApplicationContext());
        rqRequestQueue.start();
        lmLoginManager = new LoginManager(getApplicationContext(), rqRequestQueue);

        lmLoginManager.login(new LoginManager.OnLoggedInListener() {
            @Override
            public void onLoggedIn(JSONObject response) {
                super.onLoggedIn(response);
                setContentView(R.layout.activity_change_password);
                ButterKnife.bind(ChangePasswordActivity.this);
            }
        }, new LoginManager.OnLoginFailedListener());
    }

    @OnClick(R.id.button_changepassword)
    public void button_change() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setNegativeButton(R.string.change_password_dialog_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        builder.setTitle(R.string.change_password_dialog_title);
        final AlertDialog dialog = builder.create();
        dialog.show();

        lmLoginManager.changePassword(OriginalPasswordView.getText().toString(), NewPasswordView.getText().toString(), new LoginManager.OnChangedPasswordListener() {
            @Override
            public void onChangedPassword(JSONObject response) {
                super.onChangedPassword(response);
                dialog.dismiss();
                finish();
                try {
                    Toast.makeText(getApplicationContext(), response.getString("msg"), Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new LoginManager.OnChangePasswordFailedListener() {
            @Override
            public void onChangePasswordFailed(JSONObject response) {
                super.onChangePasswordFailed(response);
                if (response == null) {
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), R.string.change_password_failed, Toast.LENGTH_LONG).show();
                } else {
                    try {
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), response.getString("error"), Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
    @OnClick(R.id.button_change_pw_return)
    public void button_return(View view) {
        finish();
    }
}