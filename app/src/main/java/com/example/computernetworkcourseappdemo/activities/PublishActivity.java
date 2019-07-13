package com.example.computernetworkcourseappdemo.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayout;
import android.util.Base64;
import android.view.View;
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

import net.alhazmy13.mediapicker.Image.ImagePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PublishActivity extends AppCompatActivity {
    @BindView(R.id.constraintLayout_publish)
    ConstraintLayout constraintLayout_publish;
    @BindView(R.id.editText_publish_content)
    EditText Content;
    @BindView(R.id.gridLayout_publish_picture)
    GridLayout Pictures;

    private LoginManager.UserData User;
    private RequestQueue rqRequestQueue;
    Bitmap[] PictureBitmaps = null;

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

        ImageView mImageView = new ImageView(getApplicationContext());
        GridLayout.LayoutParams mLayoutParams = new GridLayout.LayoutParams();
        mLayoutParams.columnSpec = GridLayout.spec(0, 1, 1.0f);
        mLayoutParams.rowSpec = GridLayout.spec(0, 1, 1.0f);
        mImageView.setLayoutParams(mLayoutParams);
        mImageView.setImageResource(R.drawable.ic_add_2);
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ImagePicker.Builder(PublishActivity.this)
                        .mode(ImagePicker.Mode.CAMERA_AND_GALLERY)
                        .compressLevel(ImagePicker.ComperesLevel.MEDIUM)
                        .directory(ImagePicker.Directory.DEFAULT)
                        .extension(ImagePicker.Extension.JPG)
                        .scale(600, 600)
                        .allowMultipleImages(true)
                        .enableDebuggingMode(true)
                        .build();
            }
        });
        final float scale = getApplicationContext().getResources().getDisplayMetrics().density;
        int pixels = (int) (100 * scale + 0.5f);
        mLayoutParams.width = pixels;
        mLayoutParams.height = pixels;
        Pictures.addView(mImageView, pixels, pixels);
    }

    @OnClick(R.id.button_publish_return)
    public void button_publish_return() {
        finish();
    }
    @OnClick(R.id.button_publish)
    public void button_publish() {
        if (PictureBitmaps == null) {
            publishWithDialog(Content.getText().toString(), "");
            return;
        }

        StringBuffer base64Str = new StringBuffer();
        for (int i = 0; i < PictureBitmaps.length - 1; i++) {
            base64Str.append(bitmapToBase64(PictureBitmaps[i]));
            base64Str.append(',');
        }
        base64Str.append(bitmapToBase64(PictureBitmaps[PictureBitmaps.length - 1]));
        publishWithDialog(Content.getText().toString(), base64Str.toString());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ImagePicker.IMAGE_PICKER_REQUEST_CODE && resultCode == RESULT_OK) {
            final ArrayList<String> mPaths = data.getStringArrayListExtra(ImagePicker.EXTRA_IMAGE_PATH);
            PictureBitmaps = new Bitmap[mPaths.size()];
//            Uri uri = data.getData();
//            ContentResolver cr = this.getContentResolver();
            try {
//                Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
//
//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                int quality = 80;
//                do {
//                    baos.reset();
//                    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
//                    quality -= 10;
//                } while (baos.size() > 1024 * 1024 && quality >= 0);
//                bitmap = BitmapFactory.decodeByteArray(baos.toByteArray(), 0, baos.size());
//

                Pictures.removeAllViews();
                for (int i = 0; i <= mPaths.size() / 3; i++) {
                    for (int j = 0; j < 3 && (i * 3 + j) < mPaths.size() && (i * 3 + j) < 9; j++) {
                        Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(mPaths.get(i * 3 + j)));
                        int newWidth = bitmap.getWidth() > 500 ? 500 : bitmap.getWidth();
                        int newHeight = bitmap.getHeight() > 500 ? 500 : bitmap.getHeight();
                        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, bitmap.getWidth() / 2 - newWidth / 2, bitmap.getHeight() / 2 - newHeight / 2, newWidth, newHeight);
                        ImageView image = new ImageView(getApplicationContext());
                        image.setImageBitmap(resizedBitmap);
                        image.setPadding(5, 5, 5, 5);
                        GridLayout.LayoutParams mLayoutParams = new GridLayout.LayoutParams();
                        mLayoutParams.rowSpec = GridLayout.spec(i, 1, 1.0f);
                        mLayoutParams.columnSpec = GridLayout.spec(j, 1, 1.0f);
                        mLayoutParams.width = 0;
                        mLayoutParams.height = 0;
                        image.setLayoutParams(mLayoutParams);
                        final int imagePosition = i * 3 + j;
                        image.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Bundle bundle = new Bundle();
                                bundle.putInt("ImagePosition", imagePosition);
                                bundle.putStringArrayList("ImagePaths", mPaths);
                                Intent it = new Intent(PublishActivity.this, PublishBigImageActivity.class);
                                it.putExtras(bundle);
                                PublishActivity.this.startActivity(it);
                            }
                        });

                        Pictures.addView(image);
                        PictureBitmaps[i * 3 + j] = bitmap;
                    }
                }
                if (mPaths.size() < 9) {
                    ImageView mImageView = new ImageView(getApplicationContext());
                    GridLayout.LayoutParams mLayoutParams = new GridLayout.LayoutParams();
                    mLayoutParams.rowSpec = GridLayout.spec(mPaths.size() / 3, 1, 1.0f);
                    mLayoutParams.columnSpec = GridLayout.spec(mPaths.size() - (mPaths.size() / 3) * 3, 1, 1.0f);
                    mLayoutParams.width = 0;
                    mLayoutParams.height = 0;
                    mImageView.setLayoutParams(mLayoutParams);
                    mImageView.setImageResource(R.drawable.ic_add_2);
                    mImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new ImagePicker.Builder(PublishActivity.this)
                                    .mode(ImagePicker.Mode.CAMERA_AND_GALLERY)
                                    .compressLevel(ImagePicker.ComperesLevel.MEDIUM)
                                    .directory(ImagePicker.Directory.DEFAULT)
                                    .extension(ImagePicker.Extension.JPG)
                                    .scale(600, 600)
                                    .allowMultipleImages(true)
                                    .enableDebuggingMode(true)
                                    .build();
                        }
                    });

                    Pictures.addView(mImageView);
                }
//                Pictures.setImageBitmap(bitmap);
//                ConstraintSet c = new ConstraintSet();
//                c.clone(constraintLayout_publish);
//                c.constrainWidth(R.id.gridLayout_publish_picture, ConstraintSet.MATCH_CONSTRAINT);
//                c.constrainHeight(R.id.gridLayout_publish_picture, ConstraintSet.MATCH_CONSTRAINT);
//                c.applyTo(constraintLayout_publish);
//                Pictures.setAdjustViewBounds(true);

//                PictureBitmap = bitmap;
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
            if (picture == null) {
                jsonObject.put("image", "");
            } else {
                jsonObject.put("image", picture);
            }
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
