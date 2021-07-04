package com.archiermind.aidldemo.client;

import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.archiermind.aidldemo.Callback;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private TextView mDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDisplay = findViewById(R.id.tv_display);
    }

    public void load(View view) {
        try {
            MainApplication.getmBinder().get("https://www.baidu.com", new Callback.Stub() {
                @Override
                public void onRespond(final String msg) {

                    Log.i(TAG, "[onRespond] thread name :" + Thread.currentThread().getName());

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mDisplay.setText(Html.fromHtml(msg, Html.FROM_HTML_MODE_LEGACY));
                            }
                        });

                    }
                }
            });
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

}
