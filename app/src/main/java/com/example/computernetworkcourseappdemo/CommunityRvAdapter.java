package com.example.computernetworkcourseappdemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CommunityRvAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<CommunityData> DataSource;
    private RequestQueue requestQueue;

    private static final String TAG_FETCHDATA = "TAG_FETCHDATA_COMMUNITY";
    private static final String URL_FETCHDATA = "http://47.94.248.141:5000/blog/get_all";

    public CommunityRvAdapter(RequestQueue requestQueue) {
        this.DataSource = new ArrayList<>();
        this.requestQueue = requestQueue;
        fetchData();
    }
    public CommunityRvAdapter(RequestQueue requestQueue, List<CommunityData> dataSource) {
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
                    JSONObject data = null;
                    try {
                        data = jsonObject.getJSONObject("blogs");
                        Iterator<String> ite = data.keys();
                        while (ite.hasNext()) {
                            String index = ite.next();
                            byte[] avatar_bytes = Base64.decode(data.getJSONObject(index).getString("image"), Base64.DEFAULT);
                            DataSource.add(new CommunityData(data.getJSONObject(index).getString("nickname"), data.getJSONObject(index).getString("body"), BitmapFactory.decodeByteArray(avatar_bytes, 0, avatar_bytes.length)));
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
        Holder.ivPicture.setImageBitmap(DataSource.get(position).Picture);
    }
    @Override
    public int getItemCount() {
        return DataSource == null ? 0 : DataSource.size();
    }

    class CommunityCardViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivAvatar;
        private TextView tvUsername;
        private TextView tvContent;
        private ImageView ivPicture;

        public CommunityCardViewHolder(View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.image_community_card_avatar);
            tvUsername = itemView.findViewById(R.id.text_community_card_username);
            tvContent = itemView.findViewById(R.id.text_community_card_content);
            ivPicture = itemView.findViewById(R.id.image_community_card_picture);
        }
    }
    public static class OnFetchCompleteListener {
        public void onFetchComplete() { }
    }
    public static class OnFetchFailedListener {
        public void onFetchFailed() { }
    }
    public static class CommunityData {
        String Username;
        String Content;
        Bitmap Picture;

        public CommunityData(String username, String content, Bitmap picture) {
            Username = username;
            Content = content;
            Picture = picture;
        }
    }
}
