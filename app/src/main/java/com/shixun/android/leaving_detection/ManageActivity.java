package com.shixun.android.leaving_detection;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.cocosw.bottomsheet.BottomSheet;
import com.shixun.android.leaving_detection.Activity.ActionListener;
import com.shixun.android.leaving_detection.Activity.GeneralFragmentActivity;
import com.shixun.android.leaving_detection.Detection.MyService;
import com.shixun.android.leaving_detection.Fragment.AmbientListFragment;
import com.shixun.android.leaving_detection.Fragment.ChooseSensorFragment;
import com.shixun.android.leaving_detection.Fragment.DetectionFragment;
import com.shixun.android.leaving_detection.Fragment.GeneralFragment;
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
    protected Fragment createFragment() {
        return new DetectionFragment();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.general_activity_fragment);
        setupMenu();
        bundleFromSetting = new Bundle();
    }

    @Override
    public void onClick(View v) {

        if(v == itemProfile) {
            nevigateToFragment(ProfileFragment.class, false, bundleFromSetting);
        }

        //点击 folder
        if(v == itemFolder) {
            nevigateToFragment(RawDataListFragment.class, false, null);
        }

        if(v == itemHome) {
            nevigateToFragment(DetectionFragment.class, false, null);
        }

        //点击 setting
        if(v == itemSettings) {
            nevigateToFragment(AmbientListFragment.class, false, null);
        }

        openMenu(false); //隐藏菜单项
    }

    @Override
    public void onUpNevigationClick() {
        if(getSupportFragmentManager().getBackStackEntryCount() > 0) {
            this.fragmentManager.popBackStack();
        }
    }

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
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        if(isWifiScanOn) {
            wifiManager.setWifiEnabled(true);
        } else {
            wifiManager.setWifiEnabled(false);
        }
    }

    @Override
    public void onCollectSensorDataSuccessful() {
        nevigateToFragment(RawDataListFragment.class, false, null);
    }

    @Override
    public void onRemodel() {
        nevigateToFragment(RemodelFragment.class, true, bundleFromSetting);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return resideMenu.dispatchTouchEvent(ev);
    }

    @Override
    public void startDetection(File modelFile) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("loading", true);
        bundle.putSerializable("model",modelFile);
        nevigateToFragment(DetectionFragment.class, false, bundle);
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
        Log.d("*******************", ambientMap.toString());
        bundleFromSetting.putSerializable(getString(R.string.key_ambient), ambientMap);
    }

    @Override
    public void startService() {
        if(mServiceIntent == null) {
            mServiceIntent = new Intent(this, MyService.class);
            mServiceIntent.putExtra(getString(R.string.key_pressure_on), isPressureOn);
            mServiceIntent.putExtra(getString(R.string.key_magnetic_on), isMagneticOn);
            mServiceIntent.putExtra(getString(R.string.key_wifi_scan_on), isWifiScanOn);
            mServiceIntent.putExtra(getString(R.string.key_temperature_on), isTemperatureOn);
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

    @Override
    public void onSaveSensorChosen(Bundle bundle) {
        isPressureOn = bundle.getBoolean(getString(R.string.key_pressure_on));
        isMagneticOn = bundle.getBoolean(getString(R.string.key_magnetic_on));
        isWifiScanOn = bundle.getBoolean(getString(R.string.key_wifi_scan_on));
        isTemperatureOn = bundle.getBoolean(getString(R.string.key_temperature_on));

        bundleFromSetting.putBoolean(getString(R.string.key_pressure_on), isPressureOn);
        bundleFromSetting.putBoolean(getString(R.string.key_magnetic_on), isMagneticOn);
        bundleFromSetting.putBoolean(getString(R.string.key_wifi_scan_on), isWifiScanOn);
        bundleFromSetting.putBoolean(getString(R.string.key_temperature_on), isTemperatureOn);
    }

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
        new BottomSheet
                .Builder(this, R.style.BottomSheet_StyleDialog)
                .title(getString(R.string.choose_model_dialog_title))
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

    private void setupMenu() {
        // attach to current activity
        resideMenu = new ResideMenu(this);
        resideMenu.setBackground(R.drawable.menu_background);
        resideMenu.attachToActivity(this);

        // create menu items;
        itemHome     = new ResideMenuItem(this, R.drawable.icon_home,     getString(R.string.menu_home));
        itemProfile  = new ResideMenuItem(this, R.drawable.icon_profile,  getString(R.string.menu_profile));
        itemFolder   = new ResideMenuItem(this, R.drawable.icon_folder,   getString(R.string.menu_folder));
        itemSettings = new ResideMenuItem(this, R.drawable.icon_settings, getString(R.string.menu_setting));

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
}
