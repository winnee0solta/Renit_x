package com.example.winnee.renit_x.UI;

import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.bumptech.glide.Glide;
import com.example.winnee.renit_x.Model.Constants;
import com.example.winnee.renit_x.R;
import com.example.winnee.renit_x.Security.Login;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
               // checkupdate();
                startActivity(new Intent(Splash.this,Login.class));
             //   startActivity(new Intent(Splash.this,TabHolder.class));
                finish();

            }
        },3000);

        // TODO: 9/21/17  need to chek for new update also  ...
    }

    private void checkupdate() {

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child(Constants.UPDATE).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue().toString().equals(Constants.NO))
                {
                    startActivity(new Intent(Splash.this,Login.class));
                }else
                {
                    Snackbar.make(findViewById(android.R.id.content), "New Update Is Avilable!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
