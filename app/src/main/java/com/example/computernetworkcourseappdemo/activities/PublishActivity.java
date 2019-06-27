package com.example.computernetworkcourseappdemo.activities;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.computernetworkcourseappdemo.LoginManager;
import com.example.computernetworkcourseappdemo.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PublishActivity extends AppCompatActivity {
    @BindView(R.id.editText_publish_content)
    EditText Content;
    @BindView(R.id.imageView_publish_picture)
    ImageView Picture;

    private LoginManager.UserData User;
    private RequestQueue rqRequestQueue;
    Bitmap PictureBitmap = null;

    private static final String TAG_PUBLISH = "TAG_PUBLISH";
    private static final String URL_PUBLISH = "http://47.94.248.141:5000/blog/create";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);
        getSupportActionBar().hide();

        ButterKnife.bind(this);

        User = (LoginManager.UserData) getIntent().getExtras().getSerializable("User");
        rqRequestQueue = Volley.newRequestQueue(getApplicationContext());
        rqRequestQueue.start();
    }

    @OnClick(R.id.button_publish_return)
    public void button_publish_return() {
        finish();
    }
    @OnClick(R.id.button_publish)
    public void button_publish() {
        publishWithDialog(Content.getText().toString(), bitmapToBase64(PictureBitmap));
    }
    @OnClick(R.id.imageView_publish_picture)
    public void select_picture() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, MainActivity.REQUEST_CODE_PUBLISH_SELECT_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MainActivity.REQUEST_CODE_PUBLISH_SELECT_PICTURE && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            ContentResolver cr = this.getContentResolver();
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                Picture.setImageBitmap(resizedBitmap);
                PictureBitmap = resizedBitmap;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void publishWithDialog(String content, String picture) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.publish_dialog_title);
        builder.setNegativeButton(R.string.publish_dialog_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                rqRequestQueue.cancelAll(TAG_PUBLISH);
            }
        });
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                rqRequestQueue.cancelAll(TAG_PUBLISH);
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                rqRequestQueue.cancelAll(TAG_PUBLISH);
            }
        });
        final AlertDialog dialog = builder.create();
        dialog.show();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", User.getId());
            jsonObject.put("nickname", User.getNickname());
            jsonObject.put("title", "aaa");
            jsonObject.put("body", content);
            jsonObject.put("image", picture);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL_PUBLISH, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if (jsonObject.isNull("error")) {
                    setResult(MainActivity.RESULT_CODE_PUBLISH_SUCCESS);
                    finish();
                } else {
                    dialog.setTitle(getResources().getString(R.string.publish_dialog_failed));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                dialog.setTitle(getResources().getString(R.string.publish_dialog_failed));
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        request.setTag(TAG_PUBLISH);
        rqRequestQueue.add(request);
    }

    public static String bitmapToBase64(Bitmap bitmap) {
        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                baos.flush();
                baos.close();

                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
