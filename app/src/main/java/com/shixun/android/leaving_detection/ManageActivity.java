package com.shixun.android.leaving_detection;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.avos.avoscloud.AVUser;
import com.shixun.android.leaving_detection.Detection.MyService;
import com.special.ResideMenu.ResideMenu;
import com.special.ResideMenu.ResideMenuItem;

import static com.avos.avoscloud.AVUser.getCurrentUser;

public class ManageActivity extends GeneralFragmentActivity implements ActionListener, View.OnClickListener{

    private ResideMenu resideMenu;
    private ResideMenuItem itemHome;
    private ResideMenuItem itemProfile;
    private ResideMenuItem itemSettings;
    private ResideMenuItem itemLogout;
    private Intent mServiceIntent;
    private boolean isPressureOn;
    private boolean isMagneticOn;
    private boolean isWifiScanOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.general_activity_fragment);
        //初始化菜单项
        setupMenu();
        if(getCurrentUser() != null) {
            showLogoutMenuItem();
        }
        isPressureOn = false;
        isMagneticOn = false;
        isWifiScanOn = false;
    }

    /**
     * 程序开启后, 判断是否已经处于登录状态:已经登录,直接启动 mainFragnment, 否则启动loginFragment
     * @return
     */
    @Override
    protected Fragment createFragment() {
        if(getCurrentUser() != null) {
            return new MainFragment();
        } else {
            return new LoginFragment();
        }
    }

    /**
     * 点击 register 按钮,导向至注册界面, loginFragment 压入回退栈
     */
    @Override
    public void onRegisterClick() {
        nevigateToFragment(RegisterFragment.class, true, null);
    }

    /**
     * 响应菜单按钮点击
     * @param v
     */
    @Override
    public void onClick(View v) {

        //点击 logout
        if(v == itemLogout){
            getCurrentUser().logOut();// 清除缓存用户对象
            nevigateToFragment(LoginFragment.class, false, null);//返回至注册界面
            v.setVisibility(View.GONE);//隐藏 logout 菜单项
        }

        //点击 setting
        if(v == itemSettings) {
            //若用户已经登录,则导航至 setting 界面
            if(getCurrentUser() != null) {
                nevigateToFragment(SettingFragment.class, false, null);
            } else {
                //用户没有登录,提示登录
                Toast.makeText(this, "Please login First", Toast.LENGTH_SHORT).show();
            }
        }

        //点击 profile
        if(v == itemProfile) {
            //若用户已经登录,则导航至 setting 界面
            if(getCurrentUser() != null) {
                nevigateToFragment(ProfileFragment.class, false, null);
            } else {
                //用户没有登录,提示登录
                Toast.makeText(this, "Please login First", Toast.LENGTH_SHORT).show();
            }
        }

        openMenu(false); //关闭菜单项
    }

    /**
     * 响应 Up Action
     */
    @Override
    public void onUpNevigationClick() {
        //若回退栈中存在 fragment, 将其弹出
        if(getSupportFragmentManager().getBackStackEntryCount() > 0) {
            this.fragmentManager.popBackStack();
        }
    }

    /**
     * 点击 menu 按钮,显示菜单列表
     */
    @Override
    public void onMenuClick() {
        openMenu(true);
    }

    /**
     * 响应成功 login
     */
    @Override
    public void onLoginSuccessful() {
        showLogoutMenuItem();// 显示menu
        nevigateToFragment(MainFragment.class, false, null);// 导航至MaingFragment
    }

    /**
     * 响应成功 register, 说明为新用户,导航至 setting 界面进行首次设置
     */
    @Override
    public void onRegisterSuccessful() {
        clearBackStack();// loginFragment在回退栈中,将其清空
        showLogoutMenuItem();//显示 logout 菜单项
        openMenu(false);//关闭 menu
        showModelOptionDialog();//显示 model 选择对话框
    }

    @Override
    public void onChooseDefaultModel() {
        nevigateToFragment(MainFragment.class, false, null);
    }

    @Override
    public void onChooseCustomModel() {
        nevigateToFragment(SettingFragment.class, false, null);
    }

    /**
     * 响应成功保存 setting, 若没有 remodel,导航至 mainFragment
     */
    @Override
    public void onDetection() {
        nevigateToFragment(MainFragment.class, false, null);
    }

    /**
     * 响应成功保存 setting, 导航至 remodel fragment
     */
    @Override
    public void onRemodel() {
        nevigateToFragment(RemodelFragment.class, true, null);
    }

    /**
     * 激活滑动打开 menu
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return resideMenu.dispatchTouchEvent(ev);
    }

    /**
     * 响应start detection
     */
    @Override
    public void startDetection() {
        checkSensor();//检测用户设置的 sensor
        //开启 service
        mServiceIntent = new Intent(this, MyService.class);
        mServiceIntent.putExtra("usePressure", isPressureOn);
        this.startService(mServiceIntent);
    }

    /**
     * 响应stop detectin
     */
    @Override
    public void stopDetection() {
        stopService(mServiceIntent);// 关闭 service
    }


    /**
     * 初始化菜单选项
     */
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

    /**
     * 用户成功登陆后, 显示 logout 菜单选项
     */
    private void showLogoutMenuItem() {
        itemLogout   = new ResideMenuItem(this, R.drawable.icon_logout,   "Logout");
        itemLogout.setOnClickListener(this);
        resideMenu.addMenuItem(itemLogout, ResideMenu.DIRECTION_LEFT);
    }

    /**
     * 导向不同的 fragment
     * @param GeneralFragment 传入要导向的 fragment 类
     * @param isUpSet 是否设置up action
     */
    private void nevigateToFragment(Class GeneralFragment, boolean isUpSet, Bundle bundle) {

        try{
            Object obj = GeneralFragment.newInstance();

            GeneralFragment generalFragment = (GeneralFragment) obj;

            if(bundle != null) {
                generalFragment.setArguments(bundle);
            }

            if(isUpSet) {
                this.fragmentManager
                        .beginTransaction()
                        .replace(R.id.fragment_container, generalFragment)
                        .addToBackStack(null)
                        .commit();
            } else {
                this.fragmentManager
                        .beginTransaction()
                        .replace(R.id.fragment_container, generalFragment)
                        .commit();
            }
        } catch (Exception e) {

        }
    }

    /**
     * 清空回退栈
     */
    private void clearBackStack() {
        this.fragmentManager.popBackStack(null, this.fragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    /**
     * 设置 menu 开关
     * @param isOpen
     */
    private void openMenu(boolean isOpen) {
        if(isOpen) {
            resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
        } else {
            resideMenu.closeMenu();
        }
    }

    /**
     * 获取用户 sensor 选项情况
     */
    private void checkSensor() {
        if (getCurrentUser().has("pressure")) {
            isPressureOn = AVUser.getCurrentUser().getBoolean("pressure");
        }

        if (getCurrentUser().has("magnetic")) {
            isMagneticOn = getCurrentUser().getBoolean("magnetic");
        }

        if (getCurrentUser().has("wifi")) {
            isWifiScanOn = getCurrentUser().getBoolean("wifi");
        }
    }

    /**
     * 显示 model 选择对话框
     */
    public void showModelOptionDialog () {
        ModelOptionDialogFragment editNameDialog = new ModelOptionDialogFragment();
        editNameDialog.show(this.fragmentManager, "ModelOptionDialog");
    }

    /**
     * 关闭程序时,用户自动登出
     */
    @Override
    protected void onDestroy() {
        getCurrentUser().logOut();
        super.onDestroy();
    }
}
