package com.example.sathish.avs.util;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by SATHISH on 12/23/2016.
 */

public class Network extends Activity
{
    public Boolean isOnline(Context cont) {

        ConnectivityManager cm=(ConnectivityManager)cont.getSystemService(getBaseContext().CONNECTIVITY_SERVICE);

        NetworkInfo netInfo=cm.getActiveNetworkInfo();

        if(netInfo!=null && netInfo.isConnectedOrConnecting())
        {
            return true;
        }
        else
        {
            return false;
        }

    }
}
