package com.example.computernetworkcourseappdemo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.widget.GridLayout;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.computernetworkcourseappdemo.activities.BigImageActivity;
import com.example.computernetworkcourseappdemo.activities.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommunityRvAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Activity parent;
    private List<PostData> DataSource;
    private RequestQueue requestQueue;

    private static final String TAG_FETCHDATA = "TAG_FETCHDATA_COMMUNITY";
    private static final String TAG_LIKE = "TAG_LIKE";
    private static final String URL_FETCHDATA = "http://47.94.248.141:5000/blog/get_all";
    private static final String URL_BIGIMAGE = "http://47.94.248.141:5000/blog/get_image";
    private static final String URL_LIKE = "http://47.94.248.141:5000/blog/like";

    public CommunityRvAdapter(Activity parent, RequestQueue requestQueue) {
        this.parent = parent;
        this.DataSource = new ArrayList<>();
        this.requestQueue = requestQueue;
        fetchData();
    }
    public CommunityRvAdapter(Activity parent, RequestQueue requestQueue, List<PostData> dataSource) {
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
                        for (int index = 0; index < data.length(); index++) {
                            JSONObject blog = data.getJSONObject(index);

                            int postID = blog.getInt("id");

                            LoginManager.UserData author = new LoginManager.UserData();
                            author.id = blog.getInt("author_id");
                            author.nickname = blog.getString("nickname");

                            String content = blog.getString("body");

                            Bitmap[] pictureBitmaps = new Bitmap[0];
                            try {
                                JSONArray pictureArray = blog.getJSONArray("image");
                                pictureBitmaps = new Bitmap[pictureArray.length()];
                                for (int i = 0; i < pictureBitmaps.length; i++) {
                                    byte[] pictureBytes = Base64.decode(pictureArray.getString(i), Base64.DEFAULT);
                                    pictureBitmaps[i] = BitmapFactory.decodeByteArray(pictureBytes, 0, pictureBytes.length);
                                }
                            } catch (JSONException e) {  }

                            String time = blog.getString("created");

                            List<LoginManager.UserData> likers = new ArrayList<>();
                            try {
                                JSONArray likers_json = blog.getJSONArray("likers");
                                for (int i = 0; i < likers_json.length(); i++) {
                                    LoginManager.UserData liker = new LoginManager.UserData();
                                    JSONObject liker_json = likers_json.getJSONObject(i);
                                    liker.id = liker_json.getInt("id");
                                    liker.nickname = liker_json.getString("nickname");
                                    likers.add(liker);
                                }
                            } catch (JSONException e) {  }

                            List<LoginManager.UserData> commenters = new ArrayList<>();
                            List<String> comments = new ArrayList<>();
                            try {
                                JSONArray comments_json = blog.getJSONArray("comment");
                                for (int i = 0; i < comments_json.length(); i++) {
                                    JSONObject comment_json = comments_json.getJSONObject(i);

                                    LoginManager.UserData commenter = new LoginManager.UserData();
                                    commenter.id = comment_json.getInt("id");
                                    commenter.nickname = comment_json.getString("nickname");
                                    commenters.add(commenter);

                                    comments.add(comment_json.getString("comment"));
                                }
                            } catch (JSONException e) {  }

                            DataSource.add(new PostData(postID, author, content, pictureBitmaps, time, likers, commenters, comments));
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
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final CommunityCardViewHolder Holder = (CommunityCardViewHolder)holder;
        Holder.ivAvatar.setImageResource(R.drawable.avatar_demo);
        Holder.tvUsername.setText(DataSource.get(position).Author.nickname);
        Holder.tvContent.setText(DataSource.get(position).Content);

        Holder.glPictures.removeAllViews();
        if (DataSource.get(position).Pictures.length == 0) {
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(Holder.constraintLayout);
            constraintSet.constrainHeight(Holder.glPictures.getId(), 0);
            constraintSet.applyTo(Holder.constraintLayout);
        } else {
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
                    final int Position = position;
                    image.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Bundle bundle = new Bundle();
                            ArrayList<String> imageUrls = new ArrayList<>();
                            for (int i = 0; i < DataSource.get(Position).Pictures.length; i++) {
                                imageUrls.add(URL_BIGIMAGE + "?p_id=" + DataSource.get(Position).PostID + "&index=" + i);
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

        Holder.tvTime.setText(DataSource.get(position).Time);

        boolean liked = false;
        for (LoginManager.UserData liker : DataSource.get(position).Likers) {
            if (liker.id == ((MainActivity)parent).lmLoginManager.User.id) {
                liked = true;
                break;
            }
        }
        if (!liked) {
            Holder.btLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Holder.btLike.setBackgroundResource(R.drawable.ic_liked);

                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("id", ((MainActivity)parent).lmLoginManager.User.id);
                        jsonObject.put("p_id", DataSource.get(position).PostID);
                    } catch (JSONException e) { e.printStackTrace(); }
                    JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL_LIKE, jsonObject, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            if (jsonObject.isNull("error")) {
                                fetchData();
                            } else {
                                Holder.btLike.setBackgroundResource(R.drawable.ic_like);
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            Holder.btLike.setBackgroundResource(R.drawable.ic_like);
                        }
                    }) {
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            HashMap<String, String> headers = new HashMap<String, String>();
                            headers.put("Content-Type", "application/json");
                            return headers;
                        }
                    };
                    request.setTag(TAG_LIKE);
                    requestQueue.add(request);
                }
            });
        } else {
            Holder.btLike.setBackgroundResource(R.drawable.ic_liked);
            Holder.btLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Holder.btLike.setBackgroundResource(R.drawable.ic_like);

                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("id", ((MainActivity)parent).lmLoginManager.User.id);
                        jsonObject.put("p_id", DataSource.get(position).PostID);
                    } catch (JSONException e) { e.printStackTrace(); }
                    JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL_LIKE, jsonObject, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            if (jsonObject.isNull("error")) {
                                fetchData();
                            } else {
                                Holder.btLike.setBackgroundResource(R.drawable.ic_liked);
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            Holder.btLike.setBackgroundResource(R.drawable.ic_liked);
                        }
                    }) {
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            HashMap<String, String> headers = new HashMap<String, String>();
                            headers.put("Content-Type", "application/json");
                            return headers;
                        }
                    };
                    request.setTag(TAG_LIKE);
                    requestQueue.add(request);
                }
            });
        }

        if (DataSource.get(position).Likers.size() == 0) {
            Holder.tvLikers.setVisibility(View.GONE);
        } else {
            Holder.tvLikers.setVisibility(View.VISIBLE);

            SpannableStringBuilder spannableString = new SpannableStringBuilder();
            spannableString.append("\uD83D\uDDA4 ");
            for (LoginManager.UserData liker : DataSource.get(position).Likers) {
                spannableString.append(liker.nickname);
                final String name = liker.nickname;
                ClickableSpan clickableSpan = new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        Toast.makeText(parent.getApplicationContext(), name, Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void updateDrawState(TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setUnderlineText(false);
                    }
                };
                ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#515bd4"));
                spannableString.setSpan(clickableSpan, spannableString.length() - liker.nickname.length(), spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannableString.setSpan(colorSpan, spannableString.length() - liker.nickname.length(), spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannableString.append(", ");
            }
            spannableString.delete(spannableString.length() - 2, spannableString.length());
            Holder.tvLikers.setText(spannableString);
            Holder.tvLikers.setMovementMethod(LinkMovementMethod.getInstance());
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
        private TextView tvTime;
        private Button btLike;
        private Button btComment;
        private TextView tvLikers;
        private TextView tvComments;

        public CommunityCardViewHolder(View itemView) {
            super(itemView);
            constraintLayout = itemView.findViewById(R.id.constraintLayout_community_card);
            ivAvatar = itemView.findViewById(R.id.image_community_card_avatar);
            tvUsername = itemView.findViewById(R.id.text_community_card_username);
            tvContent = itemView.findViewById(R.id.text_community_card_content);
            glPictures = itemView.findViewById(R.id.gridLayout_community_card_picture);
            tvTime = itemView.findViewById(R.id.textView_community_card_time);
            btLike = itemView.findViewById(R.id.button_community_card_like);
            btComment = itemView.findViewById(R.id.button_community_card_comment);
            tvLikers = itemView.findViewById(R.id.textView_community_card_likes);
            tvComments = itemView.findViewById(R.id.textView_community_card_comments);
        }
    }
    public static class OnFetchCompleteListener {
        public void onFetchComplete() { }
    }
    public static class OnFetchFailedListener {
        public void onFetchFailed() { }
    }
    public static class PostData {
        int PostID;
        LoginManager.UserData Author;
        String Content;
        Bitmap[] Pictures;
        String Time;
        List<LoginManager.UserData> Likers;
        List<LoginManager.UserData> Commenters;
        List<String> Comments;

        public PostData(int postID, LoginManager.UserData author, String content, Bitmap[] pictures, String time, List<LoginManager.UserData> likers, List<LoginManager.UserData> commenters, List<String> comments) {
            PostID = postID;
            Author = author;
            Content = content;
            Pictures = pictures;
            Time = time;
            Likers = likers;
            Commenters = commenters;
            Comments = comments;
        }
    }
}
