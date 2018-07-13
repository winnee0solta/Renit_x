package com.example.winnee.renit_x.Profile;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.winnee.renit_x.Model.Constants;
import com.example.winnee.renit_x.Model.Postfetch;
import com.example.winnee.renit_x.R;
import com.example.winnee.renit_x.Security.Login;
import com.example.winnee.renit_x.Security.Register;
import com.example.winnee.renit_x.Tabs.Homefrag;
import com.example.winnee.renit_x.UI.SingleMyRentview;
import com.example.winnee.renit_x.UI.SingleRentview;
import com.example.winnee.renit_x.UI.TabHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Myposts extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Toolbar mToolbar;

    //for firebase
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private StorageReference mImageStorage;
    private DatabaseReference mUserDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myposts);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            //we have signed in
            Intent intent = new Intent(Myposts.this, Login.class);
            startActivity(intent);
            finish();
        }
        mToolbar = (Toolbar) findViewById(R.id.mypost_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(" My Posts ");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mToolbar.setNavigationIcon(R.drawable.ic_clear_black_24dp);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        firebase_data();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview2);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        set_recyclerview();

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    private void set_recyclerview() {

        String current_uid = mCurrentUser.getUid();
        DatabaseReference localDatabaseReference = FirebaseDatabase.getInstance().getReference().child(Constants.RENTS);
        localDatabaseReference.keepSynced(true);
        Query dbquery = localDatabaseReference.orderByChild(Constants.USERUID).equalTo(current_uid);


        FirebaseRecyclerAdapter<Postfetch,PostViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Postfetch, PostViewHolder>(
                Postfetch.class,
                R.layout.recycerview_v1_post,
                PostViewHolder.class,
                dbquery) {
            @Override
            protected void populateViewHolder(PostViewHolder viewHolder, final Postfetch model, int position) {

                // String post_key = getRef(position).toString();//for the whole url
                String post_key = getRef(position).getKey();//for the unique key only

                viewHolder.setTitleOfPost(model.getTitle());
                viewHolder.setPriceOfPost(model.getPrice());
                viewHolder.setUsernameOfPost(model.getUseruid());
                viewHolder.setAddressOfPost(model.getAddress());
                viewHolder.setPhoneOfPost(model.getContactno());
                viewHolder.setImageOfPost(model.getImage1());
                viewHolder.mview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent  = new Intent(Myposts.this, SingleMyRentview.class);
                        intent.putExtra(Constants.REFERENCEOFTHERENT, model.getRentuid());
                        startActivity(intent);
                    }
                });

            }
        };

        recyclerView.setAdapter(firebaseRecyclerAdapter);


    }


    public static class  PostViewHolder extends RecyclerView.ViewHolder{

        TextView post_tile,post_dec,post_price,post_username,post_address,post_contactno;
        ImageView userpp,postimage1;
        View mview;
        public PostViewHolder(View itemView) {
            super(itemView);
            mview = itemView;

            post_tile = (TextView) mview.findViewById(R.id.title_textview);
            // post_dec = (TextView) mview.findViewById(R.id.title_textview);
            post_price = (TextView) mview.findViewById(R.id.textview_price);//Rs:90000
            post_username = (TextView) mview.findViewById(R.id.user_name);
            post_address = (TextView) mview.findViewById(R.id.TextViewAddress);//Address : Mitrapark
            post_contactno = (TextView) mview.findViewById(R.id.TextViewcontactno);//Contact no: 9860988117
            userpp =  mview.findViewById(R.id.ppimageview);
            postimage1 =  mview.findViewById(R.id.imageViewUpload);
        }



        //for title
        public void  setTitleOfPost(String titlee){
            post_tile.setText(titlee);

        }


        //for price
        public void  setPriceOfPost(String price){
            post_price.setText("Rs:"+price);

        }
        //for username and ppimage
        public void  setUsernameOfPost(String username){
            final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            databaseReference.child(Constants.USERS).child(username).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    post_username.setText(dataSnapshot.child(Constants.USERNAME).getValue().toString().trim());

                    if (!dataSnapshot.child(Constants.PPIMAGE).getValue().toString().equals(Constants.NO))
                    {
                        Glide.with(itemView.getContext())
                                .load(dataSnapshot.child(Constants.PPIMAGE).getValue().toString())
                                .thumbnail(0.1f)
                                .into(userpp);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }
        //for address
        public void  setAddressOfPost(String address){
            post_address.setText("Address : "+address);

        }
        //for phone
        public void  setPhoneOfPost(String phone){
            post_contactno.setText("Contact no: "+phone);

        }
        //for image
        public void  setImageOfPost(String image){


            Glide.with(itemView.getContext())
                    .load(image)
                    .into(postimage1);

        }



    }

    private void firebase_data() {

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null)
        {

            Intent intent = new Intent(Myposts.this, Login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();

        }

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        //firebase storage ko root
        mImageStorage = FirebaseStorage.getInstance().getReference();
        mUserDatabase = FirebaseDatabase.getInstance().getReference();
        mUserDatabase.keepSynced(true);

    }

}
