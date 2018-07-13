package com.example.winnee.renit_x.Tabs;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.winnee.renit_x.Model.Constants;
import com.example.winnee.renit_x.Model.Postfetch;
import com.example.winnee.renit_x.R;
import com.example.winnee.renit_x.UI.SingleRentview;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class Homefrag extends Fragment {

    private RecyclerView recyclerView;
    private View rootview;



    public static Homefrag newInstance() {

        Bundle args = new Bundle();

        Homefrag fragment = new Homefrag();
        fragment.setArguments(args);
        return fragment;
    }
    public Homefrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootview = inflater.inflate(R.layout.fragment_homefrag, container, false);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView = rootview.findViewById(R.id.recyclerview1);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);


        set_recyclerview();


        return  rootview;
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
                        Intent intent  = new Intent(getActivity(), SingleRentview.class);
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

}
