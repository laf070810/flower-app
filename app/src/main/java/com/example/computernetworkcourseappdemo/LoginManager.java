package com.example.computernetworkcourseappdemo;

import android.content.Context;
import android.os.Environment;
import android.os.Parcelable;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;


public class LoginManager{
    private RequestQueue rqRequestQueue;
    private Context context;
    private boolean isLoggedIn = false;
    public UserData User = new UserData();

    private static final String FILENAME_LOGIN_CACHE = "login_cache";
    private static final String TAG_LOGIN = "TAG_LOGIN";
    private static final String TAG_REGISTER = "TAG_REGISTER";
    private static final String TAG_CHANGEPASSWORD = "TAG_CHANGEPASSWORD";
    private static final String TAG_UPDATE_USERDATA = "TAG_UPDATE_USERDATA";
    private static final String URL_LOGIN = "http://47.94.248.141:5000/auth/login";
    private static final String URL_LOGOUT = "http://47.94.248.141:5000/auth/logout";
    private static final String URL_REGISTER = "http://47.94.248.141:5000/auth/register";
    private static final String URL_CHANGEPASSWORD = "http://47.94.248.141:5000/auth/password_update";
    private static final String URL_UPDATE_USERDATA = "http://47.94.248.141:5000/auth/personal_info";

    public LoginManager(Context context, RequestQueue rqRequestQueue) {
        this.context = context;
        this.rqRequestQueue = rqRequestQueue;
    }
    public LoginManager(Context context, RequestQueue rqRequestQueue, String sUsername, String sPassword) {
        this.rqRequestQueue = rqRequestQueue;
        this.User.phone_number = sUsername;
        this.User.password = sPassword;
    }

    public UserData getUser() { return User; }
    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public boolean isCached() {
        File file = new File(context.getFilesDir(), FILENAME_LOGIN_CACHE);
        return file.exists();
    }

