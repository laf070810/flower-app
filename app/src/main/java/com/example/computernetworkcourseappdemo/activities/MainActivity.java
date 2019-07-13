package com.example.computernetworkcourseappdemo.activities;

import android.animation.Animator;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.computernetworkcourseappdemo.CommunityRvAdapter;
import com.example.computernetworkcourseappdemo.CommunityRvItemDecoration;
import com.example.computernetworkcourseappdemo.CommunityVpAdapter;
import com.example.computernetworkcourseappdemo.HomepageRvAdapter;
import com.example.computernetworkcourseappdemo.LoginManager;
import com.example.computernetworkcourseappdemo.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    //    Declare views.
    BottomNavigationView navView;
    ConstraintLayout layoutContent;
    View viewHomepage;
    View viewCommunity;
    View viewCourse;
    View viewMine;

    FloatingActionButton HomepageFloatingButton;
    SwipeRefreshLayout HomepageSwipeRefresh;
    RecyclerView HomepageRv;

    TabLayout CommunityTl;
    ViewPager CommunityVp;
    SwipeRefreshLayout[] CommunitySwipeRefresh;
    RecyclerView[] CommunityRv;
    CommunityRvAdapter[] communityRvAdapters = new CommunityRvAdapter[2];
    Button btCommunityPublish;

    Button btMineSettings;
    ImageView MineAvatar;
    TextView MineNickname;
    TextView MinePersonalPage;

    //    Declare important objects.
    private RequestQueue rqRequestQueue;
    private LoginManager lmLoginManager;

    //    Declare constants.
    public static final int REQUEST_CODE_LOGIN = 100;
    public static final int REQUEST_CODE_REGISTER = 101;
    public static final int REQUEST_CODE_PUBLISH = 103;
    public static final int REQUEST_CODE_SETTINGS = 104;
    public static final int REQUEST_CODE_FLOWER = 105;
    public static final int REQUEST_CODE_PUBLISH_SELECT_PICTURE = 106;
    public static final int RESULT_CODE_LOGIN_FILLED = 100;
    public static final int RESULT_CODE_REGISTER_FILLED = 101;
    public static final int RESULT_CODE_JUMP_TO_REGISTER = 102;
    public static final int RESULT_CODE_PUBLISH_SUCCESS = 103;
    public static final int RESULT_CODE_SETTINGS_LOGOUT = 104;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_flower:
                    layoutContent.removeAllViews();
                    layoutContent.addView(viewHomepage);
                    return true;
                case R.id.navigation_community:
                    layoutContent.removeAllViews();
                    layoutContent.addView(viewCommunity);
                    return true;
                case R.id.navigation_course:
                    layoutContent.removeAllViews();
                    layoutContent.addView(viewCourse);
                    return true;
                case R.id.navigation_my:
                    if (lmLoginManager.isLoggedIn()) {
                        layoutContent.removeAllViews();
                        layoutContent.addView(viewMine);
                        return true;
                    } else if (lmLoginManager.isCached()) {
                        loginWithToast();
                        layoutContent.removeAllViews();
                        layoutContent.addView(viewMine);
                        return true;
                    } else {
                        Intent it = new Intent(MainActivity.this, LoginActivity.class);
                        startActivityForResult(it, REQUEST_CODE_LOGIN);
                        return false;
                    }
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

//        Inflate views and find views.
        layoutContent = (ConstraintLayout) findViewById(R.id.layout_content);

        viewHomepage = LayoutInflater.from(this).inflate(R.layout.main_homepage, layoutContent, false);
        viewCommunity = LayoutInflater.from(this).inflate(R.layout.main_community, layoutContent, false);
        viewCourse = LayoutInflater.from(this).inflate(R.layout.main_course, layoutContent, false);
        viewMine = LayoutInflater.from(this).inflate(R.layout.main_mine, layoutContent, false);

        HomepageFloatingButton = viewHomepage.findViewById(R.id.button_homepage_flower);
        HomepageSwipeRefresh = viewHomepage.findViewById(R.id.srl_homepage);
        HomepageRv = viewHomepage.findViewById(R.id.rv_homepage);

        CommunityTl = viewCommunity.findViewById(R.id.tabLayout_community);
        CommunityVp = viewCommunity.findViewById(R.id.vp_community);
        CommunitySwipeRefresh = new SwipeRefreshLayout[2];
        CommunitySwipeRefresh[0] = (SwipeRefreshLayout) LayoutInflater.from(this).inflate(R.layout.main_community_swipe_refresh, (ViewPager) viewCommunity.findViewById(R.id.vp_community), false);
        CommunitySwipeRefresh[1] = (SwipeRefreshLayout) LayoutInflater.from(this).inflate(R.layout.main_community_swipe_refresh, (ViewPager) viewCommunity.findViewById(R.id.vp_community), false);
        CommunityRv = new RecyclerView[2];
        CommunityRv[0] = CommunitySwipeRefresh[0].findViewById(R.id.rv_community);
        CommunityRv[1] = CommunitySwipeRefresh[1].findViewById(R.id.rv_community);
        btCommunityPublish = (Button) viewCommunity.findViewById(R.id.button_community_add);

        btMineSettings = (Button) viewMine.findViewById(R.id.button_mine_settings);
        MineAvatar = (ImageView) viewMine.findViewById(R.id.imageView_mine_avatar);
        MineNickname = (TextView) viewMine.findViewById(R.id.textView_mine_nickname);
        MinePersonalPage = (TextView) viewMine.findViewById(R.id.text_mine_personalpage);

