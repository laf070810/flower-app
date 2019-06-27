package com.example.computernetworkcourseappdemo;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FlowerResultAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<FlowerResultData> DataSource;
    private RequestQueue requestQueue;

    private static final String URL_FETCHDATA = "http://47.94.248.141:5000/flower/auto_match";

    public FlowerResultAdapter(RequestQueue requestQueue) {
        this.DataSource = new ArrayList<>();
        this.requestQueue = requestQueue;
        fetchData();
    }
    public FlowerResultAdapter(RequestQueue requestQueue, List<FlowerResultData> dataSource) {
        DataSource = dataSource;
        this.requestQueue = requestQueue;
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
                        data = jsonObject.getJSONObject("pictures");
                        Iterator<String> ite = data.keys();
                        while (ite.hasNext()) {
                            String index = ite.next();
                            Bitmap stitchBmp = Bitmap.createBitmap(3, 3, Bitmap.Config.RGB_565);
                            stitchBmp.copyPixelsFromBuffer(ByteBuffer.wrap(Base64.decode(data.getString(index), Base64.DEFAULT)));
                            DataSource.add(new FlowerResultData(stitchBmp));
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
        requestQueue.add(request);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.flower_result_card, parent, false);
        RecyclerView.ViewHolder holder = new FlowerResultCardViewHolder(item);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        FlowerResultCardViewHolder holder = (FlowerResultCardViewHolder) viewHolder;
        holder.result.setImageBitmap(DataSource.get(i).image);
    }

    @Override
    public int getItemCount() { return DataSource == null ? 0 : DataSource.size(); }

    class FlowerResultCardViewHolder extends RecyclerView.ViewHolder {
        private ImageView result;

        public FlowerResultCardViewHolder(View itemView) {
            super(itemView);
            result = itemView.findViewById(R.id.image_flower_result);
        }
    }

    public static class OnFetchCompleteListener {
        public void onFetchComplete() { }
    }
    public static class OnFetchFailedListener {
        public void onFetchFailed() { }
    }
    public static class FlowerResultData {
        Bitmap image;

        public FlowerResultData(Bitmap image) {
            this.image = image;
        }
    }
}
