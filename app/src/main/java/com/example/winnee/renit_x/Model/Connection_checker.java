package com.example.winnee.renit_x.Model;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Winnee on 4/19/2017.
 */


public class Connection_checker {
    Context context;



    public Connection_checker(Context context) {
        this.context = context;
    }
    public  boolean isconnection()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if(connectivityManager != null)
        {
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            if (info != null)
            {
                if(info.getState()== NetworkInfo.State.CONNECTED)
                {
                    return true;
                }
            }

        }
        return false;
    }
}
