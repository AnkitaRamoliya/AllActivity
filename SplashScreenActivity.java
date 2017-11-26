package com.oozeetech.bizdesk.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.crashlytics.android.Crashlytics;
import com.oozeetech.bizdesk.BaseActivity;
import com.oozeetech.bizdesk.R;
import com.oozeetech.bizdesk.ui.drawer.DrawerActivity;
import com.oozeetech.bizdesk.utils.Utils;

import io.fabric.sdk.android.Fabric;

public class SplashScreenActivity extends BaseActivity {

//    BroadcastReceiver deviceIdReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            redirect();
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_splash_screen);

        redirect();

        Utils.getHashKey(getApplicationContext());
//        String a = FirebaseInstanceId.getInstance().getToken();
//        log.LOGE(a);

/*        if (Utils.isInternetConnected(getActivity()))
            if (pref.getString(Constants.FCM_KEY).equalsIgnoreCase("")) {
                try {
                    IntentFilter filter = new IntentFilter(Constants.ACTION_DEVICE_ID);
                    LocalBroadcastManager.getInstance(getActivity()).registerReceiver(deviceIdReceiver, filter);
                    startService(new Intent(getActivity(), MyFirebaseInstanceIDService.class));
                } catch (Exception e) {
                    log.LOGE(e.getMessage());
                }
            } else {
                redirect();
            }
        else showNoInternetDialog();*/

    }

    private void redirect() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isLoggedIn()) {
                    Intent intent = new Intent(getActivity(), DrawerActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, 2000);

    }
}
