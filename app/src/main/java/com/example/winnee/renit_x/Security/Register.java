package com.example.winnee.renit_x.Security;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.winnee.renit_x.Model.Constants;
import com.example.winnee.renit_x.R;
import com.example.winnee.renit_x.UI.TabHolder;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class Register extends AppCompatActivity {

    private Button mopenLogin;
    private Button mRegister;
    private EditText memail,mpassword,mUsername;
    private ProgressDialog progressDialog;
    private View view;
    private Toolbar mToolbar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            //we have signed in
            Intent intent = new Intent(Register.this, TabHolder.class);
            startActivity(intent);
            finish();
        }

        mToolbar = (Toolbar) findViewById(R.id.register_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(" Join Us ");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mToolbar.setNavigationIcon(R.drawable.ic_clear_black_24dp);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        progressDialog = new ProgressDialog(this);
        mUsername = (EditText) findViewById(R.id.editTextUsername);
        memail = (EditText) findViewById(R.id.editTextEmail);
        mpassword = (EditText) findViewById(R.id.editTextPassword);
        mRegister = (Button) findViewById(R.id.Register_button);


        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = memail.getText().toString();
                String password = mpassword.getText().toString();
                String username = mUsername.getText().toString();

                if (TextUtils.isEmpty(username)) {
                    Snackbar.make(view, "Watch out Username field is empty!", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                    //Toast.makeText(Register.this,"Username field is empty!",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(email)) {
                    Snackbar.make(view, "Watch out Email field is empty!", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                    //Toast.makeText(Register.this,"Email field is empty!",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Snackbar.make(view, "Watch out password field is empty!", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                    //Toast.makeText(Register.this,"password field is empty!",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!(username.length() <= 17)) {
                    Snackbar.make(view, "Its too Long to be username !!", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                    return;
                }

                Create_User(username, email, password);
            }
        });


    }

    private void Create_User(final String username, final String email, final String password) {


        progressDialog.setMessage("Travelling in time & space !!!");
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {


                if (task.isSuccessful()) {
                    final String customer_id = task.getResult().getUser().getUid();

                    final HashMap<String, String> userMap = new HashMap<String, String>();
                    userMap.put(Constants.USERNAME, username);
                    userMap.put(Constants.EMAIL, email);
                    userMap.put(Constants.PASSWORD, password);
                    userMap.put(Constants.PPIMAGE, Constants.NO);



                    //getting current date
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
                    String currentDateandTime = sdf.format(new Date());

                    if (currentDateandTime != null)
                        userMap.put(Constants.REGISTEREDDATE, currentDateandTime);
                    else
                        userMap.put(Constants.REGISTEREDDATE, Constants.NO);


                    DatabaseReference mdatabase = FirebaseDatabase.getInstance().getReference()
                            .child(Constants.USERS)
                            .child(customer_id);
                    mdatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                startActivity(new Intent(Register.this, TabHolder.class));
                            } else {
                                progressDialog.dismiss();

                                Toast.makeText(Register.this, "open the window I am stuck..", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    });

                } else {
                    progressDialog.dismiss();
                    Toast.makeText(Register.this, "open the window I am stuck..", Toast.LENGTH_SHORT).show();
                    return;
                }


            }
        });
    }

}

