package com.example.computernetworkcourseappdemo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.widget.GridLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.computernetworkcourseappdemo.activities.BigImageActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CommunityRvAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Activity parent;
    private List<CommunityData> DataSource;
    private RequestQueue requestQueue;

    private static final String TAG_FETCHDATA = "TAG_FETCHDATA_COMMUNITY";
    private static final String URL_FETCHDATA = "http://47.94.248.141:5000/blog/get_all";
    private static final String URL_BIGIMAGE = "http://47.94.248.141:5000/blog/get_image";

    public CommunityRvAdapter(Activity parent, RequestQueue requestQueue) {
        this.parent = parent;
        this.DataSource = new ArrayList<>();
        this.requestQueue = requestQueue;
        fetchData();
    }
    public CommunityRvAdapter(Activity parent, RequestQueue requestQueue, List<CommunityData> dataSource) {
        this.parent = parent;
        DataSource = dataSource;
        this.requestQueue = requestQueue;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_community_rv_card, parent, false);
        RecyclerView.ViewHolder holder = new CommunityCardViewHolder(item);
        return holder;
    }

    public void fetchData() {fetchData(new OnFetchCompleteListener(), new OnFetchFailedListener());}
    public void fetchData(final OnFetchCompleteListener onFetchCompleteListener, final OnFetchFailedListener onFetchFailedListener) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL_FETCHDATA, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if (jsonObject.isNull("error")) {
                    DataSource.clear();
                    JSONArray data;
                    try {
                        data = jsonObject.getJSONArray("blogs");
//                        Iterator<String> ite = data.keys();
                        for (int index = 0; index < data.length(); index++) {
//                            String index = ite.next();
                            String[] pictureStrings = data.getJSONObject(index).getString("image").split(",");
                            Bitmap[] pictureBitmaps = new Bitmap[pictureStrings.length];
                            for (int i = 0; i < pictureBitmaps.length; i++) {
                                byte[] pictureBytes = Base64.decode(pictureStrings[i], Base64.DEFAULT);
                                pictureBitmaps[i] = BitmapFactory.decodeByteArray(pictureBytes, 0, pictureBytes.length);
                            }
                            DataSource.add(new CommunityData(data.getJSONObject(index).getInt("id"), data.getJSONObject(index).getString("nickname"), data.getJSONObject(index).getString("body"), pictureBitmaps));
                        }
                    } catch (JSONException e) { e.printStackTrace(); }

                    notifyDataSetChanged();
                    onFetchCompleteListener.onFetchComplete();
                } else {
                    onFetchFailedListener.onFetchFailed();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                onFetchFailedListener.onFetchFailed();
            }
        });
        request.setTag(TAG_FETCHDATA);
        requestQueue.add(request);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        CommunityCardViewHolder Holder = (CommunityCardViewHolder)holder;
        Holder.ivAvatar.setImageResource(R.drawable.avatar_demo);
        Holder.tvUsername.setText(DataSource.get(position).Username);
        Holder.tvContent.setText(DataSource.get(position).Content);

        Holder.glPictures.removeAllViews();
        if (DataSource.get(position).Pictures[0] == null) {
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(Holder.constraintLayout);
            constraintSet.constrainHeight(Holder.glPictures.getId(), 0);
            constraintSet.applyTo(Holder.constraintLayout);
            return;
        }
        for (int i = 0; i <= DataSource.get(position).Pictures.length / 3; i++) {
            for (int j = 0; j < 3 && (i * 3 + j) < DataSource.get(position).Pictures.length && (i * 3 + j) < 9; j++) {
                Bitmap bitmap = DataSource.get(position).Pictures[i * 3 + j];
                int newWidth = bitmap.getWidth() > 500 ? 500 : bitmap.getWidth();
                int newHeight = bitmap.getHeight() > 500 ? 500 : bitmap.getHeight();
                Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, bitmap.getWidth() / 2 - newWidth / 2, bitmap.getHeight() / 2 - newHeight / 2, newWidth, newHeight);
                ImageView image = new ImageView(parent.getApplicationContext());
                image.setImageBitmap(resizedBitmap);
                image.setPadding(5, 5, 5, 5);
                GridLayout.LayoutParams mLayoutParams = new GridLayout.LayoutParams();
                mLayoutParams.rowSpec = GridLayout.spec(i, 1, 1.0f);
                mLayoutParams.columnSpec = GridLayout.spec(j, 1, 1.0f);
                mLayoutParams.width = 0;
                mLayoutParams.height = 0;
                image.setLayoutParams(mLayoutParams);
                final int imagePosition = i * 3 + j;
                final int postID = position;
                image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        ArrayList<String> imageUrls = new ArrayList<>();
                        for (int i = 0; i < DataSource.get(postID).Pictures.length; i++) {
                            imageUrls.add(URL_BIGIMAGE + "?p_id=" + DataSource.get(postID).id + "&index=" + i);
                        }
                        bundle.putStringArrayList("ImageUrls", imageUrls);
                        bundle.putInt("ImagePosition", imagePosition);
                        Intent it = new Intent(parent, BigImageActivity.class);
                        it.putExtras(bundle);
                        parent.startActivity(it);
                    }
                });

                Holder.glPictures.addView(image);
            }
        }
    }
    @Override
    public int getItemCount() {
        return DataSource == null ? 0 : DataSource.size();
    }

    class CommunityCardViewHolder extends RecyclerView.ViewHolder {
        private ConstraintLayout constraintLayout;
        private ImageView ivAvatar;
        private TextView tvUsername;
        private TextView tvContent;
        private GridLayout glPictures;

        public CommunityCardViewHolder(View itemView) {
            super(itemView);
            constraintLayout = itemView.findViewById(R.id.constraintLayout_community_card);
            ivAvatar = itemView.findViewById(R.id.image_community_card_avatar);
            tvUsername = itemView.findViewById(R.id.text_community_card_username);
            tvContent = itemView.findViewById(R.id.text_community_card_content);
            glPictures = itemView.findViewById(R.id.gridLayout_community_card_picture);
        }
    }
    public static class OnFetchCompleteListener {
        public void onFetchComplete() { }
    }
    public static class OnFetchFailedListener {
        public void onFetchFailed() { }
    }
    public static class CommunityData {
        int id;
        String Username;
        String Content;
        Bitmap[] Pictures;

        public CommunityData(int id, String username, String content, Bitmap[] pictures) {
            this.id = id;
            Username = username;
            Content = content;
            Pictures = pictures;
        }
    }
}
