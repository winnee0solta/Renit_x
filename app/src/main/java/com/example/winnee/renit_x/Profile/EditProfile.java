package com.example.winnee.renit_x.Profile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.winnee.renit_x.Model.Constants;
import com.example.winnee.renit_x.R;
import com.example.winnee.renit_x.Security.Login;
import com.example.winnee.renit_x.UI.SingleRentview;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;
import java.util.Map;

public class EditProfile extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private Toolbar mToolbar;
    private static final int GALLERY_PICK = 2;
    //for firebase
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private StorageReference mImageStorage;
    private DatabaseReference mUserDatabase;

    private boolean permissionToReadAccepted = false;
    private String [] permissions = { "android.permission.READ_EXTERNAL_STORAGE"};

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 200:
                permissionToReadAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToReadAccepted ) EditProfile.super.finish();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        int requestCode = 200;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, requestCode);
        }


        mToolbar = (Toolbar) findViewById(R.id.editprofile_app_bar);
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

        progressDialog = new ProgressDialog(this);

        Button changepp = (Button) findViewById(R.id.changeppimage_btn);
        changepp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, GALLERY_PICK);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode  == GALLERY_PICK && resultCode == RESULT_OK){


            Uri imageuri = data.getData();
//            CropImage.activity(imageuri)
//                    .setAspectRatio(1,1)
//                    .start(EditProfile.this);



            progressDialog.setMessage("Making Some Changes !!!");
            progressDialog.show();
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);

            final String uid = mCurrentUser.getUid();

            StorageReference filepath = mImageStorage
                    .child(Constants.PROFILEIMAGES).child(uid+".jpg");
            filepath.putFile(imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    if (taskSnapshot.getDownloadUrl() != null)
                    {
                        final String download_url =taskSnapshot.getDownloadUrl().toString();
                        Map update_hasmap = new HashMap<String, String>();
                        update_hasmap.put(Constants.PPIMAGE,download_url);
                        //profile image upload
                        mUserDatabase.child(Constants.USERS)
                                .child(uid)
                                .updateChildren(update_hasmap)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful())
                                {
                                    progressDialog.dismiss();
                                    Toast.makeText(EditProfile.this,"Profile Image Changed",Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
                    }
                }
            });
            filepath.putFile(imageuri).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    progressDialog.dismiss();
                    Toast.makeText(EditProfile.this,"Some error occured !!",Toast.LENGTH_SHORT).show();

                }
            });


        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                progressDialog.setMessage("Making Some Changes !!!");
                progressDialog.show();
                progressDialog.setCancelable(false);
                progressDialog.setCanceledOnTouchOutside(false);

                Uri resultUri = result.getUri();
                final String uid = mCurrentUser.getUid();

                StorageReference filepath = mImageStorage
                        .child(Constants.PROFILEIMAGES).child(uid+".jpg");
                filepath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        if (taskSnapshot.getDownloadUrl() != null)
                        {
                            final String download_url =taskSnapshot.getDownloadUrl().toString();
                            Map update_hasmap = new HashMap<String, String>();
                            update_hasmap.put(Constants.PPIMAGE,download_url);
                            //profile image upload
                            mUserDatabase.child(Constants.USERS)
                                    .child(uid)
                                    .updateChildren(update_hasmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                    {
                                        progressDialog.dismiss();
                                        Toast.makeText(EditProfile.this,"Profile Image Changed",Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });
                        }
                    }
                });
                filepath.putFile(resultUri).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        progressDialog.dismiss();
                        Toast.makeText(EditProfile.this,"Some error occured !!",Toast.LENGTH_SHORT).show();

                    }
                });



            }
        }


    }



    private void firebase_data() {

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null)
        {

            Intent intent = new Intent(EditProfile.this, Login.class);
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
