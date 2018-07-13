package com.example.winnee.renit_x.Security;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
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
import android.widget.RelativeLayout;

import com.example.winnee.renit_x.R;
import com.example.winnee.renit_x.UI.TabHolder;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    AnimationDrawable animationDrawable;
    RelativeLayout relativeLayout;
    private FirebaseAuth.AuthStateListener authStateListener;
    private Button mLogin;
    private EditText memail,mpassword;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;

    private Toolbar mToolbar;
    private View view;

    public Login() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() !=null)
        {
            //we have signed in
            Intent intent = new Intent(Login.this,TabHolder.class);
            startActivity(intent);
            finish();
        }



        progressDialog = new ProgressDialog(this);
        memail = (EditText) findViewById(R.id.editTextEmail);
        mpassword = (EditText) findViewById(R.id.editTextPassword);
        mLogin = (Button) findViewById(R.id.login_button);
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = memail.getText().toString();
                String password = mpassword.getText().toString();

                if(TextUtils.isEmpty(email)){
                    Snackbar.make(view, "Watch out Email field is empty!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    // Toast.makeText(Login.this,"Email field is empty!",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    Snackbar.make(view, "Watch out Password field is empty!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    // Toast.makeText(Login.this,"password field is empty!",Toast.LENGTH_SHORT).show();
                    return;
                }

                loginUser(email,password);
            }
        });

        //for create account
        Button createaccount = (Button) findViewById(R.id.register_btn);
        createaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(Login.this,Register.class));
            }
        });
    }

    private void loginUser(String email, String password) {
        final View parentLayout = findViewById(android.R.id.content);

        progressDialog.setMessage("Travelling in time & space !!!");
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){

                    progressDialog.dismiss();
                    startActivity(new Intent(Login.this,TabHolder.class));
                    finish();

                }
                else {
                    progressDialog.dismiss();
                    Snackbar.make(parentLayout, "Sorry Some error occured", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                }

            }
        });


    }


}
