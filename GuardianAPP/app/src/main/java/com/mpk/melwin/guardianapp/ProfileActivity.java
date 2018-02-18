package com.mpk.melwin.guardianapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProfileActivity extends AppCompatActivity {
    Button Map,logoutb;
    private FirebaseAuth firebaseAuth;
    FirebaseUser user;
    TextView Pulse,Name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Map = (Button) findViewById(R.id.mapbutton);
        logoutb =(Button) findViewById(R.id.logoutbutton);
        Pulse = (TextView) findViewById(R.id.pulse);
        Name = (TextView) findViewById(R.id.name);
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        Name.setText("Patient: "+user.getEmail());
        DatabaseReference mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("UsersInfo").child("Patient").child(user.getUid());
        Map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                //  startActivity(new Intent(ProfileActivity.this,Driver_map.class));
            }
        });

        logoutb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(ProfileActivity.this,LoginActivity.class));
            }
        });
    }
}
