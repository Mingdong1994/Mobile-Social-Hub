package com.example.mobilesocialhub;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.mobilesocialhub.eventcard.CreateEventFragment;
import com.example.mobilesocialhub.eventcard.EventFragment;
import com.example.mobilesocialhub.chat.ChatFragment;
import com.example.mobilesocialhub.databinding.ActivityTestBinding;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class TestActivity extends AppCompatActivity {
    boolean flag;
    EventFragment eventFragment;
    ChatFragment chatFragment;
    CreateEventFragment createFragment;
    Fragment nowFragment;
    String username;
    String TAG="Test Activity";

    private void initPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //检查权限
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //请求权限
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                flag = true;
            }
        } else {
            flag = true;
        }
    }

    private void checkIntent(){
        Log.d(TAG,"intent is coming1");
        if(getIntent().hasExtra("username")){
            Log.d(TAG,"found");
            username = getIntent().getStringExtra("username");
            Log.d(TAG,username);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initPermission();
        checkIntent();


        ActivityTestBinding testBinding = DataBindingUtil.setContentView(this, R.layout.activity_test);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        // Create the event fragment
        eventFragment = new EventFragment();
        eventFragment.setOnButtonClick(new EventFragment.OnButtonClick() {
            @Override
            public void onclick(View view) {
                navContent(nowFragment, createFragment);
            }
        });
        //setUsername event fragment username here
        eventFragment.setUsername(username);

        chatFragment = new ChatFragment();

        // Create the createactivity Fragment
        createFragment = new CreateEventFragment();
        createFragment.setOnButtonClick(new CreateEventFragment.OnButtonClick() {
            @Override
            public void onclick(View view) {
                navContent(nowFragment, eventFragment);
            }
        });
        //set the createactivity Fragment
        createFragment.setUsername(username);


        nowFragment = eventFragment;
        ChipNavigationBar bottomNavView = testBinding.chipNavigationBar;

        ft.replace(R.id.main_fragment, eventFragment).commit();
        ChipNavigationBar.OnItemSelectedListener itemListener = new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                switch (i) {
                    case R.id.home_info:
                        navContent(nowFragment, eventFragment);
                        break;
                    case R.id.person_info:
                        navContent(nowFragment, chatFragment);
                        break;
                    case R.id.activity_info:
                        navContent(nowFragment, createFragment);
                        break;
                    default:
                        break;
                }
            }
        };
        bottomNavView.setOnItemSelectedListener(itemListener);
        bottomNavView.setItemSelected(R.id.home_info, true);
    }
    private void navContent(Fragment from, Fragment to) {
        if(nowFragment != to) {
            nowFragment = to;
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            if (!to.isAdded()) {
                ft.hide(from).add(R.id.main_fragment, to).commit();
            } else {
                ft.hide(from).show(to).commit();
            }
        }
    }
}