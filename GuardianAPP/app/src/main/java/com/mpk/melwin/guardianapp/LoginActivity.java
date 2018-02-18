package com.mpk.melwin.guardianapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText Uname,Pswd;
    private Button Login;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private FirebaseAuth.AuthStateListener authStateListener;
   // private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Uname = (EditText) findViewById(R.id.uname);
        Pswd = (EditText) findViewById(R.id.pswd);
        Login = (Button) findViewById(R.id.login);
        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();
        //databaseReference = FirebaseDatabase.getInstance().getReference();

        if(firebaseAuth.getCurrentUser() != null){
            //profile activity
            finish();
            startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
        }
      /*  authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user  =FirebaseAuth.getInstance().getCurrentUser();
                if(user!=null) {
                    Intent intent = new Intent(LoginActivity.this,ProfileActivity.class);
                    finish();
                    startActivity(intent);
                    return;
                }
            }
        };*/
        Login.setOnClickListener(this);
    }

    private void LoginUser(){
        String email = Uname.getText().toString().trim();
        String password = Pswd.getText().toString().trim();
        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
            Toast.makeText(getApplicationContext(),"Plz fill Both The Fields",Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog.setMessage("LOADING!!!!");
        progressDialog.show();


        firebaseAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if(task.isSuccessful()){
                            //
                            finish();
                            startActivity(new Intent(LoginActivity.this,ProfileActivity.class));

                        }
                        else{
                            Toast.makeText(getApplicationContext(),"Username/Password Wrong",Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                });

    }



    @Override
    public void onClick(View v) {
        if(v == Login){
            LoginUser();
        }

    }

   /* @Override
    protected void onStart(){
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop(){
        super.onStop();
        if (authStateListener != null) {
            firebaseAuth.addAuthStateListener(authStateListener);

        }
    } */


}
