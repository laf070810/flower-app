package com.example.computernetworkcourseappdemo;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
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

import java.util.Iterator;
import java.util.List;

public class HomepageRvAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<HomepageData> DataSource;
    private RequestQueue requestQueue;

    private static final String TAG_FETCHDATA = "TAG_FETCHDATA_HOMEPAGE";
    private static final String URL_FETCHDATA = "http://47.94.248.141:5000/blog/get_all";

    public HomepageRvAdapter(RequestQueue requestQueue) {
        this.requestQueue = requestQueue;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_homepage_rv_card, parent, false);
        RecyclerView.ViewHolder holder = new HomepageCardViewHolder(item);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        HomepageCardViewHolder holder = (HomepageCardViewHolder) viewHolder;
        holder.Picture.setImageBitmap(DataSource.get(i).Picture);
        holder.Description.setText(DataSource.get(i).Description);
    }

    @Override
    public int getItemCount() {
        return DataSource == null ? 0 : DataSource.size();
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
//                            DataSource.add(new CommunityData(data.getJSONObject(index).getString("nickname"), data.getJSONObject(index).getString("body")));
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

    public static class OnFetchCompleteListener {
        public void onFetchComplete() { }
    }
    public static class OnFetchFailedListener {
        public void onFetchFailed() { }
    }
    class HomepageCardViewHolder extends RecyclerView.ViewHolder {
        private ImageView Picture;
        private TextView Description;

        public HomepageCardViewHolder(@NonNull View itemView) {
            super(itemView);
            Picture = itemView.findViewById(R.id.image_picture);
            Description = itemView.findViewById(R.id.text_description);
        }
    }
    class HomepageData {
        Bitmap Picture;
        String Description;

        public HomepageData(Bitmap picture, String description) {
            Picture = picture;
            Description = description;
        }
    }
}
