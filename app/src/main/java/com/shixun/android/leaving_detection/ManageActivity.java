package com.shixun.android.leaving_detection;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.MotionEvent;
import android.view.View;

import com.cocosw.bottomsheet.BottomSheet;
import com.shixun.android.leaving_detection.Activity.ActionListener;
import com.shixun.android.leaving_detection.Activity.GeneralFragmentActivity;
import com.shixun.android.leaving_detection.Detection.MyService;
import com.shixun.android.leaving_detection.Fragment.AmbientListFragment;
import com.shixun.android.leaving_detection.Fragment.ChooseSensorFragment;
import com.shixun.android.leaving_detection.Fragment.GeneralFragment;
import com.shixun.android.leaving_detection.Fragment.MainFragment;
import com.shixun.android.leaving_detection.Fragment.ModelListFragment;
import com.shixun.android.leaving_detection.Fragment.ProfileFragment;
import com.shixun.android.leaving_detection.Fragment.RawDataListFragment;
import com.shixun.android.leaving_detection.Fragment.RemodelFragment;
import com.shixun.android.leaving_detection.Fragment.ShowTextFragment;
import com.special.ResideMenu.ResideMenu;
import com.special.ResideMenu.ResideMenuItem;

import java.io.File;
import java.util.HashMap;
import java.util.StringTokenizer;

public class ManageActivity extends GeneralFragmentActivity implements ActionListener, View.OnClickListener{

