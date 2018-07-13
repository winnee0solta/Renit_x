package com.example.winnee.renit_x.UI;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ButtonBarLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.winnee.renit_x.Model.Constants;
import com.example.winnee.renit_x.R;
import com.example.winnee.renit_x.Security.Login;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SingleMyRentview extends AppCompatActivity {

    private  Button deletepost;
    private Toolbar mToolbar;
    private String  product_ref;
    //for firebase
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private StorageReference mImageStorage;
    private DatabaseReference mUserDatabase;

    TextView post_tile,post_dec,post_price,post_username,post_address,post_contactno,post_date;
    ImageView userpp,postimage1;
    private AdView mAdView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_my_rentview);

        deletepost = (Button) findViewById(R.id.delete_btn);

        deletepost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mUserDatabase.child(Constants.RENTS).child(product_ref).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isComplete())
                        {
                            finish();
                        }
                    }
                });
            }
        });

        if (getIntent().getStringExtra(Constants.REFERENCEOFTHERENT) == null)
        {
            finish();
        }
        product_ref = getIntent().getStringExtra(Constants.REFERENCEOFTHERENT);



        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        mToolbar = (Toolbar) findViewById(R.id.singlerentpost_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("  ");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mToolbar.setNavigationIcon(R.drawable.ic_clear_black_24dp);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        firebase_data();


        post_tile = (TextView) findViewById(R.id.title_textview);
        post_dec = (TextView) findViewById(R.id.dec_textview);
        post_price = (TextView) findViewById(R.id.textview_price);//Rs:90000
        post_username = (TextView) findViewById(R.id.user_name);
        post_address = (TextView) findViewById(R.id.TextViewAddress);//Address : Mitrapark
        post_contactno = (TextView) findViewById(R.id.TextViewcontactno);//Contact no: 9860988117
        post_date = (TextView) findViewById(R.id.TextViewpostdate);//Posted On : 09102910
        userpp = (ImageView) findViewById(R.id.ppimageview);
        postimage1 = (ImageView) findViewById(R.id.imageViewUpload);


        fill_data();

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

    private void fill_data() {

        mUserDatabase.child(Constants.RENTS).child(product_ref).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {

                if (!dataSnapshot.hasChild(Constants.TITLE) || !dataSnapshot.hasChild(Constants.DEC)||!dataSnapshot.hasChild(Constants.IMAGE1))
                {
                    finish();
                }
                post_tile.setText(dataSnapshot.child(Constants.TITLE).getValue().toString().trim());
                post_dec.setText(dataSnapshot.child(Constants.DEC).getValue().toString().trim());
                post_price.setText("RS : "+dataSnapshot.child(Constants.PRICE).getValue().toString().trim());
                post_address.setText("Address : "+dataSnapshot.child(Constants.ADDRESS).getValue().toString().trim());
                post_contactno.setText("Contact no: "+dataSnapshot.child(Constants.CONTACT).getValue().toString().trim());
                mUserDatabase.child(Constants.USERS).child(dataSnapshot.child(Constants.USERUID).getValue().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        post_username.setText(dataSnapshot.child(Constants.USERNAME).getValue().toString().trim());

                        if (!dataSnapshot.child(Constants.PPIMAGE).getValue().toString().equals(Constants.NO))
                        {
                            Glide.with(SingleMyRentview.this)
                                    .load(dataSnapshot.child(Constants.PPIMAGE).getValue().toString())
                                    .thumbnail(0.1f)
                                    .into(userpp);
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                Glide.with(SingleMyRentview.this)
                        .load(dataSnapshot.child(Constants.IMAGE1).getValue().toString())
                        .into(postimage1);

                postimage1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent  = new Intent(SingleMyRentview.this,SinglePhotoView.class);
                        intent.putExtra(Constants.IMAGE1,dataSnapshot.child(Constants.IMAGE1).getValue().toString() );
                        startActivity(intent);

                    }
                });

                //for date
                SimpleDateFormat dateformat = new SimpleDateFormat("MM-dd-yyyy");
                String datetime = dataSnapshot.child(Constants.REGISTEREDDATE).getValue().toString();

                try {

                    Date date = dateformat.parse(datetime);
                    post_date.setText("Posted On : "+date.toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void firebase_data() {

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null)
        {

            Intent intent = new Intent(SingleMyRentview.this, Login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();

        }

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        //firebase storage ko root
        mUserDatabase = FirebaseDatabase.getInstance().getReference();
        mUserDatabase.keepSynced(true);

    }
}
