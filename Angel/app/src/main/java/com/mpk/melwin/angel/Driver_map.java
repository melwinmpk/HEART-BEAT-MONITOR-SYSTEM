package com.mpk.melwin.angel;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.ContextMenu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Driver_map extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        RoutingListener{

    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    LatLng mPickupLocation;
    private Marker currentlocationMarker;
    public static final int  REQUEST_LOCATION_CODE = 99;
    private DatabaseReference databaseReference;
    double latitude,longitude;
    String customerID="",userid;
    Button Back;
    int Flag=0;
    DatabaseReference refAvailable,ref1Working;
    GeoFire geoFireAvailable,geoFireWorking;
    private List<Polyline> polylines;
    private static final int[] COLORS = new int[]{R.color.primary_dark_material_light}; //R.color.primary_dark,R.color.primary,R.color.primary_light,R.color.accent,
    float  distance = 0;
    private LinearLayout mCustomerInfo;
    private TextView mCustomerName,mCustomerDescription;
    private ImageView mCustomerProfileImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Flag=0;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_map);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            checkLocationPermission();
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
      //  customerID="";
        getAssignedCustomer();
        mCustomerInfo = (LinearLayout) findViewById(R.id.customerInfo);
        mCustomerName = (TextView) findViewById(R.id.customerName);
        mCustomerDescription = (TextView) findViewById(R.id.customerDescription);
        mCustomerProfileImage = (ImageView) findViewById(R.id.customerProfileImage);
        Back = (Button)findViewById(R.id.back);
        polylines = new ArrayList<>();
        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customerID="B";
                //onStop();
                Flag=1;
                onStop();
                 finish();
                startActivity(new Intent(Driver_map.this, ProfileActivity.class));
            }
        });
    }
    private void getAssignedCustomer(){
        String driverId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference assignedCustomerRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Docter").child(driverId).child("customerRideID");
        assignedCustomerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    customerID = dataSnapshot.getValue().toString();
                    getAssignedCustomerPickupLocation();
                    getAssignedCustomerInfo();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void getAssignedCustomerInfo(){
        DatabaseReference mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("UsersInfo").child("Patient").child(customerID);
        mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if(map.get("Name")!=null){
                        mCustomerName.setText("Name: "+map.get("Name").toString());
                    }
                    if(map.get("Descritpion")!=null){
                        mCustomerDescription.setText("Descritpion: "+map.get("Descritpion").toString());
                    }
                    if(map.get("profileImageUrl")!=null){
                       // mProfileImageUrl = map.get("profileImageUrl").toString();
                        Glide.with(getApplication()).load(map.get("profileImageUrl").toString()).into(mCustomerProfileImage);
                    }

                    mCustomerInfo.setVisibility(View.VISIBLE);


                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    /*private void getAssignedCustomer(){
        String driverId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference assignedCustomerRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Docter").child(driverId);

        assignedCustomerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Map<String,Object> map = (Map<String,Object>) dataSnapshot.getValue();
                    if(map.get("customerRideID")!= null){
                        customerID = map.get("customerRideID").toString();
                        getAssignedCustomerPickupLocation();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

}*/

private void getAssignedCustomerPickupLocation(){
    DatabaseReference assignedCustomerPickupRef = FirebaseDatabase.getInstance().getReference().child("Emergency").child(customerID).child("l");

    assignedCustomerPickupRef.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists() && !customerID.equals("")) {

                List<Object> map = (List<Object>) dataSnapshot.getValue();
                double locationlat = 0;
                double locationlng = 0;
                // call.setText("Locating the Ambulance.........");
                if (map.get(0) != null) {
                    locationlat = Double.parseDouble(map.get(0).toString());
                }
                if (map.get(1) != null) {
                    locationlng = Double.parseDouble(map.get(1).toString());
                }
                mPickupLocation = new LatLng(locationlat, locationlng);
                mMap.addMarker(new MarkerOptions().position(mPickupLocation).title("Patient Location"));
                getRoutetoMarker(mPickupLocation);
            }
        }
        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    });


}

    private void getRoutetoMarker(LatLng pickupLatLng) {
        Routing routing = new Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(this)
                .alternativeRoutes(false)
                .waypoints(new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude()), pickupLatLng)
                .build();
        routing.execute();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
        //buildGoogleApiClient();
        //mMap.setMyLocationEnabled(true);

    }

    protected synchronized void buildGoogleApiClient(){
        mGoogleApiClient =new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        if(getApplicationContext()!=null) {
            mLastLocation = location;
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());


            if(Flag==0) {
                userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                refAvailable = FirebaseDatabase.getInstance().getReference("UsersLocation");
                ref1Working = FirebaseDatabase.getInstance().getReference("DoctorsWorking");
                geoFireAvailable = new GeoFire(refAvailable);
                geoFireWorking = new GeoFire(ref1Working);



              //  mDriverMarker = mMap.addMarker(new MarkerOptions().position(driverLatLng).title("Ambulance Position"));




                switch (customerID) {
                    case "":
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                        geoFireWorking.removeLocation(userid);
                        geoFireAvailable.setLocation(userid, new GeoLocation(location.getLatitude(), location.getLongitude()));
                        break;
                    case "B":
                        geoFireWorking.removeLocation(userid);
                        geoFireAvailable.removeLocation(userid);
                        break;
                    default:
                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        LatLng driverLatLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());

                        builder.include(mPickupLocation);
                        builder.include(driverLatLng);
                        LatLngBounds bounds = builder.build();
                        getRoutetoMarker(mPickupLocation);
                        int width = getResources().getDisplayMetrics().widthPixels;
                        int padding = (int) (width*0.2);

                        Location loc1 = new Location("");
                        loc1.setLatitude(mPickupLocation.latitude);
                        loc1.setLongitude(mPickupLocation.longitude);

                        Location loc2 = new Location("");
                        loc2.setLatitude(driverLatLng.latitude);
                        loc2.setLongitude(driverLatLng.longitude);
                        distance = loc1.distanceTo(loc2);
                        if(distance > 200){
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                        mMap.animateCamera(cameraUpdate);}
                        else{
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                            mMap.animateCamera(CameraUpdateFactory.zoomTo(17));
                        }

                        geoFireAvailable.removeLocation(userid);
                        geoFireWorking.setLocation(userid, new GeoLocation(location.getLatitude(), location.getLongitude()));
                        break;
                }
            }
            if(currentlocationMarker != null){
                currentlocationMarker.remove();
            }
          /*  LatLng latlng = new LatLng(location.getLatitude(),location.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latlng);
            markerOptions.title("Current location");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

            currentlocationMarker = mMap.addMarker(markerOptions);
*/



          //  GeoFire geoFire = new GeoFire(ref);
            // geoFire.setLocation(userid, new GeoLocation(location.getLatitude(), location.getLongitude()));
        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(100);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            //return;

            //ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            //ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        }


    }

    public boolean checkLocationPermission(){
        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,android.Manifest.permission.ACCESS_FINE_LOCATION)){
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION_CODE);
            }
            else{
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION_CODE);

            }
            return false;
        }
        else
            return true;
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    protected void onStop() {
        super.onStop();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        // LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference.child("Users").child("Docter").child(userid).setValue(true);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("UsersLocation");
        DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("DoctorsWorking");
        GeoFire geoFire = new GeoFire(ref);
        GeoFire geoFire1 = new GeoFire(ref1);
        geoFire.removeLocation(userid);
        geoFire1.removeLocation(userid);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_LOCATION_CODE:
                if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
                        if(mGoogleApiClient == null){
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                }
                else{
                    Toast.makeText(this,"Permission Denied",Toast.LENGTH_LONG).show();
                }
        }
    }

    @Override
    public void onRoutingFailure(RouteException e) {
        if(e != null) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(this, "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
        if(polylines.size()>0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }
        polylines = new ArrayList<>();
        //add route(s) to the map.
        for (int i = 0; i <route.size(); i++) {

            //In case of more than 5 alternative routes
            int colorIndex = i % COLORS.length;

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(COLORS[colorIndex]));
            polyOptions.width(10 + i * 3);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = mMap.addPolyline(polyOptions);
            polylines.add(polyline);

            //Toast.makeText(getApplicationContext(),"Route "+ (i+1) +": distance - "+ route.get(i).getDistanceValue()+": duration - "+ route.get(i).getDurationValue(),Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onRoutingCancelled() {

    }
    private void erasePolylines(){
        for(Polyline line : polylines){
            line.remove();
        }
        polylines.clear();
    }
}