//        Initialize network config.
        rqRequestQueue = Volley.newRequestQueue(getApplicationContext());
        rqRequestQueue.start();

        lmLoginManager = new LoginManager(getApplicationContext(), rqRequestQueue);

//        Setup homepage.
        HomepageFloatingButton.addOnShowAnimationListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                Intent it = new Intent(MainActivity.this, FlowerActivity.class);
                startActivityForResult(it, REQUEST_CODE_FLOWER);
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        HomepageRvAdapter HomepageAdapter = new HomepageRvAdapter(rqRequestQueue);
        HomepageRv.setAdapter(HomepageAdapter);
        HomepageRv.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false));

        HomepageSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                HomepageSwipeRefresh.setRefreshing(false);
            }
        });

//        Setup community page.
        CommunityTl.setupWithViewPager(CommunityVp);

        List<View> listSwipe = new ArrayList<>();
        listSwipe.add(CommunitySwipeRefresh[0]);
        listSwipe.add(CommunitySwipeRefresh[1]);
        List<String> titles = new ArrayList<>();
        titles.add(getResources().getString(R.string.main_community_tab_square));
        titles.add(getResources().getString(R.string.main_community_tab_follow));
        CommunityVp.setAdapter(new CommunityVpAdapter(listSwipe, titles));

        communityRvAdapters[0] = new CommunityRvAdapter(this, rqRequestQueue);
        CommunityRv[0].setAdapter(communityRvAdapters[0]);
        CommunityRv[0].setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false));
        CommunityRv[0].addItemDecoration(new DividerItemDecoration(MainActivity.this, DividerItemDecoration.VERTICAL));
        CommunityRv[0].addItemDecoration(new CommunityRvItemDecoration());

        CommunitySwipeRefresh[0].setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                communityRvAdapters[0].fetchData(new CommunityRvAdapter.OnFetchCompleteListener() {
                    @Override
                    public void onFetchComplete() {
                        super.onFetchComplete();
                        CommunitySwipeRefresh[0].setRefreshing(false);
                    }
                }, new CommunityRvAdapter.OnFetchFailedListener() {
                    @Override
                    public void onFetchFailed() {
                        super.onFetchFailed();
                        CommunitySwipeRefresh[0].setRefreshing(false);
                    }
                });
            }
        });

        communityRvAdapters[1] = new CommunityRvAdapter(this, rqRequestQueue);
        CommunityRv[1].setAdapter(communityRvAdapters[1]);
        CommunityRv[1].setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false));
        CommunityRv[1].addItemDecoration(new DividerItemDecoration(MainActivity.this, DividerItemDecoration.VERTICAL));
        CommunityRv[1].addItemDecoration(new CommunityRvItemDecoration());

        CommunitySwipeRefresh[1].setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                communityRvAdapters[1].fetchData(new CommunityRvAdapter.OnFetchCompleteListener() {
                    @Override
                    public void onFetchComplete() {
                        super.onFetchComplete();
                        CommunitySwipeRefresh[1].setRefreshing(false);
                    }
                }, new CommunityRvAdapter.OnFetchFailedListener() {
                    @Override
                    public void onFetchFailed() {
                        super.onFetchFailed();
                        CommunitySwipeRefresh[1].setRefreshing(false);
                    }
                });
            }
        });

        btCommunityPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lmLoginManager.isLoggedIn()) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("User", lmLoginManager.getUser());
                    Intent it = new Intent(MainActivity.this, PublishActivity.class);
                    it.putExtras(bundle);
                    startActivityForResult(it, REQUEST_CODE_PUBLISH);
                } else if (lmLoginManager.isCached()) {
                    loginWithToast();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("User", lmLoginManager.getUser());
                    Intent it = new Intent(MainActivity.this, PublishActivity.class);
                    it.putExtras(bundle);
                    startActivityForResult(it, REQUEST_CODE_PUBLISH);
                } else {
                    Intent it = new Intent(MainActivity.this, LoginActivity.class);
                    startActivityForResult(it, REQUEST_CODE_LOGIN);
                }
            }
        });

//        Setup Mine page.
        btMineSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(MainActivity.this, SettingsActivity.class);
                startActivityForResult(it, REQUEST_CODE_SETTINGS);
            }
        });
        MinePersonalPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(MainActivity.this, PersonalPageActivity.class);
                startActivity(it);
            }
        });

//        Add initial view.
        layoutContent.addView(viewHomepage);

