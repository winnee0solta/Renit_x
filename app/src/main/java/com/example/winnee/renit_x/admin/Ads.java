package com.example.winnee.renit_x.admin;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.winnee.renit_x.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class Ads extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ads);


        AdView mAdView1 = (AdView) findViewById(R.id.adView1);
        AdView mAdView2 = (AdView) findViewById(R.id.adView2);
        AdView mAdView3 = (AdView) findViewById(R.id.adView3);
        AdView mAdView4 = (AdView) findViewById(R.id.adView4);
        AdView mAdView5 = (AdView) findViewById(R.id.adView5);
        AdView mAdView6 = (AdView) findViewById(R.id.adView6);
        AdView mAdView7 = (AdView) findViewById(R.id.adView7);
        AdView mAdView8 = (AdView) findViewById(R.id.adView8);
        AdView mAdView9 = (AdView) findViewById(R.id.adView9);



        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView1.loadAd(adRequest);

        AdRequest adRequest1 = new AdRequest.Builder().build();
        mAdView2.loadAd(adRequest1);

        AdRequest adRequest11 = new AdRequest.Builder().build();
        mAdView3.loadAd(adRequest11);

        AdRequest adRequest1111 = new AdRequest.Builder().build();
        mAdView4.loadAd(adRequest1111);

        AdRequest adRequest2 = new AdRequest.Builder().build();
        mAdView5.loadAd(adRequest2);

        AdRequest adRequest3 = new AdRequest.Builder().build();
        mAdView6.loadAd(adRequest3);

        AdRequest adRequest4 = new AdRequest.Builder().build();
        mAdView7.loadAd(adRequest4);

        AdRequest adRequest5 = new AdRequest.Builder().build();
        mAdView8.loadAd(adRequest5);

        AdRequest adRequest6 = new AdRequest.Builder().build();
        mAdView9.loadAd(adRequest6);


    }
}
