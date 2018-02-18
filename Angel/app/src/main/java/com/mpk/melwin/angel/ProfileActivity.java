package com.mpk.melwin.angel;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {
    Button Map,logoutb,ebutton;
    TextView Name1;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setContentView(R.layout.activity_profile);
        Name1 = (TextView) findViewById(R.id.username);
        Map = (Button) findViewById(R.id.mapbutton);
        logoutb =(Button) findViewById(R.id.logoutbutton);
        firebaseAuth = FirebaseAuth.getInstance();


        FirebaseUser user = firebaseAuth.getCurrentUser();
        Name1.setText("Welcome "+user.getEmail());

        Map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                  startActivity(new Intent(ProfileActivity.this,Driver_map.class));
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