    private ResideMenu resideMenu;
    private ResideMenuItem itemHome;
    private ResideMenuItem itemProfile;
    private ResideMenuItem itemSettings;
    private ResideMenuItem itemFolder;
    private Intent mServiceIntent;
    private boolean isPressureOn;
    private boolean isMagneticOn;
    private boolean isWifiScanOn;
    private boolean isTemperatureOn;
    private Bundle bundleFromSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.general_activity_fragment);
        setupMenu();//初始化菜单项
        bundleFromSetting = new Bundle();
    }

    /*
    程序运行,首先执行登录操作
     */
    @Override
    protected Fragment createFragment() {
        return new MainFragment();
    }

    /*
    响应菜单按钮点击
     */
    @Override
    public void onClick(View v) {

        //点击 profile
        if(v == itemProfile) {
            nevigateToFragment(ProfileFragment.class, false, bundleFromSetting);
        }

        //点击 folder
        if(v == itemFolder) {
            nevigateToFragment(RawDataListFragment.class, false, null);
        }

        if(v == itemHome) {
            nevigateToFragment(MainFragment.class, false, null);
        }

        //点击 profile
        if(v == itemSettings) {
            nevigateToFragment(AmbientListFragment.class, false, null);
        }

        openMenu(false); //隐藏菜单项
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
    响应点击文件列表,显示文件内容
     */
    @Override
    public void onShowFileText(File file) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("textFile",file);
        nevigateToFragment(ShowTextFragment.class, true, bundle);// 导航至ShowTextFragment
    }

    @Override
    public void updateSensor(File file) {
        String filename = file.getName();
        String newName = filename.replace("_", " ");
        StringTokenizer st = new StringTokenizer(newName," ");
        st.nextElement();
        while(st.hasMoreTokens()) {
            String str = st.nextElement().toString();
            switch(str.charAt(0)){
                case 'P':
                    if(str.charAt(1) == '1') {
                        isPressureOn = true;
                    } else {
                        isPressureOn = false;
                    }
                    break;
                case 'M':
                    if(str.charAt(1) == '1') {
                        isMagneticOn = true;
                    } else {
                        isMagneticOn = false;
                    }
                    break;
                case 'W':
                    if(str.charAt(1) == '1') {
                        isWifiScanOn= true;
                    } else {
                        isWifiScanOn = false;
                    }
                    break;
                case 'T':
                    if(str.charAt(1) == '1') {
                        isTemperatureOn = true;
                    } else {
                        isTemperatureOn = false;
                    }
            }
        }
    }

    /*
         响应成功采集传感器数据, 导航至文件列表
         */
    @Override
    public void onCollectSensorDataSuccessful() {
        nevigateToFragment(RawDataListFragment.class, false, null);
    }

    /*
         响应成功保存 setting, 导航至 remodel fragment
         */
    @Override
    public void onRemodel() {
        nevigateToFragment(RemodelFragment.class, true, bundleFromSetting);
    }

    /*
     激活滑动打开 menu
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return resideMenu.dispatchTouchEvent(ev);
    }

    @Override
    public void startMain(File file) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("loading", true);
        bundle.putSerializable("model",file);
        nevigateToFragment(MainFragment.class, false, bundle);
    }

    @Override
    public void addNewTrainingData() {
        nevigateToFragment(ChooseSensorFragment.class, true, null);
    }

    @Override
    public void addNewModel() {
        nevigateToFragment(RawDataListFragment.class, true, null);
    }

    @Override
    public void saveAmbient(HashMap<String, String> ambientMap) {
        bundleFromSetting.putSerializable("ambient", ambientMap);
    }

    /*
                    开启 / 关闭 detection 服务
                     */
    @Override
    public void startService() {
        if(mServiceIntent == null) {
            mServiceIntent = new Intent(this, MyService.class);
            mServiceIntent.putExtra("isPressureOn", isPressureOn);
            mServiceIntent.putExtra("isMagneticOn", isMagneticOn);
            mServiceIntent.putExtra("isWifiScanOn", isWifiScanOn);
            mServiceIntent.putExtra("isTemperatureOn", isTemperatureOn);
        }

        this.startService(mServiceIntent);
    }

    @Override
    public void stopService() {
        if (mServiceIntent != null) {
            stopService(mServiceIntent);
        }
        mServiceIntent = null;
    }

    /*
     获取用户 sensor 选项情况
     */
    @Override
    public void onSendDataBack(Bundle bundle) {
        isPressureOn = bundle.getBoolean("pressure");
        isMagneticOn = bundle.getBoolean("magnetic");
        isWifiScanOn = bundle.getBoolean("wifi");
        isTemperatureOn = bundle.getBoolean("temperature");

        bundleFromSetting.putBoolean("pressure", isPressureOn);
        bundleFromSetting.putBoolean("magnetic", isMagneticOn);
        bundleFromSetting.putBoolean("wifi", isWifiScanOn);
        bundleFromSetting.putBoolean("temperature", isTemperatureOn);
    }

    /*
     关闭程序时,用户自动登出
     */
    @Override
    protected void onDestroy() {
        if(mServiceIntent != null) {
            stopService(mServiceIntent);
        }
        mServiceIntent = null;

        super.onDestroy();
    }

    @Override
    public void showModelTypeDialog() {
//        ChooseModelType modelTypeDialog = new ChooseModelType();
//        modelTypeDialog.show(this.fragmentManager, "modelType");
        new BottomSheet
                .Builder(this, R.style.BottomSheet_StyleDialog)
                .title("PLEASE CHOOSE A MODEL")
                .sheet(R.menu.menu_select_model)
                .listener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case R.id.custom_model:
                            nevigateToFragment(ModelListFragment.class, true, null);
                            break;
                }
            }
        }).show();
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
        itemFolder   = new ResideMenuItem(this, R.drawable.icon_folder,   "Folder");
        itemSettings = new ResideMenuItem(this, R.drawable.icon_settings, "Setting");

        itemHome.setOnClickListener(this);
        itemProfile.setOnClickListener(this);
        itemFolder.setOnClickListener(this);
        itemSettings.setOnClickListener(this);

        resideMenu.addMenuItem(itemHome, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemProfile, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemFolder, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemSettings, ResideMenu.DIRECTION_LEFT);

        resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);
    }

    /**
     * 导向不同的 fragment
     * @param GeneralFragment 传入要导向的 fragment 类
     * @param isUpSet 是否设置up action
     */
    private void nevigateToFragment(Class GeneralFragment, boolean isUpSet, Bundle bundle) {

        try{
            Object obj = GeneralFragment.newInstance();

            com.shixun.android.leaving_detection.Fragment.GeneralFragment generalFragment = (GeneralFragment) obj;

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
}
