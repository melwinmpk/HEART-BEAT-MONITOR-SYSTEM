package com.mpk.melwin.patientapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText Uname,Pswd,Name,Decep;
    private Button Signup,Login;
   // private Spinner Type;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private DatabaseReference databaseReference;
    private ProgressDialog progressDialog;
    private ImageView mProfileImage;
    private Uri resultUri=null;
    private  String resultUrilink,uid;

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
        mProfileImage = (ImageView) findViewById(R.id.profileImage);

        if(firebaseAuth.getCurrentUser() != null){
            //profile activity
            finish();
            startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
        }
        /* authStateListener = new FirebaseAuth.AuthStateListener() {
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

        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });
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
    private void SignupUser(){
        String email = Uname.getText().toString().trim();
        String password = Pswd.getText().toString().trim();
         final String name1 = Name.getText().toString().trim();
         final String des = Decep.getText().toString().trim();
        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(name1) || TextUtils.isEmpty(des) || resultUri == null){
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
                                uid = firebaseAuth.getCurrentUser().getUid();

                               uploadphoto();


                                //String uid = firebaseAuth.getCurrentUser().getUid();

                               // Toast.makeText(LoginActivity.this, resultUrilink, Toast.LENGTH_SHORT).show();
                                com.mpk.melwin.patientapp.UserInfo object1 = (com.mpk.melwin.patientapp.UserInfo) new com.mpk.melwin.patientapp.UserInfo(name1,des,uid);

                               // UserInfo object1 = (UserInfo) new com.mpk.melwin.patientapp.UserInfo(name1,des,uid);

                                //databaseReference.child("Users").child("Patient").child(uid).setValue(object1);
                                databaseReference.child("Users").child("Patient").child(uid).setValue(true);
                                databaseReference.child("UsersInfo").child("Patient").child(uid).setValue(object1);
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

    private void uploadphoto() {
        StorageReference filePath = FirebaseStorage.getInstance().getReference().child("profile_images").child(uid);
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), resultUri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = filePath.putBytes(data);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                finish();
                return;
            }
        });
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                DatabaseReference mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("UsersInfo").child("Patient").child(uid);
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                resultUrilink = downloadUrl.toString();
                mCustomerDatabase.child("profileImageUrl").setValue(resultUrilink);
                // Map newImage = new HashMap();
                // newImage.put("profileImageUrl", downloadUrl.toString());
               // Toast.makeText(LoginActivity.this, "URL is"+ resultUrilink, Toast.LENGTH_SHORT).show();

                // finish();
                return;
            }
        });
    }

  /*  @Override
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == Activity.RESULT_OK){
            final Uri imageUri = data.getData();
            resultUri = imageUri;
            mProfileImage.setImageURI(resultUri);
        }
    }
}

