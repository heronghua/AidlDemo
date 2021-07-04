package com.archiermind.aidldemo.server;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import com.archiermind.aidldemo.Callback;
import com.archiermind.aidldemo.HttpsRequestAIDLInterface;
import android.util.Log;
import androidx.annotation.Nullable;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

public class HttpsRequestService extends Service {

    private static final String TAG = HttpsRequestService.class.getSimpleName();

    static class NetWorkThread extends Thread {
        private ArrayBlockingQueue<HttpsRequester> requestsQueue = new ArrayBlockingQueue<>(10);
        boolean start = true;
        public NetWorkThread(){
            super("NetWorkThread");
        }
        @Override
        public void run() {
            while (start) {
                try {
                    HttpsRequester httpsRequester = requestsQueue.take();
                    Log.d(TAG, "[run] + url =" + httpsRequester.getUrl());
                    String respond = httpsRequester.send();
                    httpsRequester.getCallback().onRespond(respond);
                    Log.i(TAG, "[run] -");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (RemoteException e){
                    e.printStackTrace();
                }

            }

        }

        public void putRequest(HttpsRequester httpsRequester) {
            try {
                requestsQueue.put(httpsRequester);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private NetWorkThread mNetWorkThread = new NetWorkThread();
    private HttpsRequestAIDLInterface.Stub mBinder = new HttpsRequestAIDLInterface.Stub() {
        @Override
        public void addRequest(String method, String url, Map params, Callback callback) {
            Log.i(TAG, "[addRequest] + ThreadName = " + Thread.currentThread().getName());
            HttpsRequester httpsRequester = new HttpsRequester.Builder(url)
                    .requestMethod(method)
                    .parameters(params)
                    .callback(callback)
                    .build();
            mNetWorkThread.putRequest(httpsRequester);
        }

        @Override
        public void get(String url, Callback callback) {
            addRequest("GET", url, null, callback);
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mNetWorkThread.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mNetWorkThread.start = false;
    }
}
