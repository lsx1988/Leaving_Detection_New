package com.shixun.android.leaving_detection;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.avos.avoscloud.AVUser;
import com.special.ResideMenu.ResideMenu;
import com.special.ResideMenu.ResideMenuItem;

public class ManageActivity extends GeneralFragmentActivity implements GeneralFragment.btnClickListener, View.OnClickListener{

    private ResideMenu resideMenu;
    private ResideMenuItem itemHome;
    private ResideMenuItem itemProfile;
    private ResideMenuItem itemSettings;
    private ResideMenuItem itemLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.general_activity_fragment);
        setupMenu();
        if(AVUser.getCurrentUser() != null) {
            showLogoutMenuItem();
        }
    }

    @Override
    protected Fragment createFragment() {
        return new LoginFragment();
    }

    @Override
    public void onRegisterClick() {
        nevigateToFragment(RegisterFragment.class, true);
    }

    @Override
    public void onClick(View v) {

        if(v == itemLogout){
            AVUser.getCurrentUser().logOut();// 清除缓存用户对象
            nevigateToFragment(LoginFragment.class, false);
            v.setVisibility(View.GONE);
        }
        if(v == itemSettings) {
            if(AVUser.getCurrentUser() != null) {
                nevigateToFragment(SettingFragment.class, false);
            } else {
                Toast.makeText(this, "Please login First", Toast.LENGTH_SHORT).show();
            }
        }

        if(v == itemProfile) {
            if(AVUser.getCurrentUser() != null) {
                nevigateToFragment(ProfileFragment.class, false);
            } else {
                Toast.makeText(this, "Please login First", Toast.LENGTH_SHORT).show();
            }
        }

        openMenu(false);
    }

    @Override
    public void onUpNevigationClick() {
        if(getSupportFragmentManager().getBackStackEntryCount() > 0) {
            this.fragmentManager.popBackStack();
        }
    }

    @Override
    public void onMenuClick() {
        openMenu(true);
    }

    @Override
    public void onLoginSuccessful() {
        showLogoutMenuItem();
        nevigateToFragment(MainFragment.class, false);
    }

    @Override
    public void onRegisterSuccessful() {
        clearBackStack();
        showLogoutMenuItem();
        nevigateToFragment(SettingFragment.class, false);
    }

    @Override
    public void onSaveSettingSuccessful() {
        nevigateToFragment(MainFragment.class, false);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return resideMenu.dispatchTouchEvent(ev);
    }

    private void setupMenu() {
        // attach to current activity
        resideMenu = new ResideMenu(this);
        resideMenu.setBackground(R.drawable.menu_background);
        resideMenu.attachToActivity(this);

        // create menu items;
        itemHome     = new ResideMenuItem(this, R.drawable.icon_home,     "Home");
        itemProfile  = new ResideMenuItem(this, R.drawable.icon_profile,  "Profile");
        itemSettings = new ResideMenuItem(this, R.drawable.icon_settings, "Settings");

        itemHome.setOnClickListener(this);
        itemProfile.setOnClickListener(this);
        itemSettings.setOnClickListener(this);

        resideMenu.addMenuItem(itemHome, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemProfile, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemSettings, ResideMenu.DIRECTION_LEFT);

        resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);
    }

    private void showLogoutMenuItem() {
        itemLogout   = new ResideMenuItem(this, R.drawable.icon_logout,   "Logout");
        itemLogout.setOnClickListener(this);
        resideMenu.addMenuItem(itemLogout, ResideMenu.DIRECTION_LEFT);
    }

    private void nevigateToFragment(Class GeneralFragment, boolean isUpSet) {

        try{
            Object obj = GeneralFragment.newInstance();
            if(isUpSet) {
                this.fragmentManager
                        .beginTransaction()
                        .replace(R.id.fragment_container, (GeneralFragment)obj)
                        .addToBackStack(null)
                        .commit();
            } else {
                this.fragmentManager
                        .beginTransaction()
                        .replace(R.id.fragment_container, (GeneralFragment)obj)
                        .commit();
            }
        } catch (Exception e) {

        }
    }

    private void clearBackStack() {
        this.fragmentManager.popBackStack(null, this.fragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    private void openMenu(boolean isOpen) {
        if(isOpen) {
            resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
        } else {
            resideMenu.closeMenu();
        }
    }

    @Override
    protected void onDestroy() {
        AVUser.getCurrentUser().logOut();
        super.onDestroy();
    }
}
