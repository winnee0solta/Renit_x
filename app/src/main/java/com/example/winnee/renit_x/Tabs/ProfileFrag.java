package com.example.winnee.renit_x.Tabs;


import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.winnee.renit_x.Model.Connection_checker;
import com.example.winnee.renit_x.Model.Constants;
import com.example.winnee.renit_x.Profile.EditProfile;
import com.example.winnee.renit_x.Profile.Myposts;
import com.example.winnee.renit_x.R;
import com.example.winnee.renit_x.Security.Login;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFrag extends Fragment {

    private ImageView ppimage,backimg;

    private Connection_checker check;
    private TextView email,username;

    private  View rootview;

    //for firebase
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private StorageReference mImageStorage;
    private DatabaseReference mUserDatabase;

    public static ProfileFrag newInstance() {

        Bundle args = new Bundle();

        ProfileFrag fragment = new ProfileFrag();
        fragment.setArguments(args);
        return fragment;
    }
    public ProfileFrag() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootview =  inflater.inflate(R.layout.fragment_profilefrag, container, false);

        firebase_data();
        //for images
        backimg = (ImageView) rootview.findViewById(R.id.imagebackground);
        BitmapDrawable drawable = (BitmapDrawable) backimg.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        Bitmap blurred = blurRenderScript(bitmap, 25);//second parametre is radius
        backimg.setImageBitmap(blurred);

        ppimage = (ImageView) rootview.findViewById(R.id.ppimageview);
        email =(TextView)  rootview.findViewById(R.id.email_text);
        username =(TextView)  rootview.findViewById(R.id.user_name);

        Button logout = (Button)  rootview.findViewById(R.id.logout_btn);
        Button changeprofile = (Button)  rootview.findViewById(R.id.changeprofile_btn);
        Button myposts = (Button)  rootview.findViewById(R.id.mypost_btn);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                if (mAuth.getCurrentUser() == null)
                {
                    Intent intent = new Intent(getActivity(), Login.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    getActivity().finish();

                }
            }
        });

        changeprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),EditProfile.class));
            }
        });

        myposts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),Myposts.class));
            }
        });


        fillData();

        return rootview;
    }

    private void fillData() {
        String current_uid = mCurrentUser.getUid();
        mUserDatabase
                .child(Constants.USERS)
                .child(current_uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(Constants.PPIMAGE))
                        {
                            String ppimage_v = dataSnapshot.child(Constants.PPIMAGE).getValue().toString();
                            if (!ppimage_v.equals(Constants.NO))
                            {
                                Glide.with(ProfileFrag.this)
                                        .load(ppimage_v)
                                        .thumbnail(0.1f)
                                        .into(ppimage);
                                Glide.with(ProfileFrag.this)
                                        .load(ppimage_v)
                                        .into(backimg);
                            }
                        }
                        if (dataSnapshot.hasChild(Constants.USERNAME))
                        {
                            username.setText(dataSnapshot.child(Constants.USERNAME).getValue().toString());
                        }
                        if (dataSnapshot.hasChild(Constants.EMAIL))
                        {
                            email.setText(dataSnapshot.child(Constants.EMAIL).getValue().toString());
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

            Intent intent = new Intent(getActivity(), Login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            getActivity().finish();

        }

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        //firebase storage ko root
        mImageStorage = FirebaseStorage.getInstance().getReference();
        mUserDatabase = FirebaseDatabase.getInstance().getReference();
        mUserDatabase.keepSynced(true);

    }



    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private Bitmap blurRenderScript(Bitmap smallBitmap, int radius) {

        try {
            smallBitmap = RGB565toARGB888(smallBitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }


        Bitmap bitmap = Bitmap.createBitmap(
                smallBitmap.getWidth(), smallBitmap.getHeight(),
                Bitmap.Config.ARGB_8888);

        RenderScript renderScript = RenderScript.create(getActivity());

        Allocation blurInput = Allocation.createFromBitmap(renderScript, smallBitmap);
        Allocation blurOutput = Allocation.createFromBitmap(renderScript, bitmap);

        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(renderScript,
                Element.U8_4(renderScript));
        blur.setInput(blurInput);
        blur.setRadius(radius); // radius must be 0 < r <= 25
        blur.forEach(blurOutput);

        blurOutput.copyTo(bitmap);
        renderScript.destroy();

        return bitmap;

    }

    private Bitmap RGB565toARGB888(Bitmap img) throws Exception {
        int numPixels = img.getWidth() * img.getHeight();
        int[] pixels = new int[numPixels];

        //Get JPEG pixels.  Each int is the color values for one pixel.
        img.getPixels(pixels, 0, img.getWidth(), 0, 0, img.getWidth(), img.getHeight());

        //Create a Bitmap of the appropriate format.
        Bitmap result = Bitmap.createBitmap(img.getWidth(), img.getHeight(), Bitmap.Config.ARGB_8888);

        //Set RGB pixels.
        result.setPixels(pixels, 0, result.getWidth(), 0, 0, result.getWidth(), result.getHeight());
        return result;
    }
}
