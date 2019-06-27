package com.example.computernetworkcourseappdemo.activities;

import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.RadioGroup;
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

public class ModifyPersonalDataActivity extends AppCompatActivity {
    @BindView(R.id.editText_modify_personal_data_phone_number)
    TextView phone_number;
    @BindView(R.id.editText_modify_personal_data_nickname)
    TextView nickname;
    @BindView(R.id.editText_modify_personal_data_personal_description)
    TextView personal_description;
    @BindView(R.id.editText_modify_personal_data_age)
    TextView age;
    @BindView(R.id.editText_modify_personal_data_region)
    TextView region;
    @BindView(R.id.radioGroup_modify_personal_data_sex)
    RadioGroup sex;
    @BindView(R.id.imageView_modify_personal_data_avatar)
    ImageView avatar;

    private LoginManager lmLoginManager;
    private RequestQueue rqRequestQueue;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        rqRequestQueue = Volley.newRequestQueue(getApplicationContext());
        rqRequestQueue.start();
        lmLoginManager = new LoginManager(getApplicationContext(), rqRequestQueue);

        lmLoginManager.login(new LoginManager.OnLoggedInListener() {
            @Override
            public void onLoggedIn(JSONObject response) {
                super.onLoggedIn(response);
                setContentView(R.layout.activity_modify_personal_data);
                ButterKnife.bind(ModifyPersonalDataActivity.this);

                phone_number.setText(lmLoginManager.User.phone_number);
                nickname.setText(lmLoginManager.User.nickname);
                personal_description.setText(lmLoginManager.User.personal_description);
                age.setText(String.valueOf(lmLoginManager.User.age));
                region.setText(lmLoginManager.User.region);

                if (lmLoginManager.User.sex.equals("男")) {
                    sex.check(R.id.checkBox_modify_personal_data_male);
                } else if (lmLoginManager.User.sex.equals("女")) {
                    sex.check(R.id.checkBox_modify_personal_data_female);
                } else if (lmLoginManager.User.sex.equals("未知")) {
                    sex.check(R.id.checkBox_modify_personal_data_unknown);
                }

                byte[] avatar_bytes = Base64.decode(lmLoginManager.User.getHead(), Base64.DEFAULT);
                avatar.setImageBitmap(BitmapFactory.decodeByteArray(avatar_bytes, 0, avatar_bytes.length));
            }
        }, new LoginManager.OnLoginFailedListener());
    }

    @OnClick(R.id.button_modify_cancel)
    public void button_cancel() {
        finish();
    }
    @OnClick(R.id.button_modify_confirm)
    public void button_confirm() {
        lmLoginManager.User.phone_number = phone_number.getText().toString();
        lmLoginManager.User.nickname = nickname.getText().toString();
        lmLoginManager.User.personal_description = personal_description.getText().toString();
        lmLoginManager.User.age = Integer.valueOf(age.getText().toString());
        lmLoginManager.User.region = region.getText().toString();

        if (sex.getCheckedRadioButtonId() == R.id.checkBox_modify_personal_data_male) {
            lmLoginManager.User.sex = "男";
        } else if (sex.getCheckedRadioButtonId() == R.id.checkBox_modify_personal_data_female) {
            lmLoginManager.User.sex = "女";
        } else if (sex.getCheckedRadioButtonId() == R.id.checkBox_modify_personal_data_unknown) {
            lmLoginManager.User.sex = "未知";
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setNegativeButton(R.string.modify_personal_data_dialog_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        builder.setTitle(R.string.modify_personal_data_dialog_title);
        final AlertDialog dialog = builder.create();
        dialog.show();

        lmLoginManager.updateUserData(new LoginManager.OnUpdatedUserDataListener() {
            @Override
            public void onUpdatedUserData(JSONObject response) {
                super.onUpdatedUserData(response);
                dialog.dismiss();
                finish();
                try {
                    Toast.makeText(getApplicationContext(), response.getString("msg"), Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new LoginManager.OnUpdateUserDataFailedListerner() {
            @Override
            public void onUpdateUserDataFailed(JSONObject response) {
                super.onUpdateUserDataFailed(response);
                if (response == null) {
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), R.string.modify_personal_data_dialog_failed, Toast.LENGTH_LONG).show();
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
}
