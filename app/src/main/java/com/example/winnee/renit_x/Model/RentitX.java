package com.example.winnee.renit_x.Model;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by winnee on 04/10/2017.
 */

public class RentitX extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);



    }
}