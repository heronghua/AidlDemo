package com.archiermind.aidldemo.server;

import android.app.Application;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import com.archiermind.aidldemo.HttpsRequestAIDLInterface;


//aidl调用会导致被调用进程启动一个子线程来执行实际代码。
//因此后台service通过aidl调用前台进程的话，要注意无法在调用函数体内执行ui更新操作，因为此函数是在非主线程中执行的
public class MainApplication extends Application {

    private static final String TAG = MainApplication.class.getSimpleName();

    private static HttpsRequestAIDLInterface mBinder;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBinder = HttpsRequestAIDLInterface.Stub.asInterface(service);
            Log.i(TAG,"[onServiceConnected] + mService = " + mBinder);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, "[onServiceDisconnected] +");
            mBinder = null;
            Log.i(TAG, "[onServiceDisconnected] -");
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "[onCreate] +");
        Intent intent = new Intent(this, HttpsRequestService.class);
        bindService(intent, connection, Service.BIND_AUTO_CREATE);

        /*if (LeakCanary.isInAnalyzerProcess(this)) {//1
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);*/

        Log.i(TAG, "[onCreate] -");
       
    }

    public static HttpsRequestAIDLInterface getBinder() {
        return mBinder;
    }
}
