package com.example.winnee.renit_x.UI;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;

import com.example.winnee.renit_x.Model.Connection_checker;
import com.example.winnee.renit_x.Model.Constants;
import com.example.winnee.renit_x.R;
import com.example.winnee.renit_x.Security.Login;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Postrent extends AppCompatActivity {

    private static final int GALLERY_PICK = 2;
    private Connection_checker check;
    private View mview;
    private ImageView uploadimg;
    private Button uploadBtn;
    private EditText addressText,titleText,decText,priceText,contactText;
    private Uri resultUri = null;
    private ProgressDialog progressDialog;


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
        if (!permissionToReadAccepted ) Postrent.super.finish();

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postrent);

        check = new Connection_checker(this);

        int requestCode = 200;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, requestCode);
        }

        Toolbar mToolbar = (Toolbar) findViewById(R.id.upload_app_bar);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mToolbar.setNavigationIcon(R.drawable.ic_clear_black_24dp);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        firebase_data();

        uploadimg = (ImageView) findViewById(R.id.imageViewUpload);
        uploadBtn = (Button) findViewById(R.id.uploadasnap_btn);
        addressText = (EditText) findViewById(R.id.editTextAddress);
        decText = (EditText) findViewById(R.id.editTextDesc);
        priceText = (EditText) findViewById(R.id.editTextprice);
        contactText = (EditText) findViewById(R.id.editTextcontactno);
        titleText = (EditText) findViewById(R.id.editTexttitle);
        progressDialog = new ProgressDialog(this);

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (check.isconnection())
                {
                    //upload
                    String title_v = titleText.getText().toString().trim();
                    String dec_v = decText.getText().toString().trim();
                    String address_v = addressText.getText().toString().trim();
                    String price_v = priceText.getText().toString().trim();
                    String contact_v = contactText.getText().toString().trim();


                    if (TextUtils.isEmpty(title_v))
                    {
                        Snackbar.make(view, "Add some title in your post !!", Snackbar.LENGTH_SHORT)
                                .setAction("Action", null).show();
                        return;
                    }


                    if (TextUtils.isEmpty(address_v))
                    {
                        Snackbar.make(view, "Where is the location  !!", Snackbar.LENGTH_SHORT)
                                .setAction("Action", null).show();
                        return;
                    }
                    if (TextUtils.isEmpty(price_v))
                    {
                        Snackbar.make(view, "Add some price!!", Snackbar.LENGTH_SHORT)
                                .setAction("Action", null).show();
                        return;
                    }
                    if (TextUtils.isEmpty(contact_v))
                    {
                        Snackbar.make(view, "Add Contact no of the owner !!", Snackbar.LENGTH_SHORT)
                                .setAction("Action", null).show();
                        return;
                    }
                    if (resultUri == null)
                    {
                        Snackbar.make(view, "Insert a Image!!", Snackbar.LENGTH_SHORT)
                                .setAction("Action", null).show();
                        return;
                    }

                    uploadData(view,title_v,dec_v,address_v,price_v,contact_v,resultUri);
                }else
                {
                    //no internet connextion
                    Snackbar.make(view, "Check your internet connection !!", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                }
            }
        });

        uploadimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (check.isconnection())
                {
                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    startActivityForResult(photoPickerIntent, GALLERY_PICK);
                }else
                {
                    //no internet connextion
                    Snackbar.make(view, "Check your internet connection !!", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                }

            }
        });



    }

    private void uploadData(final View view, final String title_v, final String dec_v, final String address_v, final String price_v, final String contact_v, Uri resultUri)
    {
        progressDialog.setMessage("Travelling in time & space !!!");
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        final String uid = mCurrentUser.getUid();
        StorageReference filepath = mImageStorage
                .child(Constants.RENTS)
                .child(uid)
                .child(random()+".jpg");
        filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                if (task.isSuccessful())
                {
                    if (task.getResult().getDownloadUrl() != null)
                    {
                        //for date
                        SimpleDateFormat dateformat = new SimpleDateFormat("MM-dd-yyyy");
                        Date date = new Date();
//

//                        String datetime = dateformat.format(date);
//
//                        try {
//                            progressDialog.dismiss();
//                            Snackbar.make(view, dateformat.parse(datetime).toString(), Snackbar.LENGTH_LONG
//                            )
//                                    .setAction("Action", null).show();
//                            System.out.println(dateformat.parse(datetime));
//                        } catch (ParseException e) {
//                            e.printStackTrace();
//                        }
                        final String push_ko_key = mUserDatabase.child(Constants.RENTS).push().getKey();

                        final String download_url =task.getResult().getDownloadUrl().toString();
                        Map post_hasmap = new HashMap<String, String>();
                        post_hasmap.put(Constants.IMAGE1,download_url);
                        post_hasmap.put(Constants.TITLE,title_v);
                        post_hasmap.put(Constants.DEC,dec_v);
                        post_hasmap.put(Constants.ADDRESS,address_v);
                        post_hasmap.put(Constants.PRICE,price_v);
                        post_hasmap.put(Constants.CONTACT,contact_v);
                        post_hasmap.put(Constants.USERUID,uid);
                        post_hasmap.put(Constants.RENTUID,push_ko_key);
                        post_hasmap.put(Constants.REGISTEREDDATE, dateformat.format(date));




                        mUserDatabase.child(Constants.RENTS).child(push_ko_key).setValue(post_hasmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful())
                                {
                                    //upload complete
                                    progressDialog.dismiss();
                                    Snackbar.make(view, "Uploaded successfully!!", Snackbar.LENGTH_SHORT)
                                            .setAction("Action", null).show();
                                    finish();

                                }
                            }
                        });

                    }
                }else
                {
                    progressDialog.dismiss();
                }
            }
        });
        filepath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


            }
        });


    }

    private void firebase_data()
    {

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null)
        {
            Intent intent = new Intent(Postrent.this, Login.class);
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode  == GALLERY_PICK && resultCode == RESULT_OK)
        {
            resultUri= data.getData();
            uploadimg.setImageURI(resultUri);
        }

    }

    public static String random(){

        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(16);//10 is max length
        char tempChar;
        for (int i =0;i<randomLength; i++){
            tempChar =(char) (generator.nextInt(96)+32);
            randomStringBuilder.append(tempChar);
        }

        return randomStringBuilder.toString();
    }

}
