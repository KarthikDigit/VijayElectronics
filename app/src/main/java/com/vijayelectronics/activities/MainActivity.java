package com.vijayelectronics.activities;


import android.annotation.SuppressLint;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;

import androidx.fragment.app.FragmentTransaction;

import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.os.Handler;

import com.google.android.material.navigation.NavigationView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.core.graphics.drawable.WrappedDrawable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.graphics.drawable.DrawableWrapper;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.MobileAds;
import com.vijayelectronics.app.App;
import com.vijayelectronics.customs.CircularImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vijayelectronics.R;
import com.vijayelectronics.fragments.CurrencyFrag;
import com.vijayelectronics.fragments.Download;
import com.vijayelectronics.fragments.HomePage_1;
import com.vijayelectronics.fragments.HomePage_10;
import com.vijayelectronics.fragments.HomePage_2;
import com.vijayelectronics.fragments.HomePage_3;
import com.vijayelectronics.fragments.HomePage_4;
import com.vijayelectronics.fragments.HomePage_5;
import com.vijayelectronics.fragments.Categories_1;
import com.vijayelectronics.fragments.Categories_2;
import com.vijayelectronics.fragments.Categories_3;
import com.vijayelectronics.fragments.Categories_4;
import com.vijayelectronics.fragments.Categories_5;
import com.vijayelectronics.fragments.Categories_6;
import com.vijayelectronics.fragments.HomePage_6;
import com.vijayelectronics.fragments.HomePage_7;
import com.vijayelectronics.fragments.HomePage_8;
import com.vijayelectronics.fragments.HomePage_9;
import com.vijayelectronics.fragments.Languages;
import com.vijayelectronics.fragments.News;
import com.vijayelectronics.fragments.About;
import com.vijayelectronics.fragments.My_Cart;
import com.vijayelectronics.fragments.NotificationFrag;
import com.vijayelectronics.fragments.Points_Fragment;
import com.vijayelectronics.fragments.Products;
import com.vijayelectronics.fragments.Shipping_Address;
import com.vijayelectronics.fragments.WishList;
import com.vijayelectronics.fragments.My_Orders;
import com.vijayelectronics.fragments.ContactUs;
import com.vijayelectronics.fragments.Update_Account;
import com.vijayelectronics.fragments.SearchFragment;
import com.vijayelectronics.fragments.SettingsFragment;
import com.vijayelectronics.network.StartAppRequests;
import com.vijayelectronics.utils.ExampleNotificationOpenedHandler;
import com.vijayelectronics.utils.Utilities;
import com.vijayelectronics.utils.LocaleHelper;
import com.vijayelectronics.utils.NotificationScheduler;
import com.vijayelectronics.app.MyAppPrefsManager;
import com.vijayelectronics.receivers.AlarmReceiver;
import com.vijayelectronics.constant.ConstantValues;
import com.vijayelectronics.customs.NotificationBadger;
import com.vijayelectronics.adapters.DrawerExpandableListAdapter;
import com.vijayelectronics.models.drawer_model.Drawer_Items;
import com.vijayelectronics.models.device_model.AppSettingsDetails;

import static com.vijayelectronics.activities.SplashScreen.startAppRequests;