//        Initialize ButterKnife.
        ButterKnife.bind(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_LOGIN && resultCode == RESULT_CODE_LOGIN_FILLED) {
            Bundle bundle = data.getBundleExtra("USER_DATA");
            String username = bundle.getString("Username");
            String password = bundle.getString("Password");
            lmLoginManager.User.setPhone_number(username);
            lmLoginManager.User.setPassword(password);
            loginWithDialog();
        } else if (requestCode == REQUEST_CODE_LOGIN && resultCode == RESULT_CODE_JUMP_TO_REGISTER) {
            Intent it = new Intent(MainActivity.this, RegisterActivity.class);
            startActivityForResult(it, REQUEST_CODE_REGISTER);
        } else if (requestCode == REQUEST_CODE_REGISTER && resultCode == RESULT_CODE_REGISTER_FILLED) {
            Bundle bundle = data.getBundleExtra("USER_DATA");
            String phone_number = bundle.getString("PhoneNumber");
            String nickname = bundle.getString("Nickname");
            String password = bundle.getString("Password");
            lmLoginManager.User.setPhone_number(phone_number);
            lmLoginManager.User.setNickname(nickname);
            lmLoginManager.User.setPassword(password);
            registerWithDialog();
        } else if (requestCode == REQUEST_CODE_PUBLISH && resultCode == RESULT_CODE_PUBLISH_SUCCESS) {
            communityRvAdapters[0].fetchData();
            communityRvAdapters[1].fetchData();
        } else if (requestCode == REQUEST_CODE_SETTINGS && resultCode == RESULT_CODE_SETTINGS_LOGOUT) {
            lmLoginManager.logout();
            navView.setSelectedItemId(R.id.navigation_flower);
        }
    }

    public void loginWithToast() {
        lmLoginManager.login(new LoginManager.OnLoggedInListener() {
            @Override
            public void onLoggedIn(JSONObject response) {
                super.onLoggedIn(response);
                try {
                    byte[] avatar_bytes = Base64.decode(lmLoginManager.User.getHead(), Base64.DEFAULT);
                    MineNickname.setText(lmLoginManager.User.getNickname());
                    MineAvatar.setImageBitmap(BitmapFactory.decodeByteArray(avatar_bytes, 0, avatar_bytes.length));
//                                    Toast.makeText(getApplicationContext(), response.getString("msg"), Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new LoginManager.OnLoginFailedListener() {
            @Override
            public void onLoginFailed(JSONObject response) {
                super.onLoginFailed(response);
                if (response == null) {
                    Toast.makeText(getApplicationContext(), R.string.login_dialog_failed, Toast.LENGTH_LONG).show();
                } else {
                    try {
                        Toast.makeText(getApplicationContext(), response.getString("error"), Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
    public void loginWithDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.login_dialog_title);
        builder.setNegativeButton(R.string.login_dialog_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (!lmLoginManager.isLoggedIn()) {
                    lmLoginManager.cancelLogin();
                }
            }
        });
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (!lmLoginManager.isLoggedIn()) {
                    lmLoginManager.cancelLogin();
                }
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (!lmLoginManager.isLoggedIn()) {
                    lmLoginManager.cancelLogin();
                }
            }
        });
        final AlertDialog dialog = builder.create();
        dialog.show();

        lmLoginManager.login(new LoginManager.OnLoggedInListener() {
            @Override
            public void onLoggedIn(JSONObject response) {
                super.onLoggedIn(response);
                dialog.dismiss();
                try {
                    byte[] avatar_bytes = Base64.decode(lmLoginManager.User.getHead(), Base64.DEFAULT);
                    MineNickname.setText(lmLoginManager.User.getNickname());
                    MineAvatar.setImageBitmap(BitmapFactory.decodeByteArray(avatar_bytes, 0, avatar_bytes.length));
//                    Toast.makeText(getApplicationContext(), response.getString("msg"), Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new LoginManager.OnLoginFailedListener() {
            @Override
            public void onLoginFailed(JSONObject response) {
                super.onLoginFailed(response);
                if (response == null) {
                    dialog.setTitle(R.string.login_dialog_failed);
                } else {
                    try {
                        dialog.setTitle(response.getString("error"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
    public void registerWithDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.register_dialog_title);
        builder.setNegativeButton(R.string.register_dialog_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                lmLoginManager.cancelRegister();
            }
        });
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                lmLoginManager.cancelRegister();
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                lmLoginManager.cancelRegister();
            }
        });
        final AlertDialog dialog = builder.create();
        dialog.show();

        lmLoginManager.register(new LoginManager.OnRegisteredListener() {
            @Override
            public void onRegistered(JSONObject response) {
                super.onRegistered(response);
                dialog.dismiss();
                builder.setTitle(getResources().getString(R.string.register_dialog_success));
                builder.setNegativeButton(getResources().getString(R.string.register_dialog_confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        loginWithDialog();
                    }
                });
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        loginWithDialog();
                    }
                });
                builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        loginWithDialog();
                    }
                });
                builder.create().show();
            }
        }, new LoginManager.OnRegisterFailedListener() {
            @Override
            public void onRegisterFailed(JSONObject response) {
                super.onRegisterFailed(response);
                dialog.dismiss();
                if (response == null) {
                    builder.setTitle(R.string.register_dialog_failed);
                } else {
                    try {
                        builder.setTitle(response.getString("error"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                builder.create().show();
            }
        });
    }
}