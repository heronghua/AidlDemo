package com.archiermind.aidldemo.client;

import android.app.Application;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import com.archiermind.aidldemo.HttpsRequestAIDLInterface;

public class MainApplication extends Application {

    private static HttpsRequestAIDLInterface mBinder;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBinder = HttpsRequestAIDLInterface.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBinder = null;
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        
        Intent intent = new Intent("com.archiermind.aidldemo.server.HttpsRequestService");
        intent.setPackage("com.archiermind.aidldemo.server");
        bindService(intent, connection, Service.BIND_AUTO_CREATE);
    }

    public static HttpsRequestAIDLInterface getmBinder() {
        return mBinder;
    }
}
