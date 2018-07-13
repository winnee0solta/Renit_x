package com.example.winnee.renit_x.UI;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.bumptech.glide.Glide;
import com.example.winnee.renit_x.Model.Constants;
import com.example.winnee.renit_x.R;
import com.github.chrisbanes.photoview.PhotoView;
import com.github.chrisbanes.photoview.PhotoViewAttacher;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SinglePhotoView extends AppCompatActivity {

    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_single_photo_view);

        String imagetodisplay = getIntent().getStringExtra(Constants.IMAGE1);
        PhotoView photoView = (PhotoView) findViewById(R.id.photo_view);
        PhotoViewAttacher photoViewAttacher = new PhotoViewAttacher(photoView);
        photoViewAttacher.update();


        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        Glide.with(this)
                .load(imagetodisplay)
                .into(photoView);


    }

    private void getandsetadunit() {

        DatabaseReference dataref = FirebaseDatabase.getInstance().getReference();
        dataref.child(Constants.BANNER2).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                AdView mAdView = (AdView) findViewById(R.id.adView);
                mAdView.setAdUnitId(dataSnapshot.getValue().toString().trim());
                AdRequest adRequest = new AdRequest.Builder().build();
                mAdView.loadAd(adRequest);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
