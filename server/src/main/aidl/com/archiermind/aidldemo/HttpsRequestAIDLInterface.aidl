// HttpsRequestAIDLInterface.aidl
package com.archiermind.aidldemo;

// Declare any non-default types here with import statements
import com.archiermind.aidldemo.Callback;

interface HttpsRequestAIDLInterface {

    void addRequest(String method, String url,in Map params,Callback callback);

    void get(String url,Callback callback);
}
