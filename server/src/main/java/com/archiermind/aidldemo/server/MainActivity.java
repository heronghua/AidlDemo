package com.archiermind.aidldemo.server;

import android.os.*;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.archiermind.aidldemo.Callback;

public class MainActivity extends AppCompatActivity implements Handler.Callback {

    private static final String TAG = MainActivity.class.getSimpleName();
    private Handler mMainHandler;
    private TextView displayTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMainHandler = new Handler(getMainLooper(),this);
        displayTV = findViewById(R.id.tv_display);
    }

    public void update(View view) {
        Log.i(TAG, "[update] Thread " + Thread.currentThread().getName());
        try {
            MainApplication.getBinder().get("https://www.baidu.com",new Callback.Stub() {
                @Override
                public void onRespond(final String msg) {
                    Log.i(TAG, "[onRespond] Thread name = "+Thread.currentThread().getName());
                    mMainHandler.obtainMessage(0x123,msg).sendToTarget();

                }
            });
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }


    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what){
            case 0x123:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    displayTV.setText(Html.fromHtml(msg.obj+"", Html.FROM_HTML_MODE_LEGACY));
                    Log.d(TAG, "[handleMessage] Thread " + Thread.currentThread().getName());
                }
                break;
            default:
                break;
        }
        return true;
    }
}
