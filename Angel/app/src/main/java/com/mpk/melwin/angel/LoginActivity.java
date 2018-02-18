package com.mpk.melwin.angel;

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

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText Uname,Pswd,Name,Decep;
    private Button Signup,Login;
    // private Spinner Type;
    private FirebaseAuth firebaseAuth,firebaseAuth1;
    private FirebaseAuth.AuthStateListener authStateListener;
    private DatabaseReference databaseReference;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Uname = (EditText) findViewById(R.id.uname);
        Pswd = (EditText) findViewById(R.id.pswd);
        Name = (EditText) findViewById(R.id.name);
        Decep = (EditText) findViewById(R.id.descp);
        Signup = (Button) findViewById(R.id.signup);
        Login = (Button) findViewById(R.id.login);
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        databaseReference = FirebaseDatabase.getInstance().getReference();


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


        Signup.setOnClickListener(this);

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

                            finish();
                            startActivity(new Intent(LoginActivity.this,ProfileActivity.class));

                        }
                    }
                });

    }
    private void SignupUser(){
        String email = Uname.getText().toString().trim();
        String password = Pswd.getText().toString().trim();
        final String name1 = Name.getText().toString().trim();
        final String des = Decep.getText().toString().trim();
        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(name1) || TextUtils.isEmpty(des)){
            Toast.makeText(getApplicationContext(),"Plz fill All The Fields",Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog.setMessage("LOADING!!!!");
        progressDialog.show();

        if(true) {

            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {

                                Toast.makeText(getApplicationContext(),"Could not Register sucessfully",Toast.LENGTH_SHORT).show();

                            }
                            else{


                                String uid = firebaseAuth.getCurrentUser().getUid();


                                com.mpk.melwin.angel.UserInfo object1 = (com.mpk.melwin.angel.UserInfo) new com.mpk.melwin.angel.UserInfo(name1,des,uid);

                                // UserInfo object1 = (UserInfo) new com.mpk.melwin.patientapp.UserInfo(name1,des,uid);
                                databaseReference.child("Users").child("Docter").child(uid).setValue(true);
                                databaseReference.child("UsersInfo").child("Docter").child(uid).setValue(object1);
                                Toast.makeText(getApplicationContext(), "Register sucessfully", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                                finish();
                                startActivity(new Intent(LoginActivity.this, ProfileActivity.class));
                            }

                        }

                    });

        }
        else{
            Toast.makeText(getApplicationContext(),"Every parameters must be filled!!!",Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            return;
        }
    }

    /*@Override
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
    }*/


    @Override
    public void onClick(View v) {
        if(v == Signup  ){
            SignupUser();
        }
        if(v == Login){
            LoginUser();
        }

    }
}

