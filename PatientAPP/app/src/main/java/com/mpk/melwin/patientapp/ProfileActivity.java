package com.mpk.melwin.patientapp;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    public static final String TAG = "ProfileActivity";

    // Whether the Log Fragment is currently shown
    private boolean mLogShown;
    Button Map,logoutb,ebutton;
    TextView Name1,Pulse;
    private String mProfileImageUrl;
    private ImageView mProfileImage;
    private FirebaseAuth firebaseAuth;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Name1 = (TextView) findViewById(R.id.username);
        Map = (Button) findViewById(R.id.mapbutton);
        logoutb =(Button) findViewById(R.id.logoutbutton);
        ebutton =(Button) findViewById(R.id.emergencybutton);
        firebaseAuth = FirebaseAuth.getInstance();
        mProfileImage = (ImageView) findViewById(R.id.profileImage);
       // Pulse = (TextView) findViewById(R.id.pulse);


        user = firebaseAuth.getCurrentUser();
        Name1.setText("Welcome "+user.getEmail());

        DatabaseReference mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("UsersInfo").child("Patient").child(user.getUid());
        //java.util.Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
        fethprofileImg();

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            BluetoothFragment fragment = new BluetoothFragment();
            transaction.replace(R.id.sample_content_fragment, fragment);
            transaction.commit();
        }

      //  Glide.with(getApplication()).load(mProfileImageUrl).into(mProfileImage);
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
        ebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(ProfileActivity.this,Patient_Map.class));
            }
        });
    }
//////////////////////////////////////////////////////////////////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem logToggle = menu.findItem(R.id.menu_toggle_log);
        //logToggle.setVisible(findViewById(R.id.sample_output) instanceof ViewAnimator);
        //logToggle.setTitle(mLogShown ? R.string.sample_hide_log : R.string.sample_show_log);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_toggle_log:
                mLogShown = !mLogShown;
            /*    ViewAnimator output = (ViewAnimator) findViewById(R.id.sample_output);
                if (mLogShown) {
                    output.setDisplayedChild(1);
                } else {
                    output.setDisplayedChild(0);
                }*/
                supportInvalidateOptionsMenu();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
//////////////////////////////////////////////////////////////////////////////////////////////////
    private void fethprofileImg() {
        DatabaseReference mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("UsersInfo").child("Patient").child(user.getUid());
        mCustomerDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();

                    if(map.get("profileImageUrl")!=null){
                        mProfileImageUrl = map.get("profileImageUrl").toString();
                        Glide.with(getApplication()).load(mProfileImageUrl).into(mProfileImage);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}