    public boolean register(final OnRegisteredListener onRegisteredListener, final OnRegisterFailedListener onRegisterFailedListener) {
        if (isLoggedIn) {
            return false;
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("phone_number", User.phone_number);
            jsonObject.put("password", User.password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL_REGISTER, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if (jsonObject.isNull("error")) {
                    onRegisteredListener.onRegistered(jsonObject);
                } else {
                    onRegisterFailedListener.onRegisterFailed(jsonObject);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                onRegisterFailedListener.onRegisterFailed(null);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        rqRequestQueue.add(request);

        return true;
    }
    public boolean cancelRegister() {
        rqRequestQueue.cancelAll(TAG_REGISTER);
        return true;
    }

    public boolean login(final OnLoggedInListener onLoggedInListener, final OnLoginFailedListener onLoginFailedListener) {
        if (isLoggedIn) {
            return false;
        }
        if (isCached()) {
            readCache();
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("phone_number", User.phone_number);
            jsonObject.put("password", User.password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL_LOGIN, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if (jsonObject.isNull("error")) {
                    for (Field field : UserData.class.getDeclaredFields()) {
                        if (!jsonObject.isNull(field.getName())) {
                            try {
                                Method m = UserData.class.getDeclaredMethod("set" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1), field.getType());
                                m.invoke(User, jsonObject.get(field.getName()));
                            } catch (Exception e) { e.printStackTrace();}
                        }
                    }

                    isLoggedIn = true;
                    writeCache();

                    onLoggedInListener.onLoggedIn(jsonObject);
                } else {
                    onLoginFailedListener.onLoginFailed(jsonObject);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                onLoginFailedListener.onLoginFailed(null);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        request.setTag(TAG_LOGIN);
        rqRequestQueue.add(request);
        
        return true;
    }
    public boolean cancelLogin() {
        rqRequestQueue.cancelAll(TAG_LOGIN);
        return true;
    }
    public boolean logout() {
        if (!isLoggedIn) {
            return false;
        }
        if (isCached()) {
            File file = new File(context.getFilesDir(), FILENAME_LOGIN_CACHE);
            file.delete();
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", User.id);
            jsonObject.put("password", User.password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL_LOGOUT, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if (jsonObject.isNull("error")) {

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        rqRequestQueue.add(request);
        isLoggedIn = false;

        return true;
    }
    public boolean changePassword(String oldPassword, final String newPassword, final OnChangedPasswordListener onChangedPasswordListener, final OnChangePasswordFailedListener onChangePasswordFailedListener) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", User.id);
            jsonObject.put("old_password", oldPassword);
            jsonObject.put("new_password", newPassword);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL_CHANGEPASSWORD, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if (jsonObject.isNull("error")) {
                    User.password = newPassword;
                    writeCache();
                    onChangedPasswordListener.onChangedPassword(jsonObject);
                } else {
                    onChangePasswordFailedListener.onChangePasswordFailed(jsonObject);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                onChangePasswordFailedListener.onChangePasswordFailed(null);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        request.setTag(TAG_CHANGEPASSWORD);
        rqRequestQueue.add(request);

        return true;
    }
    public boolean updateUserData(final OnUpdatedUserDataListener onUpdatedUserDataListener, final OnUpdateUserDataFailedListerner onUpdateUserDataFailedListerner) {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", User.id);
            jsonObject.put("phone_number", User.phone_number);
            jsonObject.put("nickname", User.nickname);
            jsonObject.put("personal_description", User.personal_description);
            jsonObject.put("age", User.age);
            jsonObject.put("region", User.region);
            jsonObject.put("head", User.head);
            jsonObject.put("sex", User.sex);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL_UPDATE_USERDATA, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                if (jsonObject.isNull("error")) {
                    onUpdatedUserDataListener.onUpdatedUserData(jsonObject);
                } else {
                    onUpdateUserDataFailedListerner.onUpdateUserDataFailed(jsonObject);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                onUpdateUserDataFailedListerner.onUpdateUserDataFailed(jsonObject);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        request.setTag(TAG_UPDATE_USERDATA);
        rqRequestQueue.add(request);

        return true;
    }
    public boolean readCache() {
        try {
            DataInputStream dis = new DataInputStream(context.openFileInput(FILENAME_LOGIN_CACHE));
            int PhoneNumberLength = dis.readInt();
            int PasswordLength = dis.readInt();

            StringBuffer buf = new StringBuffer();
            for (int i = 0; i < PhoneNumberLength; i++) {
                buf.append(dis.readChar());
            }
            User.phone_number = buf.toString();

            buf = new StringBuffer();
            for (int i = 0; i < PasswordLength; i++) {
                buf.append(dis.readChar());
            }
            User.password = buf.toString();

            dis.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean writeCache() {
        try {
            DataOutputStream dos = new DataOutputStream(context.openFileOutput(FILENAME_LOGIN_CACHE, Context.MODE_PRIVATE));
            dos.writeInt(User.phone_number.getBytes().length);
            dos.writeInt(User.password.getBytes().length);
            dos.writeChars(User.phone_number);
            dos.writeChars(User.password);
            dos.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static class OnLoggedInListener {
        public void onLoggedIn(JSONObject response) {
        }
    }
    public static class OnLoginFailedListener {
        public void onLoginFailed(JSONObject response) {
        }
    }
    public static class OnRegisteredListener {
        public void onRegistered(JSONObject response) {
        }
    }
    public static class OnRegisterFailedListener {
        public void onRegisterFailed(JSONObject response) {

        }
    }
    public static class OnChangedPasswordListener {
        public void onChangedPassword(JSONObject response) {

        }
    }
    public static class OnChangePasswordFailedListener{
        public void onChangePasswordFailed(JSONObject response) {

        }
    }
    public static class OnUpdatedUserDataListener {
        public void onUpdatedUserData (JSONObject response) {

        }
    }
    public static class OnUpdateUserDataFailedListerner {
        public void onUpdateUserDataFailed (JSONObject response) {

        }
    }
    public static class OnLoggedOutListener {
        public void onLoggedOut(JSONObject response) { }
    }

    public static class UserData implements Serializable {
        public int id = 0;
        public String phone_number = "";
        public String password = "";
        public String nickname = "";
        public String head = "";
        public int level = 0;
        public int EXPoint = 0;
        public String friend = "";
        public String personal_description = "";
        public String sex = "";
        public int age = 0;
        public String region = "";

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getPhone_number() {
            return phone_number;
        }

        public void setPhone_number(String phone_number) {
            this.phone_number = phone_number;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getHead() {
            return head;
        }

        public void setHead(String head) {
            this.head = head;
        }

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public int getEXPoint() {
            return EXPoint;
        }

        public void setEXPoint(int EXPoint) {
            this.EXPoint = EXPoint;
        }

        public String getFriend() {
            return friend;
        }

        public void setFriend(String friend) {
            this.friend = friend;
        }

        public String getPersonal_description() {
            return personal_description;
        }

        public void setPersonal_description(String personal_description) {
            this.personal_description = personal_description;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public String getRegion() {
            return region;
        }

        public void setRegion(String region) {
            this.region = region;
        }
    }
}
