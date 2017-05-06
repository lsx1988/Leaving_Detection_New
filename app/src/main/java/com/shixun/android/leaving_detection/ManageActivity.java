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

import java.io.File;

import static com.avos.avoscloud.AVUser.getCurrentUser;

public class ManageActivity extends GeneralFragmentActivity implements ActionListener, View.OnClickListener{

    private ResideMenu resideMenu;
    private ResideMenuItem itemHome;
    private ResideMenuItem itemProfile;
    private ResideMenuItem itemSettings;
    private ResideMenuItem itemLogout;
    private ResideMenuItem itemFolder;
    private Intent mServiceIntent;
    private boolean isPressureOn;
    private boolean isMagneticOn;
    private boolean isWifiScanOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.general_activity_fragment);
        setupMenu();//初始化菜单项
        //若用户已经登录,则强制登出
        if(getCurrentUser() != null) {
            getCurrentUser().logOut();
        }
        isPressureOn = false;
        isMagneticOn = false;
        isWifiScanOn = false;
    }

    /*
    程序运行,首先执行登录操作
     */
    @Override
    protected Fragment createFragment() {
        return new LoginFragment();
    }

    /*
    点击 register 按钮,导向至注册界面, loginFragment 压入回退栈
     */
    @Override
    public void onRegisterClick() {
        nevigateToFragment(RegisterFragment.class, true, null);
    }

    /*
    响应菜单按钮点击
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

        //点击 folder
        if(v == itemFolder) {
            nevigateToFragment(FolderFragment.class, false, null);
        }

        openMenu(false); //关闭菜单项
    }

    /*
    响应 Up Action
     */
    @Override
    public void onUpNevigationClick() {
        //若回退栈中存在 fragment, 将其弹出
        if(getSupportFragmentManager().getBackStackEntryCount() > 0) {
            this.fragmentManager.popBackStack();
        }
    }

    /*
    点击 menu 按钮,显示菜单列表
     */
    @Override
    public void onMenuClick() {
        openMenu(true);
    }

    /*
    响应成功 login
     */
    @Override
    public void onLoginSuccessful() {
        showLogoutMenuItem();// 显示menu
        nevigateToFragment(MainFragment.class, false, null);// 导航至MainFragment
    }

    /*
     响应成功 register, 说明为新用户,导航至 setting 界面进行首次设置
     */
    @Override
    public void onRegisterSuccessful() {
        clearBackStack();// loginFragment在回退栈中,将其清空
        showLogoutMenuItem();//显示 logout 菜单项
        openMenu(false);//关闭 menu
        showModelOptionDialog();//显示 model 选择对话框
    }

    /*
    响应点击文件列表,显示文件内容
     */
    @Override
    public void onShowFileText(File file) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("textFile",file);
        nevigateToFragment(ShowTextFragment.class, true, bundle);// 导航至ShowTextFragment
    }

    /*
    响应点击训练, 开始训练 scale 和 model
     */

    @Override
    public void onTrainData(File file) {

    }

    /*
            响应成功采集传感器数据
             */
    @Override
    public void onCollectSensorDataSuccessful() {
        showFileSavingDialog();
    }

    /*
        首次注册, 若选择默认模型,导航至 MainFragment
         */
    @Override
    public void onChooseDefaultModel() {
        nevigateToFragment(MainFragment.class, false, null);
    }

    /*
    首次注册,若选择自定义模型,导航至 SettingFragment
     */
    @Override
    public void onChooseCustomModel() {
        nevigateToFragment(SettingFragment.class, false, null);
    }

    /*
     响应成功保存 setting, 若没有 remodel,导航至 mainFragment
     */
    @Override
    public void onDetection() {
        nevigateToFragment(MainFragment.class, false, null);
    }

    /*
     响应成功保存 setting, 导航至 remodel fragment
     */
    @Override
    public void onRemodel() {
        nevigateToFragment(RemodelFragment.class, false, null);
    }

    /*
     激活滑动打开 menu
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return resideMenu.dispatchTouchEvent(ev);
    }

    /*
    开启 / 关闭 detection 服务
     */
    @Override
    public void startDetection(boolean isRemodel) {
        //联网检查用户历史设置
        checkSensor();
        if(mServiceIntent == null) {
            mServiceIntent = new Intent(this, MyService.class);
            mServiceIntent.putExtra("isPressureOn", isPressureOn);
            mServiceIntent.putExtra("isMagneticOn", isMagneticOn);
            mServiceIntent.putExtra("isWifiScanOn", isWifiScanOn);
            mServiceIntent.putExtra("isRemodel", isRemodel);
            this.startService(mServiceIntent);
        }
    }

    @Override
    public void stopDetection() {
        if (mServiceIntent != null) {
            stopService(mServiceIntent);
        }

        mServiceIntent = null;
    }

    /*
     初始化菜单选项
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
        itemFolder   = new ResideMenuItem(this, R.drawable.icon_folder,   "Folder");

        itemHome.setOnClickListener(this);
        itemProfile.setOnClickListener(this);
        itemSettings.setOnClickListener(this);
        itemFolder.setOnClickListener(this);

        resideMenu.addMenuItem(itemHome, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemProfile, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemSettings, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemFolder, ResideMenu.DIRECTION_LEFT);

        resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);
    }

    /*
     用户成功登陆后, 显示 logout 菜单选项
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

    /*
     清空回退栈
     */
    private void clearBackStack() {
        this.fragmentManager.popBackStack(null, this.fragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    /*
     * 设置 menu 开关
     */
    private void openMenu(boolean isOpen) {
        if(isOpen) {
            resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
        } else {
            resideMenu.closeMenu();
        }
    }

    /*
     获取用户 sensor 选项情况
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

    /*
     显示 model 选择对话框
     */
    public void showModelOptionDialog () {
        ModelOptionDialogFragment modelDialog = new ModelOptionDialogFragment();
        modelDialog.show(this.fragmentManager, "ModelOptionDialog");
    }

    /*
    显示文件保存对话框
     */

    public void showFileSavingDialog() {
        FileSavingDialogFragment fileDialog = new FileSavingDialogFragment();
        fileDialog.show(this.fragmentManager, "FileSavingDialog");

    }

    /*
     关闭程序时,用户自动登出
     */
    @Override
    protected void onDestroy() {
        getCurrentUser().logOut();
        stopService(mServiceIntent);
        super.onDestroy();
    }
}