/**
 * MainActivity of the App
 **/

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    Toolbar toolbar;
    ActionBar actionBar;

    ImageView drawer_header;
    DrawerLayout drawerLayout;
    NavigationView navigationDrawer;

    SharedPreferences sharedPreferences;
    MyAppPrefsManager myAppPrefsManager;

    ExpandableListView main_drawer_list;
    DrawerExpandableListAdapter drawerExpandableAdapter;

    boolean doublePressedBackToExit = false;

    private static String mSelectedItem;
    private static final String SELECTED_ITEM_ID = "selected";
    public static ActionBarDrawerToggle actionBarDrawerToggle;

    List<Drawer_Items> listDataHeader = new ArrayList<>();
    Map<Drawer_Items, List<Drawer_Items>> listDataChild = new HashMap<>();

    AppSettingsDetails appSettings;

    String product_ID;

    //*********** Called when the Activity is becoming Visible to the User ********//

    @Override
    protected void onStart() {
        super.onStart();

        if (myAppPrefsManager.isFirstTimeLaunch()) {
            NotificationScheduler.setReminder(MainActivity.this, AlarmReceiver.class);
            if (ConstantValues.DEFAULT_NOTIFICATION.equalsIgnoreCase("onesignal")) {
                StartAppRequests.RegisterDeviceForFCM(MainActivity.this);
            }

        }

        myAppPrefsManager.setFirstTimeLaunch(false);
//        User_Cart_DB.initCartInstance();

    }

    //*********** Called when the Activity is first Created ********//

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        appSettings = ((App) getApplicationContext()).getAppSettingsDetails();
        MobileAds.initialize(this, ConstantValues.ADMOBE_ID);

        // Get MyAppPrefsManager
        myAppPrefsManager = new MyAppPrefsManager(MainActivity.this);
        sharedPreferences = getSharedPreferences("UserInfo", MODE_PRIVATE);


        // Binding Layout Views
        toolbar = (Toolbar) findViewById(R.id.myToolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationDrawer = (NavigationView) findViewById(R.id.main_drawer);

        // Get ActionBar and Set the Title and HomeButton of Toolbar
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setTitle(ConstantValues.APP_HEADER);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Setup Expandable DrawerList
        setupExpandableDrawerList();

        // Setup Expandable Drawer Header
        setupExpandableDrawerHeader();
        // Initialize ActionBarDrawerToggle
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawerOpen, R.string.drawerClose) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // Hide OptionsMenu
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                // Recreate the OptionsMenu
                invalidateOptionsMenu();
            }
        };


        // Add ActionBarDrawerToggle to DrawerLayout
        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        // Synchronize the indicator with the state of the linked DrawerLayout
        actionBarDrawerToggle.syncState();

        // Get the default ToolbarNavigationClickListener
        final View.OnClickListener toggleNavigationClickListener = actionBarDrawerToggle.getToolbarNavigationClickListener();

        // Handle ToolbarNavigationClickListener with OnBackStackChangedListener
        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {

                // Check BackStackEntryCount of FragmentManager
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {

                    // Set new ToolbarNavigationClickListener
                    actionBarDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Close the Drawer if Opened
                            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                                drawerLayout.closeDrawers();
                            } else {
                                // Pop previous Fragment from BackStack
                                getSupportFragmentManager().popBackStack();
                            }
                        }
                    });


                } else {
                    // Set DrawerToggle Indicator and default ToolbarNavigationClickListener
                    actionBar.setTitle(ConstantValues.APP_HEADER);
                    actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
                    actionBarDrawerToggle.setToolbarNavigationClickListener(toggleNavigationClickListener);
                }
            }
        });

        if (appSettings != null) {

            // Select SELECTED_ITEM from SavedInstanceState
            mSelectedItem = savedInstanceState == null ? getString(R.string.actionHome) + " " + appSettings.getHomeStyle() : savedInstanceState.getString(SELECTED_ITEM_ID);
            // Navigate to SelectedItem
            drawerSelectedItemNavigation(mSelectedItem);

            Log.e(TAG, "RequestAppSetting " + mSelectedItem);

        } else {
            mSelectedItem = getString(R.string.homeStyle1);
            drawerSelectedItemNavigation(mSelectedItem);
        }

        // This if condition indicate if user tab on notification outside of application
        if (ExampleNotificationOpenedHandler.Notification_FLAG.equalsIgnoreCase("1")) {
            Handler handler = new Handler();  // Use this handle because the main fragment need to some delay
            // for replacing its view
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    drawerSelectedItemNavigation(getString(R.string.push_notification));
                }
            }, 4000);

        }

        // This is local braodcast receiver if user click on in app notification.
        // there will be a trigger fire which will let it know either it should replace to notification Fragment
        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(this);
        lbm.registerReceiver(receiver, new IntentFilter(ConstantValues.NOTIFICATION_MAIN));


        new MyExtraTasks().execute();

    }

    //Check if it is from notification Click
    public BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                String notiCheck = intent.getStringExtra(ConstantValues.NOTIFICATION_FLAG);
                if (notiCheck != null && notiCheck.equalsIgnoreCase("1")) {
                    drawerSelectedItemNavigation(getString(R.string.push_notification));
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onDestroy();
    }
    //*********** Setup Header of Navigation Drawer ********//

    public void setupExpandableDrawerHeader() {

        // Binding Layout Views of DrawerHeader
        drawer_header = (ImageView) findViewById(R.id.drawer_header);
        CircularImageView drawer_profile_image = (CircularImageView) findViewById(R.id.drawer_profile_image);
        TextView drawer_profile_name = (TextView) findViewById(R.id.drawer_profile_name);
        TextView drawer_profile_email = (TextView) findViewById(R.id.drawer_profile_email);
        TextView drawer_profile_welcome = (TextView) findViewById(R.id.drawer_profile_welcome);

        // Check if the User is Authenticated
        if (ConstantValues.IS_USER_LOGGED_IN) {
            // Check User's Info from SharedPreferences
            if (!TextUtils.isEmpty(sharedPreferences.getString("userEmail", ""))) {

                // Set User's Name, Email and Photo
                drawer_profile_email.setText(sharedPreferences.getString("userEmail", ""));

                if (!TextUtils.isEmpty(sharedPreferences.getString("userDisplayName", ""))) {
                    drawer_profile_name.setText(sharedPreferences.getString("userDisplayName", ""));
                } else {
                    drawer_profile_name.setText(sharedPreferences.getString("userName", ""));
                }

                if (!TextUtils.isEmpty(sharedPreferences.getString("userPicture", "")))
                    Glide.with(this)
                            .load(sharedPreferences.getString("userPicture", "")).asBitmap()
                            .placeholder(R.drawable.profile)
                            .error(R.drawable.profile)
                            .into(drawer_profile_image);

            } else {
                // Set Default Name, Email and Photo
                drawer_profile_image.setImageResource(R.drawable.profile);
                drawer_profile_name.setText(getString(R.string.login_or_signup));
                drawer_profile_email.setText(getString(R.string.login_or_create_account));
            }

        } else {
            // Set Default Name, Email and Photo
            drawer_profile_welcome.setVisibility(View.GONE);
            drawer_profile_image.setImageResource(R.drawable.profile);
            drawer_profile_name.setText(getString(R.string.login_or_signup));
            drawer_profile_email.setText(getString(R.string.login_or_create_account));
        }


        // Handle DrawerHeader Click Listener
        drawer_header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Check if the User is Authenticated
                if (ConstantValues.IS_USER_LOGGED_IN) {

                    // Navigate to Update_Account Fragment
                    Fragment fragment = new SettingsFragment();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.main_fragment, fragment)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .addToBackStack(getString(R.string.actionHome)).commit();

                    // Close NavigationDrawer
                    drawerLayout.closeDrawers();

                } else {
                    // Navigate to Login Activity
                    startActivity(new Intent(MainActivity.this, Login.class));
                    finish();
                    overridePendingTransition(R.anim.enter_from_left, R.anim.exit_out_left);
                }
            }
        });
    }

    //*********** Setup Expandable List of Navigation Drawer ********//

    public void setupExpandableDrawerList() {

        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();


        if (appSettings != null) {

            
        /*    listDataHeader.add(new Drawer_Items(R.drawable.ic_home, getString(R.string.actionHome)));
            listDataHeader.add(new Drawer_Items(R.drawable.ic_categories, getString(R.string.actionCategories)));
            listDataHeader.add(new Drawer_Items(R.drawable.ic_cart, getString(R.string.actionShop)));
    
            if ("1".equalsIgnoreCase(appSettings.getOne_signal_notification()))
                listDataHeader.add(new Drawer_Items(R.drawable.ic_action_notifications,getString(R.string.push_notification)));
            if ("1".equalsIgnoreCase(appSettings.getEditProfilePage()))
                listDataHeader.add(new Drawer_Items(R.drawable.ic_account, getString(R.string.actionAccount)));
            if ("1".equalsIgnoreCase(appSettings.getMyOrdersPage()))
                listDataHeader.add(new Drawer_Items(R.drawable.ic_order, getString(R.string.actionOrders)));
            if ("1".equalsIgnoreCase(appSettings.getWp_point_reward()))
                listDataHeader.add(new Drawer_Items(R.drawable.ic_action_medal, getString(R.string.actionRewards)));
            if ("1".equalsIgnoreCase(appSettings.getWishListPage()))
                listDataHeader.add(new Drawer_Items(R.drawable.ic_favorite, getString(R.string.actionFavourites)));
           
            if ("1".equalsIgnoreCase(appSettings.getIntroPage()))
                listDataHeader.add(new Drawer_Items(R.drawable.ic_intro, getString(R.string.actionIntro)));
            if ("1".equalsIgnoreCase(appSettings.getBill_ship_info()))
                if (ConstantValues.IS_USER_LOGGED_IN) {
                    listDataHeader.add(new Drawer_Items(R.drawable.map_marker, getString(R.string.address_info)));
                }
            if ("1".equalsIgnoreCase(appSettings.getNewsPage()))
                listDataHeader.add(new Drawer_Items(R.drawable.ic_newspaper, getString(R.string.actionNews)));
            if ("1".equalsIgnoreCase(appSettings.getContactUsPage()))
                listDataHeader.add(new Drawer_Items(R.drawable.ic_chat_bubble, getString(R.string.actionContactUs)));
            if ("1".equalsIgnoreCase(appSettings.getAboutUsPage()))
                listDataHeader.add(new Drawer_Items(R.drawable.ic_info, getString(R.string.actionAbout)));
            if ("1".equalsIgnoreCase(appSettings.getShareApp()))
                listDataHeader.add(new Drawer_Items(R.drawable.ic_share, getString(R.string.actionShareApp)));
            if ("1".equalsIgnoreCase(appSettings.getRateApp()))
                listDataHeader.add(new Drawer_Items(R.drawable.ic_star_circle, getString(R.string.actionRateApp)));
            if ("1".equalsIgnoreCase(appSettings.getDownloads()))
                if (ConstantValues.IS_USER_LOGGED_IN) {
                    listDataHeader.add(new Drawer_Items(R.drawable.download, getString(R.string.download)));
                }
            if ("1".equalsIgnoreCase(appSettings.getSettingPage()))
                listDataHeader.add(new Drawer_Items(R.drawable.ic_settings, getString(R.string.actionSettings)));
            
            // Add last Header Item in Drawer Header List
            if (ConstantValues.IS_USER_LOGGED_IN) {
                listDataHeader.add(new Drawer_Items(R.drawable.ic_logout, getString(R.string.actionLogout)));
            } else {
                listDataHeader.add(new Drawer_Items(R.drawable.ic_logout, getString(R.string.actionLogin)));
            }
            
            
            if (!ConstantValues.IS_CLIENT_ACTIVE) {
                List<Drawer_Items> home_styles = new ArrayList<>();
                home_styles.add(new Drawer_Items(R.drawable.ic_home, getString(R.string.homeStyle1)));
                home_styles.add(new Drawer_Items(R.drawable.ic_home, getString(R.string.homeStyle2)));
                home_styles.add(new Drawer_Items(R.drawable.ic_home, getString(R.string.homeStyle3)));
                home_styles.add(new Drawer_Items(R.drawable.ic_home, getString(R.string.homeStyle4)));
                home_styles.add(new Drawer_Items(R.drawable.ic_home, getString(R.string.homeStyle5)));
                home_styles.add(new Drawer_Items(R.drawable.ic_home, getString(R.string.homeStyle6)));
                home_styles.add(new Drawer_Items(R.drawable.ic_home, getString(R.string.homeStyle7)));
                home_styles.add(new Drawer_Items(R.drawable.ic_home, getString(R.string.homeStyle8)));
                home_styles.add(new Drawer_Items(R.drawable.ic_home, getString(R.string.homeStyle9)));
                home_styles.add(new Drawer_Items(R.drawable.ic_home, getString(R.string.homeStyle10)));

                List<Drawer_Items> category_styles = new ArrayList<>();
                category_styles.add(new Drawer_Items(R.drawable.ic_categories, getString(R.string.categoryStyle1)));
                category_styles.add(new Drawer_Items(R.drawable.ic_categories, getString(R.string.categoryStyle2)));
                category_styles.add(new Drawer_Items(R.drawable.ic_categories, getString(R.string.categoryStyle3)));
                category_styles.add(new Drawer_Items(R.drawable.ic_categories, getString(R.string.categoryStyle4)));
                category_styles.add(new Drawer_Items(R.drawable.ic_categories, getString(R.string.categoryStyle5)));
                category_styles.add(new Drawer_Items(R.drawable.ic_categories, getString(R.string.categoryStyle6)));

                List<Drawer_Items> shop_childs = new ArrayList<>();
                shop_childs.add(new Drawer_Items(R.drawable.ic_sale, getString(R.string.Sale)));
                shop_childs.add(new Drawer_Items(R.drawable.ic_arrow_up, getString(R.string.Newest)));
                shop_childs.add(new Drawer_Items(R.drawable.ic_star_circle, getString(R.string.Featured)));


                // Add Child to selective Headers
                listDataChild.put(listDataHeader.get(0), home_styles);
                listDataChild.put(listDataHeader.get(1), category_styles);
                listDataChild.put(listDataHeader.get(2), shop_childs);
            }*/


            listDataHeader.add(new Drawer_Items(R.drawable.ic_home, getString(R.string.actionHome)));
            listDataHeader.add(new Drawer_Items(R.drawable.ic_categories, getString(R.string.actionCategories)));
            listDataHeader.add(new Drawer_Items(R.drawable.ic_cart, getString(R.string.actionShop)));

            if ("1".equalsIgnoreCase(appSettings.getOne_signal_notification()))
                listDataHeader.add(new Drawer_Items(R.drawable.ic_action_notifications, getString(R.string.push_notification)));
            if ("1".equalsIgnoreCase(appSettings.getEditProfilePage()))
                listDataHeader.add(new Drawer_Items(R.drawable.ic_account, getString(R.string.actionAccount)));
            if ("1".equalsIgnoreCase(appSettings.getMyOrdersPage()))
                listDataHeader.add(new Drawer_Items(R.drawable.ic_order, getString(R.string.actionOrders)));
            if ("1".equalsIgnoreCase(appSettings.getWp_point_reward()))
                listDataHeader.add(new Drawer_Items(R.drawable.ic_action_medal, getString(R.string.actionRewards)));
            if ("1".equalsIgnoreCase(appSettings.getWishListPage()))
                listDataHeader.add(new Drawer_Items(R.drawable.ic_favorite, getString(R.string.actionFavourites)));

            if ("1".equalsIgnoreCase(appSettings.getIntroPage()))
                listDataHeader.add(new Drawer_Items(R.drawable.ic_intro, getString(R.string.actionIntro)));
            if ("1".equalsIgnoreCase(appSettings.getBill_ship_info()))
                if (ConstantValues.IS_USER_LOGGED_IN) {
                    listDataHeader.add(new Drawer_Items(R.drawable.map_marker, getString(R.string.address_info)));
                }
            if ("1".equalsIgnoreCase(appSettings.getNewsPage()))
                listDataHeader.add(new Drawer_Items(R.drawable.ic_newspaper, getString(R.string.actionNews)));
            if ("1".equalsIgnoreCase(appSettings.getContactUsPage()))
                listDataHeader.add(new Drawer_Items(R.drawable.ic_chat_bubble, getString(R.string.actionContactUs)));
            if ("1".equalsIgnoreCase(appSettings.getAboutUsPage()))
                listDataHeader.add(new Drawer_Items(R.drawable.ic_info, getString(R.string.actionAbout)));
            if ("1".equalsIgnoreCase(appSettings.getShareApp()))
                listDataHeader.add(new Drawer_Items(R.drawable.ic_share, getString(R.string.actionShareApp)));
            if ("1".equalsIgnoreCase(appSettings.getRateApp()))
                listDataHeader.add(new Drawer_Items(R.drawable.ic_star_circle, getString(R.string.actionRateApp)));
            if ("1".equalsIgnoreCase(appSettings.getDownloads()))
                if (ConstantValues.IS_USER_LOGGED_IN) {
                    listDataHeader.add(new Drawer_Items(R.drawable.download, getString(R.string.download)));
                }
            if ("1".equalsIgnoreCase(appSettings.getSettingPage()))
                listDataHeader.add(new Drawer_Items(R.drawable.ic_settings, getString(R.string.actionSettings)));

            // Add last Header Item in Drawer Header List
            if (ConstantValues.IS_USER_LOGGED_IN) {
                listDataHeader.add(new Drawer_Items(R.drawable.ic_logout, getString(R.string.actionLogout)));
            } else {
                listDataHeader.add(new Drawer_Items(R.drawable.ic_logout, getString(R.string.actionLogin)));
            }


            if (!ConstantValues.IS_CLIENT_ACTIVE) {
                List<Drawer_Items> home_styles = new ArrayList<>();
                home_styles.add(new Drawer_Items(R.drawable.ic_home, getString(R.string.homeStyle1)));
                home_styles.add(new Drawer_Items(R.drawable.ic_home, getString(R.string.homeStyle2)));
                home_styles.add(new Drawer_Items(R.drawable.ic_home, getString(R.string.homeStyle3)));
                home_styles.add(new Drawer_Items(R.drawable.ic_home, getString(R.string.homeStyle4)));
                home_styles.add(new Drawer_Items(R.drawable.ic_home, getString(R.string.homeStyle5)));
                home_styles.add(new Drawer_Items(R.drawable.ic_home, getString(R.string.homeStyle6)));
                home_styles.add(new Drawer_Items(R.drawable.ic_home, getString(R.string.homeStyle7)));
                home_styles.add(new Drawer_Items(R.drawable.ic_home, getString(R.string.homeStyle8)));
                home_styles.add(new Drawer_Items(R.drawable.ic_home, getString(R.string.homeStyle9)));
                home_styles.add(new Drawer_Items(R.drawable.ic_home, getString(R.string.homeStyle10)));

                List<Drawer_Items> category_styles = new ArrayList<>();
                category_styles.add(new Drawer_Items(R.drawable.ic_categories, getString(R.string.categoryStyle1)));
                category_styles.add(new Drawer_Items(R.drawable.ic_categories, getString(R.string.categoryStyle2)));
                category_styles.add(new Drawer_Items(R.drawable.ic_categories, getString(R.string.categoryStyle3)));
                category_styles.add(new Drawer_Items(R.drawable.ic_categories, getString(R.string.categoryStyle4)));
                category_styles.add(new Drawer_Items(R.drawable.ic_categories, getString(R.string.categoryStyle5)));
                category_styles.add(new Drawer_Items(R.drawable.ic_categories, getString(R.string.categoryStyle6)));

                List<Drawer_Items> shop_childs = new ArrayList<>();
                shop_childs.add(new Drawer_Items(R.drawable.ic_sale, getString(R.string.Sale)));
                shop_childs.add(new Drawer_Items(R.drawable.ic_arrow_up, getString(R.string.Newest)));
                shop_childs.add(new Drawer_Items(R.drawable.ic_star_circle, getString(R.string.Featured)));


                // Add Child to selective Headers
//                listDataChild.put(listDataHeader.get(0), home_styles);
//                listDataChild.put(listDataHeader.get(1), category_styles);
                listDataChild.put(listDataHeader.get(2), shop_childs);
            }


        } else {

            listDataHeader.add(new Drawer_Items(R.drawable.ic_home, getString(R.string.actionHome)));
            listDataHeader.add(new Drawer_Items(R.drawable.ic_categories, getString(R.string.actionCategories)));
            listDataHeader.add(new Drawer_Items(R.drawable.ic_cart, getString(R.string.actionShop)));
            listDataHeader.add(new Drawer_Items(R.drawable.ic_account, getString(R.string.actionAccount)));
            listDataHeader.add(new Drawer_Items(R.drawable.ic_order, getString(R.string.actionOrders)));
            listDataHeader.add(new Drawer_Items(R.drawable.ic_favorite, getString(R.string.actionFavourites)));
            if (ConstantValues.IS_USER_LOGGED_IN) {
                listDataHeader.add(new Drawer_Items(R.drawable.ic_action_medal, getString(R.string.actionRewards)));
            }
            listDataHeader.add(new Drawer_Items(R.drawable.ic_intro, getString(R.string.actionIntro)));
            if (ConstantValues.IS_USER_LOGGED_IN) {
                listDataHeader.add(new Drawer_Items(R.drawable.map_marker, getString(R.string.address_info)));
            }
            listDataHeader.add(new Drawer_Items(R.drawable.ic_newspaper, getString(R.string.actionNews)));
            listDataHeader.add(new Drawer_Items(R.drawable.ic_info, getString(R.string.actionAbout)));
            listDataHeader.add(new Drawer_Items(R.drawable.ic_chat_bubble, getString(R.string.actionContactUs)));
            listDataHeader.add(new Drawer_Items(R.drawable.ic_share, getString(R.string.actionShareApp)));
            listDataHeader.add(new Drawer_Items(R.drawable.ic_star_circle, getString(R.string.actionRateApp)));
            if (ConstantValues.IS_USER_LOGGED_IN) {
                listDataHeader.add(new Drawer_Items(R.drawable.download, getString(R.string.download)));
            }
            listDataHeader.add(new Drawer_Items(R.drawable.ic_settings, getString(R.string.actionSettings)));
            // Add last Header Item in Drawer Header List
            if (ConstantValues.IS_USER_LOGGED_IN) {
                listDataHeader.add(new Drawer_Items(R.drawable.ic_logout, getString(R.string.actionLogout)));
            } else {
                listDataHeader.add(new Drawer_Items(R.drawable.ic_logout, getString(R.string.actionLogin)));
            }


            if (!ConstantValues.IS_CLIENT_ACTIVE) {
                List<Drawer_Items> home_styles = new ArrayList<>();
                home_styles.add(new Drawer_Items(R.drawable.ic_home, getString(R.string.homeStyle1)));
                home_styles.add(new Drawer_Items(R.drawable.ic_home, getString(R.string.homeStyle2)));
                home_styles.add(new Drawer_Items(R.drawable.ic_home, getString(R.string.homeStyle3)));
                home_styles.add(new Drawer_Items(R.drawable.ic_home, getString(R.string.homeStyle4)));
                home_styles.add(new Drawer_Items(R.drawable.ic_home, getString(R.string.homeStyle5)));
                home_styles.add(new Drawer_Items(R.drawable.ic_home, getString(R.string.homeStyle6)));
                home_styles.add(new Drawer_Items(R.drawable.ic_home, getString(R.string.homeStyle7)));
                home_styles.add(new Drawer_Items(R.drawable.ic_home, getString(R.string.homeStyle8)));
                home_styles.add(new Drawer_Items(R.drawable.ic_home, getString(R.string.homeStyle9)));
                home_styles.add(new Drawer_Items(R.drawable.ic_home, getString(R.string.homeStyle10)));

                List<Drawer_Items> category_styles = new ArrayList<>();
                category_styles.add(new Drawer_Items(R.drawable.ic_categories, getString(R.string.categoryStyle1)));
                category_styles.add(new Drawer_Items(R.drawable.ic_categories, getString(R.string.categoryStyle2)));
                category_styles.add(new Drawer_Items(R.drawable.ic_categories, getString(R.string.categoryStyle3)));
                category_styles.add(new Drawer_Items(R.drawable.ic_categories, getString(R.string.categoryStyle4)));
                category_styles.add(new Drawer_Items(R.drawable.ic_categories, getString(R.string.categoryStyle5)));
                category_styles.add(new Drawer_Items(R.drawable.ic_categories, getString(R.string.categoryStyle6)));

                List<Drawer_Items> shop_childs = new ArrayList<>();
                shop_childs.add(new Drawer_Items(R.drawable.ic_sale, getString(R.string.Sale)));
                shop_childs.add(new Drawer_Items(R.drawable.ic_arrow_up, getString(R.string.Newest)));
                shop_childs.add(new Drawer_Items(R.drawable.ic_star_circle, getString(R.string.Featured)));


                // Add Child to selective Headers
//                listDataChild.put(listDataHeader.get(0), home_styles);
//                listDataChild.put(listDataHeader.get(1), category_styles);
                listDataChild.put(listDataHeader.get(2), shop_childs);
            }


        }


        // Initialize DrawerExpandableListAdapter
        drawerExpandableAdapter = new DrawerExpandableListAdapter(this, listDataHeader, listDataChild);

        // Bind ExpandableListView and set DrawerExpandableListAdapter to the ExpandableListView
        main_drawer_list = (ExpandableListView) findViewById(R.id.main_drawer_list);
        main_drawer_list.setAdapter(drawerExpandableAdapter);

        drawerExpandableAdapter.notifyDataSetChanged();


        // Handle Group Item Click Listener
        main_drawer_list.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {

                if (drawerExpandableAdapter.getChildrenCount(groupPosition) < 1) {
                    // Navigate to Selected Main Item
                    if (groupPosition == 0) {
                        drawerSelectedItemNavigation(ConstantValues.DEFAULT_HOME_STYLE);
                    } else if (groupPosition == 1) {
                        drawerSelectedItemNavigation(ConstantValues.DEFAULT_CATEGORY_STYLE);
                    } else {
                        drawerSelectedItemNavigation(listDataHeader.get(groupPosition).getTitle());
                    }
                }
                return false;
            }
        });


        // Handle Child Item Click Listener
        main_drawer_list.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                // Navigate to Selected Child Item
                drawerSelectedItemNavigation(listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition).getTitle());
                return false;
            }
        });


        // Handle Group Expand Listener
        main_drawer_list.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
            }
        });
        // Handle Group Collapse Listener
        main_drawer_list.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
            }
        });

    }
    
    
   /* private void defaultIconPNG(){
        navigationDrawer.setItemIconTintList(null);
        listDataHeader.add(new Drawer_Items(R.drawable.home, getString(R.string.actionHome)));
        listDataHeader.add(new Drawer_Items(R.drawable.category, getString(R.string.actionCategories)));
        listDataHeader.add(new Drawer_Items(R.drawable.shop, getString(R.string.actionShop)));
    
        if ("1".equalsIgnoreCase(appSettings.getEditProfilePage()))
            listDataHeader.add(new Drawer_Items(R.drawable.locked, getString(R.string.actionAccount)));
        if ("1".equalsIgnoreCase(appSettings.getMyOrdersPage()))
            listDataHeader.add(new Drawer_Items(R.drawable.orders, getString(R.string.actionOrders)));
        if ("1".equalsIgnoreCase(appSettings.getWishListPage()))
            listDataHeader.add(new Drawer_Items(R.drawable.wishlist, getString(R.string.actionFavourites)));
        if ("1".equalsIgnoreCase(appSettings.getIntroPage()))
            listDataHeader.add(new Drawer_Items(R.drawable.intro, getString(R.string.actionIntro)));
        if ("1".equalsIgnoreCase(appSettings.getNewsPage()))
            listDataHeader.add(new Drawer_Items(R.drawable.news, getString(R.string.actionNews)));
        if ("1".equalsIgnoreCase(appSettings.getContactUsPage()))
            listDataHeader.add(new Drawer_Items(R.drawable.about, getString(R.string.actionContactUs)));
        if ("1".equalsIgnoreCase(appSettings.getAboutUsPage()))
            listDataHeader.add(new Drawer_Items(R.drawable.phone, getString(R.string.actionAbout)));
        if ("1".equalsIgnoreCase(appSettings.getShareApp()))
            listDataHeader.add(new Drawer_Items(R.drawable.share, getString(R.string.actionShareApp)));
        if ("1".equalsIgnoreCase(appSettings.getRateApp()))
            listDataHeader.add(new Drawer_Items(R.drawable.rating, getString(R.string.actionRateApp)));
        if ("1".equalsIgnoreCase(appSettings.getSettingPage()))
            listDataHeader.add(new Drawer_Items(R.drawable.setting, getString(R.string.actionSettings)));
    
        // Add last Header Item in Drawer Header List
        if (ConstantValues.IS_USER_LOGGED_IN) {
            listDataHeader.add(new Drawer_Items(R.drawable.ic_logout, getString(R.string.actionLogout)));
        } else {
            listDataHeader.add(new Drawer_Items(R.drawable.ic_logout, getString(R.string.actionLogin)));
        }
    
    
        if (!ConstantValues.IS_CLIENT_ACTIVE) {
            List<Drawer_Items> home_styles = new ArrayList<>();
            home_styles.add(new Drawer_Items(R.drawable.home, getString(R.string.homeStyle1)));
            home_styles.add(new Drawer_Items(R.drawable.home, getString(R.string.homeStyle2)));
            home_styles.add(new Drawer_Items(R.drawable.home, getString(R.string.homeStyle3)));
            home_styles.add(new Drawer_Items(R.drawable.home, getString(R.string.homeStyle4)));
            home_styles.add(new Drawer_Items(R.drawable.home, getString(R.string.homeStyle5)));
        
            List<Drawer_Items> category_styles = new ArrayList<>();
            category_styles.add(new Drawer_Items(R.drawable.category, getString(R.string.categoryStyle1)));
            category_styles.add(new Drawer_Items(R.drawable.category, getString(R.string.categoryStyle2)));
            category_styles.add(new Drawer_Items(R.drawable.category, getString(R.string.categoryStyle3)));
            category_styles.add(new Drawer_Items(R.drawable.category, getString(R.string.categoryStyle4)));
            category_styles.add(new Drawer_Items(R.drawable.category, getString(R.string.categoryStyle5)));
            category_styles.add(new Drawer_Items(R.drawable.category, getString(R.string.categoryStyle6)));
        
            List<Drawer_Items> shop_childs = new ArrayList<>();
            shop_childs.add(new Drawer_Items(R.drawable.ic_sale, getString(R.string.Sale)));
            shop_childs.add(new Drawer_Items(R.drawable.ic_arrow_up, getString(R.string.Newest)));
            shop_childs.add(new Drawer_Items(R.drawable.ic_star_circle, getString(R.string.Featured)));
        
        
            // Add Child to selective Headers
            listDataChild.put(listDataHeader.get(0), home_styles);
            listDataChild.put(listDataHeader.get(1), category_styles);
            listDataChild.put(listDataHeader.get(2), shop_childs);
        }
    
    }*/


    //*********** Navigate to given Selected Item of NavigationDrawer ********//

    private void drawerSelectedItemNavigation(String selectedItem) {

        Fragment fragment;
        FragmentManager fragmentManager = getSupportFragmentManager();
        
       /* if(product_ID!=null && !product_ID.isEmpty()){
    
            Bundle bundle = new Bundle();
            bundle.putInt("itemID", Integer.parseInt(product_ID));
            // Navigate to any selected HomePage Fragment
            fragment = new Product_Description();
            fragment.setArguments(bundle);
            fragmentManager.beginTransaction()
                    .replace(R.id.main_fragment, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();
    
            drawerLayout.closeDrawers();
        }*/

        if (selectedItem.equalsIgnoreCase(getString(R.string.actionHome))) {
            mSelectedItem = selectedItem;

            // Navigate to any selected HomePage Fragment
            Bundle bundle = new Bundle();
            bundle.putBoolean("on_sale", true);
            bundle.putBoolean("isMenuItem", true);

            // Navigate to Products Fragment
            fragment = new Products();
            fragment.setArguments(bundle);
            fragmentManager.beginTransaction()
                    .replace(R.id.main_fragment, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();
//            fragment = new HomePage_1();
////            fragment = new Products();
//            fragmentManager.beginTransaction()
//                    .replace(R.id.main_fragment, fragment)
//                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
//                    .commit();

            drawerLayout.closeDrawers();

        } else if (selectedItem.equalsIgnoreCase(getString(R.string.homeStyle1))) {
            mSelectedItem = selectedItem;

            // Navigate to HomePage1 Fragment
            fragment = new HomePage_1();
            fragmentManager.beginTransaction()
                    .replace(R.id.main_fragment, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();

            drawerLayout.closeDrawers();

        } else if (selectedItem.equalsIgnoreCase(getString(R.string.homeStyle2))) {
            mSelectedItem = selectedItem;

            // Navigate to HomePage2 Fragment
            fragment = new HomePage_2();
            fragmentManager.beginTransaction()
                    .replace(R.id.main_fragment, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();

            drawerLayout.closeDrawers();

        } else if (selectedItem.equalsIgnoreCase(getString(R.string.homeStyle3))) {
            mSelectedItem = selectedItem;

            // Navigate to HomePage3 Fragment
            fragment = new HomePage_3();
            fragmentManager.beginTransaction()
                    .replace(R.id.main_fragment, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();

            drawerLayout.closeDrawers();

        } else if (selectedItem.equalsIgnoreCase(getString(R.string.homeStyle4))) {
            mSelectedItem = selectedItem;

            // Navigate to HomePage4 Fragment
            fragment = new HomePage_4();
            fragmentManager.beginTransaction()
                    .replace(R.id.main_fragment, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();

            drawerLayout.closeDrawers();

        } else if (selectedItem.equalsIgnoreCase(getString(R.string.homeStyle5))) {
            mSelectedItem = selectedItem;

            // Navigate to HomePage5 Fragment
            fragment = new HomePage_5();
            fragmentManager.beginTransaction()
                    .replace(R.id.main_fragment, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();

            drawerLayout.closeDrawers();

        } else if (selectedItem.equalsIgnoreCase(getString(R.string.homeStyle6))) {
            mSelectedItem = selectedItem;

            // Navigate to HomePage6 Fragment
            fragment = new HomePage_6();
            fragmentManager.beginTransaction()
                    .replace(R.id.main_fragment, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();

            drawerLayout.closeDrawers();

        } else if (selectedItem.equalsIgnoreCase(getString(R.string.homeStyle7))) {
            mSelectedItem = selectedItem;

            // Navigate to HomePage6 Fragment
            fragment = new HomePage_7();
            fragmentManager.beginTransaction()
                    .replace(R.id.main_fragment, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();

            drawerLayout.closeDrawers();

        } else if (selectedItem.equalsIgnoreCase(getString(R.string.homeStyle8))) {
            mSelectedItem = selectedItem;

            // Navigate to HomePage6 Fragment
            fragment = new HomePage_8();
            fragmentManager.beginTransaction()
                    .replace(R.id.main_fragment, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();

            drawerLayout.closeDrawers();

        } else if (selectedItem.equalsIgnoreCase(getString(R.string.homeStyle9))) {
            mSelectedItem = selectedItem;

            // Navigate to HomePage9 Fragment
            fragment = new HomePage_9();
            fragmentManager.beginTransaction()
                    .replace(R.id.main_fragment, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();

            drawerLayout.closeDrawers();

        } else if (selectedItem.equalsIgnoreCase(getString(R.string.homeStyle10))) {
            mSelectedItem = selectedItem;

            // Navigate to HomePage9 Fragment
            fragment = new HomePage_10();
            fragmentManager.beginTransaction()
                    .replace(R.id.main_fragment, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();

            drawerLayout.closeDrawers();

        } else if (selectedItem.equalsIgnoreCase(getString(R.string.actionCategories))) {
            mSelectedItem = selectedItem;

            Bundle bundle = new Bundle();
            bundle.putBoolean("isHeaderVisible", false);

            // Navigate to any selected CategoryPage Fragment
            fragment = new Categories_1();
            fragment.setArguments(bundle);
            fragmentManager.beginTransaction()
                    .replace(R.id.main_fragment, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .addToBackStack(getString(R.string.actionHome)).commit();

            drawerLayout.closeDrawers();

        } else if (selectedItem.equalsIgnoreCase(getString(R.string.categoryStyle1))) {
            mSelectedItem = selectedItem;

            Bundle bundle = new Bundle();
            bundle.putBoolean("isHeaderVisible", false);

            // Navigate to Categories_1 Fragment
            fragment = new Categories_1();
            fragment.setArguments(bundle);
            fragmentManager.beginTransaction()
                    .replace(R.id.main_fragment, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .addToBackStack(getString(R.string.actionHome)).commit();

            drawerLayout.closeDrawers();

        } else if (selectedItem.equalsIgnoreCase(getString(R.string.categoryStyle2))) {
            mSelectedItem = selectedItem;

            Bundle bundle = new Bundle();
            bundle.putBoolean("isHeaderVisible", false);

            // Navigate to Categories_2 Fragment
            fragment = new Categories_2();
            fragment.setArguments(bundle);
            fragmentManager.beginTransaction()
                    .replace(R.id.main_fragment, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .addToBackStack(getString(R.string.actionHome)).commit();

            drawerLayout.closeDrawers();

        } else if (selectedItem.equalsIgnoreCase(getString(R.string.categoryStyle3))) {
            mSelectedItem = selectedItem;

            Bundle bundle = new Bundle();
            bundle.putBoolean("isHeaderVisible", false);

            // Navigate to Categories_3 Fragment
            fragment = new Categories_3();
            fragment.setArguments(bundle);
            fragmentManager.beginTransaction()
                    .replace(R.id.main_fragment, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .addToBackStack(getString(R.string.actionHome)).commit();

            drawerLayout.closeDrawers();

        } else if (selectedItem.equalsIgnoreCase(getString(R.string.categoryStyle4))) {
            mSelectedItem = selectedItem;

            Bundle bundle = new Bundle();
            bundle.putBoolean("isHeaderVisible", false);

            // Navigate to Categories_4 Fragment
            fragment = new Categories_4();
            fragment.setArguments(bundle);
            fragmentManager.beginTransaction()
                    .replace(R.id.main_fragment, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .addToBackStack(getString(R.string.actionHome)).commit();

            drawerLayout.closeDrawers();

        } else if (selectedItem.equalsIgnoreCase(getString(R.string.categoryStyle5))) {
            mSelectedItem = selectedItem;

            Bundle bundle = new Bundle();
            bundle.putBoolean("isHeaderVisible", false);

            // Navigate to Categories_5 Fragment
            fragment = new Categories_5();
            fragment.setArguments(bundle);
            fragmentManager.beginTransaction()
                    .replace(R.id.main_fragment, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .addToBackStack(getString(R.string.actionHome)).commit();

            drawerLayout.closeDrawers();

        } else if (selectedItem.equalsIgnoreCase(getString(R.string.categoryStyle6))) {
            mSelectedItem = selectedItem;

            Bundle bundle = new Bundle();
            bundle.putBoolean("isHeaderVisible", false);

            // Navigate to Categories_6 Fragment
            fragment = new Categories_6();
            fragment.setArguments(bundle);
            fragmentManager.beginTransaction()
                    .replace(R.id.main_fragment, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .addToBackStack(getString(R.string.actionHome)).commit();

            drawerLayout.closeDrawers();

        } else if (selectedItem.equalsIgnoreCase(getString(R.string.actionShop))) {
            mSelectedItem = selectedItem;

            Bundle bundle = new Bundle();
            bundle.putString("sortBy", "date");
            bundle.putBoolean("isMenuItem", true);

            // Navigate to Products Fragment
            fragment = new Products();
            fragment.setArguments(bundle);
            fragmentManager.beginTransaction()
                    .replace(R.id.main_fragment, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .addToBackStack(getString(R.string.actionHome)).commit();

            drawerLayout.closeDrawers();

        } else if (selectedItem.equalsIgnoreCase(getString(R.string.Sale))) {
            mSelectedItem = selectedItem;

            Bundle bundle = new Bundle();
            bundle.putBoolean("on_sale", true);
            bundle.putBoolean("isMenuItem", true);

            // Navigate to Products Fragment
            fragment = new Products();
            fragment.setArguments(bundle);
            fragmentManager.beginTransaction()
                    .replace(R.id.main_fragment, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .addToBackStack(getString(R.string.actionHome)).commit();

            drawerLayout.closeDrawers();

        } else if (selectedItem.equalsIgnoreCase(getString(R.string.push_notification))) {
            mSelectedItem = selectedItem;

            Bundle bundle = new Bundle();
            bundle.putBoolean("isMenuItem", true);

            // Navigate to Products Fragment
            fragment = new NotificationFrag();
            fragment.setArguments(bundle);
            fragmentManager.beginTransaction()
                    .replace(R.id.main_fragment, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .addToBackStack(getString(R.string.actionHome)).commit();

            drawerLayout.closeDrawers();

        } else if (selectedItem.equalsIgnoreCase(getString(R.string.Newest))) {
            mSelectedItem = selectedItem;

            Bundle bundle = new Bundle();
            bundle.putBoolean("isMenuItem", true);

            // Navigate to Products Fragment
            fragment = new Products();
            fragment.setArguments(bundle);
            fragmentManager.beginTransaction()
                    .replace(R.id.main_fragment, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .addToBackStack(getString(R.string.actionHome)).commit();

            drawerLayout.closeDrawers();

        } else if (selectedItem.equalsIgnoreCase(getString(R.string.actionRewards))) {
            mSelectedItem = selectedItem;

            Bundle bundle = new Bundle();
            bundle.putBoolean("isMenuItem", true);

            // Navigate to Products Fragment
            fragment = new Points_Fragment();
            fragment.setArguments(bundle);
            fragmentManager.beginTransaction()
                    .replace(R.id.main_fragment, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .addToBackStack(getString(R.string.actionHome)).commit();

            drawerLayout.closeDrawers();

        } else if (selectedItem.equalsIgnoreCase(getString(R.string.Featured))) {
            mSelectedItem = selectedItem;

            Bundle bundle = new Bundle();
            bundle.putBoolean("featured", true);
            bundle.putBoolean("isMenuItem", true);

            // Navigate to Products Fragment
            fragment = new Products();
            fragment.setArguments(bundle);
            fragmentManager.beginTransaction()
                    .replace(R.id.main_fragment, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .addToBackStack(getString(R.string.actionHome)).commit();

            drawerLayout.closeDrawers();

        } else if (selectedItem.equalsIgnoreCase(getString(R.string.actionAccount))) {
            if (ConstantValues.IS_USER_LOGGED_IN) {
                mSelectedItem = selectedItem;

                // Navigate to Update_Account Fragment
                fragment = new Update_Account();
                fragmentManager.beginTransaction()
                        .replace(R.id.main_fragment, fragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .addToBackStack(getString(R.string.actionHome)).commit();

                drawerLayout.closeDrawers();

            } else {
                // Navigate to Login Activity
                startActivity(new Intent(MainActivity.this, Login.class));
                finish();
                overridePendingTransition(R.anim.enter_from_left, R.anim.exit_out_left);
            }

        } else if (selectedItem.equalsIgnoreCase(getString(R.string.actionOrders))) {
            if (ConstantValues.IS_USER_LOGGED_IN) {
                mSelectedItem = selectedItem;

                // Navigate to My_Orders Fragment
                fragment = new My_Orders();
                fragmentManager.beginTransaction()
                        .replace(R.id.main_fragment, fragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .addToBackStack(getString(R.string.actionHome)).commit();

                drawerLayout.closeDrawers();

            } else {
                // Navigate to Login Activity
                startActivity(new Intent(MainActivity.this, Login.class));
                finish();
                overridePendingTransition(R.anim.enter_from_left, R.anim.exit_out_left);
            }

        } else if (selectedItem.equalsIgnoreCase(getString(R.string.actionFavourites))) {
            mSelectedItem = selectedItem;

            // Navigate to WishList Fragment
            fragment = new WishList();
            fragmentManager.beginTransaction()
                    .replace(R.id.main_fragment, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .addToBackStack(getString(R.string.actionHome)).commit();

            drawerLayout.closeDrawers();

        } else if (selectedItem.equalsIgnoreCase(getString(R.string.actionNews))) {
            mSelectedItem = selectedItem;

            // Navigate to News Fragment
            fragment = new News();
            fragmentManager.beginTransaction()
                    .replace(R.id.main_fragment, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .addToBackStack(getString(R.string.actionHome)).commit();

            drawerLayout.closeDrawers();

        } else if (selectedItem.equalsIgnoreCase(getString(R.string.address_info))) {
            mSelectedItem = selectedItem;

            Bundle bundle = new Bundle();
            bundle.putString("shipping", "1");
            // Navigate to News Fragment
            fragment = new Shipping_Address();
            fragment.setArguments(bundle);
            fragmentManager.beginTransaction()
                    .replace(R.id.main_fragment, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .addToBackStack(getString(R.string.actionHome)).commit();

            drawerLayout.closeDrawers();

        } else if (selectedItem.equalsIgnoreCase(getString(R.string.download))) {
            mSelectedItem = selectedItem;

            fragment = new Download();
            fragmentManager.beginTransaction()
                    .replace(R.id.main_fragment, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .addToBackStack(getString(R.string.actionHome)).commit();

            drawerLayout.closeDrawers();

        } else if (selectedItem.equalsIgnoreCase(getString(R.string.actionIntro))) {
            mSelectedItem = selectedItem;

            // Navigate to IntroScreen
            startActivity(new Intent(getBaseContext(), IntroScreen.class));

        } else if (selectedItem.equalsIgnoreCase(getString(R.string.actionAbout))) {
            mSelectedItem = selectedItem;

            // Navigate to About Fragment
            fragment = new About();
            fragmentManager.beginTransaction()
                    .replace(R.id.main_fragment, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .addToBackStack(getString(R.string.actionHome)).commit();

            drawerLayout.closeDrawers();

        } else if (selectedItem.equalsIgnoreCase(getString(R.string.actionShareApp))) {
            mSelectedItem = selectedItem;

            // Share App with the help of static method of Utilities class
            Utilities.shareMyApp(MainActivity.this);

            drawerLayout.closeDrawers();

        } else if (selectedItem.equalsIgnoreCase(getString(R.string.actionRateApp))) {
            mSelectedItem = selectedItem;

            // Rate App with the help of static method of Utilities class
            Utilities.rateMyApp(MainActivity.this);

            drawerLayout.closeDrawers();

        } else if (selectedItem.equalsIgnoreCase(getString(R.string.actionContactUs))) {
            mSelectedItem = selectedItem;

            // Navigate to ContactUs Fragment
            fragment = new ContactUs();
            fragmentManager.beginTransaction()
                    .replace(R.id.main_fragment, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .addToBackStack(getString(R.string.actionHome)).commit();

            drawerLayout.closeDrawers();

        } else if (selectedItem.equalsIgnoreCase(getString(R.string.actionSettings))) {
            mSelectedItem = selectedItem;

            // Navigate to SettingsFragment Fragment
            fragment = new SettingsFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.main_fragment, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .addToBackStack(getString(R.string.actionHome)).commit();

            drawerLayout.closeDrawers();

        } else if (selectedItem.equalsIgnoreCase(getString(R.string.actionLogin))) {
            mSelectedItem = selectedItem;

            // Navigate to Login Activity
            startActivity(new Intent(MainActivity.this, Login.class));
            finish();
            overridePendingTransition(R.anim.enter_from_right, R.anim.exit_out_right);

        } else if (selectedItem.equalsIgnoreCase(getString(R.string.actionLogout))) {
            mSelectedItem = selectedItem;

            // Edit UserID in SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("userID", "");
            editor.putString("userCookie", "");
            editor.putString("userPicture", "");
            editor.putString("userName", "");
            editor.putString("userDisplayName", "");
            editor.apply();

            // Set UserLoggedIn in MyAppPrefsManager
            MyAppPrefsManager myAppPrefsManager = new MyAppPrefsManager(this);
            myAppPrefsManager.setUserLoggedIn(false);

            // Set isLogged_in of ConstantValues
            ConstantValues.IS_USER_LOGGED_IN = myAppPrefsManager.isUserLoggedIn();


            // Navigate to Login Activity
            startActivity(new Intent(MainActivity.this, Login.class));
            finish();
            overridePendingTransition(R.anim.enter_from_right, R.anim.exit_out_right);
        }
    }


    //*********** Called by the System when the Device's Configuration changes while Activity is Running ********//

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Configure ActionBarDrawerToggle with new Configuration
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }


    //*********** Invoked to Save the Instance's State when the Activity may be Temporarily Destroyed ********//

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save the Selected NavigationDrawer Item
        outState.putString(SELECTED_ITEM_ID, mSelectedItem);
    }

    //*********** Set the Base Context for the ContextWrapper ********//

    @Override
    protected void attachBaseContext(Context newBase) {

        String languageCode = ConstantValues.LANGUAGE_CODE;
        if ("".equalsIgnoreCase(languageCode) || ConstantValues.LANGUAGE_CODE == null)
            languageCode = ConstantValues.LANGUAGE_CODE = "en";

        super.attachBaseContext(LocaleHelper.wrapLocale(newBase, languageCode));
    }


    //*********** Receives the result from a previous call of startActivityForResult(Intent, int) ********//

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    //*********** Creates the Activity's OptionsMenu ********//

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate toolbar_menu Menu
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);

        // Bind Menu Items
        MenuItem languageItem = menu.findItem(R.id.toolbar_ic_language);
        MenuItem currencyItem = menu.findItem(R.id.toolbar_ic_currency);
        MenuItem searchItem = menu.findItem(R.id.toolbar_ic_search);
        MenuItem cartItem = menu.findItem(R.id.toolbar_ic_cart);
        MenuItem shareItem = menu.findItem(R.id.toolbar_ic_share);

        languageItem.setVisible(false);
        currencyItem.setVisible(false);
        shareItem.setVisible(false);


        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ImageView image = (ImageView) inflater.inflate(R.layout.layout_animated_ic_cart, null);

        Drawable itemIcon = cartItem.getIcon().getCurrent();
        image.setImageDrawable(itemIcon);

        cartItem.setActionView(image);


        cartItem.getActionView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to My_Cart Fragment
                Fragment fragment = new My_Cart();
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.main_fragment, fragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .addToBackStack(getString(R.string.actionHome)).commit();
            }
        });


        // Tint Menu Icons with the help of static method of Utilities class
        Utilities.tintMenuIcon(MainActivity.this, searchItem, R.color.white);
        Utilities.tintMenuIcon(MainActivity.this, cartItem, R.color.white);

        return true;
    }


    //*********** Prepares the OptionsMenu of Toolbar ********//

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        // Clear OptionsMenu if NavigationDrawer is Opened
        if (drawerLayout.isDrawerOpen(navigationDrawer)) {

            MenuItem languageItem = menu.findItem(R.id.toolbar_ic_language);
            MenuItem currencyItem = menu.findItem(R.id.toolbar_ic_currency);
            MenuItem searchItem = menu.findItem(R.id.toolbar_ic_search);
            MenuItem cartItem = menu.findItem(R.id.toolbar_ic_cart);

            if (appSettings != null && appSettings.getWpml_enabled() != null) {
                if ("1".equalsIgnoreCase(appSettings.getWpml_enabled())) {
                    languageItem.setVisible(true);
                }
            } else {
                languageItem.setVisible(false);
            }

            if (appSettings != null && appSettings.getWp_multi_currency() != null) {
                if ("1".equalsIgnoreCase(appSettings.getWp_multi_currency())) {
                    currencyItem.setVisible(true);
                }
            } else {
                currencyItem.setVisible(false);
            }
            searchItem.setVisible(false);
            cartItem.setVisible(false);

        } else {
            MenuItem cartItem = menu.findItem(R.id.toolbar_ic_cart);

            // Get No. of Cart Items with the static method of My_Cart Fragment
            int cartSize = My_Cart.getCartSize();


            // if Cart has some Items
            if (cartSize > 0) {

                // Animation for cart_menuItem
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_icon);
                animation.setRepeatMode(Animation.REVERSE);
                animation.setRepeatCount(1);

                cartItem.getActionView().startAnimation(animation);
                cartItem.getActionView().setAnimation(null);


                LayerDrawable icon = null;
                Drawable drawable = cartItem.getIcon();

                if (drawable instanceof DrawableWrapper) {
                    drawable = ((DrawableWrapper) drawable).getWrappedDrawable();
                } else if (drawable instanceof WrappedDrawable) {
                    drawable = ((WrappedDrawable) drawable).getWrappedDrawable();
                }


                if (drawable instanceof LayerDrawable) {
                    icon = (LayerDrawable) drawable;
                } else if (drawable instanceof DrawableWrapper) {
                    DrawableWrapper wrapper = (DrawableWrapper) drawable;
                    if (wrapper.getWrappedDrawable() instanceof LayerDrawable) {
                        icon = (LayerDrawable) wrapper.getWrappedDrawable();
                    }
                }

//                icon = (LayerDrawable) drawable;


                // Set BadgeCount on Cart_Icon with the static method of NotificationBadger class
                if (icon != null)
                    NotificationBadger.setBadgeCount(this, icon, String.valueOf(cartSize));


            } else {
                // Set the Icon for Empty Cart
                cartItem.setIcon(R.drawable.ic_cart_empty);
            }

        }

        return super.onPrepareOptionsMenu(menu);
    }


    //*********** Called whenever an Item in OptionsMenu is Selected ********//

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Fragment fragment;
        FragmentManager fragmentManager = getSupportFragmentManager();


        switch (item.getItemId()) {

            case R.id.toolbar_ic_language:

                drawerLayout.closeDrawers();

                // Navigate to Languages Fragment
                fragment = new Languages();
                fragmentManager.beginTransaction()
                        .replace(R.id.main_fragment, fragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .addToBackStack(getString(R.string.actionHome)).commit();
                break;
            case R.id.toolbar_ic_currency:

                drawerLayout.closeDrawers();

                // Navigate to Languages Fragment
                fragment = new CurrencyFrag();
                fragmentManager.beginTransaction()
                        .replace(R.id.main_fragment, fragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .addToBackStack(getString(R.string.actionHome)).commit();
                break;

            case R.id.toolbar_ic_search:

                SearchFragment.FLAG_SEARCHED = false;
                // Navigate to SearchFragment Fragment
                fragment = new SearchFragment();
                //fragment.setArguments(bundle);
                fragmentManager.beginTransaction()
                        .replace(R.id.main_fragment, fragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .addToBackStack(getString(R.string.actionHome)).commit();
                break;

            case R.id.toolbar_ic_cart:

                // Navigate to My_Cart Fragment
                fragment = new My_Cart();
                fragmentManager.beginTransaction()
                        .replace(R.id.main_fragment, fragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .addToBackStack(getString(R.string.actionHome)).commit();
                break;

            default:
                break;
        }

        return true;
    }


    //*********** Called when the Activity has detected the User pressed the Back key ********//

    @Override
    public void onBackPressed() {

        // Get FragmentManager
        FragmentManager fm = getSupportFragmentManager();


        // Check if NavigationDrawer is Opened
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();

        }
        // Check if BackStack has some Fragments
        else if (fm.getBackStackEntryCount() > 0) {

            // Pop previous Fragment
            fm.popBackStack();

        }
        // Check if doubleBackToExitPressed is true
        else if (doublePressedBackToExit) {
            super.onBackPressed();

        } else {
            this.doublePressedBackToExit = true;
            Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();

            // Delay of 2 seconds
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    // Set doublePressedBackToExit false after 2 seconds
                    doublePressedBackToExit = false;
                }
            }, 2000);
        }
    }


    public class MyExtraTasks extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            // Call the method of StartAppRequests class to process App Startup Requests
            startAppRequests.StartRequestBannerAndCategories();

            return "true";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            drawerSelectedItemNavigation(mSelectedItem);
        }
    }

}

