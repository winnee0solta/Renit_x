package com.example.winnee.renit_x.UI;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.winnee.renit_x.Model.Constants;
import com.example.winnee.renit_x.Model.Postfetch;
import com.example.winnee.renit_x.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.PatternSyntaxException;

public class Searchhome extends AppCompatActivity {

    private MaterialSearchView materialSearchView;
    private Toolbar mToolbar;
    private String search_text = null;
    private RecyclerView recyclerView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchhome);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
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

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview3);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);


        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        materialSearchView = (MaterialSearchView) findViewById(R.id.search_view);
        materialSearchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {

set_recyclerview();

            }

            @Override
            public void onSearchViewClosed() {

                search_text = null;
                set_recyclerview();
            }
        });
        materialSearchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if ((query != null) && (!query.isEmpty()))
                {
                    search_text = query;
                    set_recyclerview();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if ((newText != null) && (!newText.isEmpty()))
                {
                    search_text = newText;
                    set_recyclerview();
                }
                return true;
            }
        });


//
    }

    private void set_recyclerview() {


        DatabaseReference localDatabaseReference = FirebaseDatabase.getInstance().getReference().child(Constants.RENTS);
        localDatabaseReference.keepSynced(true);

        FirebaseRecyclerAdapter<Postfetch,PostViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Postfetch, PostViewHolder>(
                Postfetch.class,
                R.layout.recycerview_v1_post,
                PostViewHolder.class,
                localDatabaseReference) {
            @Override
            protected void populateViewHolder(PostViewHolder viewHolder, final Postfetch model, int position) {

                // String post_key = getRef(position).toString();//for the whole url
//                String post_key = getRef(position).getKey();//for the unique key only

                Boolean show = false;
                if (search_text != null)
                {



//                    String searchthis = search_text;
//                    String fetched = model.getAddress();
//                    try {
//                        String[] splitArray = fetched.split("\\s+");
//                        show = Arrays.asList(splitArray).contains(searchthis);
//
//                    } catch (PatternSyntaxException ex) {
//                        //
//                    }


                    //new
                    String text = model.getAddress().toLowerCase().trim();
                    String search = search_text.toLowerCase().trim();

                    List<String> tokens = new ArrayList<String>();


                    StringTokenizer st = new StringTokenizer(text);

                    //("---- Split by space ------");
                    while (st.hasMoreElements()) {
                        tokens.add(st.nextElement().toString());
                    }


                    for(int i=0;i<tokens.size();i++)
                    {
                        if(tokens.get(i).contains(search))
                        {
                            show = true;
                        }
                        else
                            show = false;
                    }

                    //newss
                }


                if (search_text == null && !show)
                {
                    viewHolder.mview.setVisibility(View.VISIBLE);
                    viewHolder.setTitleOfPost(model.getTitle());
                    viewHolder.setPriceOfPost(model.getPrice());
                    viewHolder.setUsernameOfPost(model.getUseruid());
                    viewHolder.setAddressOfPost(model.getAddress());
                    viewHolder.setPhoneOfPost(model.getContactno());
                    viewHolder.setImageOfPost(model.getImage1());
                    viewHolder.mview.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent  = new Intent(Searchhome.this, SingleRentview.class);
                            intent.putExtra(Constants.REFERENCEOFTHERENT, model.getRentuid());
                            startActivity(intent);
                        }
                    });
                }else if (search_text != null && show)
                {
                    viewHolder.mview.setVisibility(View.VISIBLE);
                    viewHolder.setTitleOfPost(model.getTitle());
                    viewHolder.setPriceOfPost(model.getPrice());
                    viewHolder.setUsernameOfPost(model.getUseruid());
                    viewHolder.setAddressOfPost(model.getAddress());
                    viewHolder.setPhoneOfPost(model.getContactno());
                    viewHolder.setImageOfPost(model.getImage1());
                    viewHolder.mview.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent  = new Intent(Searchhome.this, SingleRentview.class);
                            intent.putExtra(Constants.REFERENCEOFTHERENT, model.getRentuid());
                            startActivity(intent);
                        }
                    });
                }
               else {
                    viewHolder.mview.setVisibility(View.GONE);
                    viewHolder.setTitleOfPost(model.getTitle());
                    viewHolder.setPriceOfPost(model.getPrice());
                    viewHolder.setUsernameOfPost(model.getUseruid());
                    viewHolder.setAddressOfPost(model.getAddress());
                    viewHolder.setPhoneOfPost(model.getContactno());
                    viewHolder.setImageOfPost(model.getImage1());
                    viewHolder.mview.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent  = new Intent(Searchhome.this, SingleRentview.class);
                            intent.putExtra(Constants.REFERENCEOFTHERENT, model.getRentuid());
                            startActivity(intent);
                        }
                    });
                }


            }
        };

        recyclerView.setAdapter(firebaseRecyclerAdapter);



    }
    private void set_rescyclerview() {



        Boolean show = false;
        String searchthis = null;
            String fetched = null;
        try {
            String[] splitArray = fetched.split("\\s+");
            show = Arrays.asList(splitArray).contains(searchthis);

        } catch (PatternSyntaxException ex) {
            //
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_searchview, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        materialSearchView.setMenuItem(item);
        return true;
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

    @Override
    protected void onStart() {
        super.onStart();

        set_recyclerview();
    }
}
