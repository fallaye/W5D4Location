package mobileappscompany.w5d4location;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 10;
    private static final String TAG = "MainActivity";
    private String[] permissionToBeAsked;
    private FusedLocationProviderClient providerClient;
    private TextView tvLocation;
    private Location currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvLocation = findViewById(R.id.tvLocation);
        permissionToBeAsked = new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
        };
        checkLocationPermission();
    }


    private void checkLocationPermission(){
        // Here, thisActivity is the current activity
        /*if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {*/
        if (ContextCompat.checkSelfPermission(this,
                permissionToBeAsked[0] )
                != PackageManager.PERMISSION_GRANTED
                &&
                ContextCompat.checkSelfPermission(this,
                        permissionToBeAsked[1]) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    permissionToBeAsked[0] )
                    &&
                    ActivityCompat.shouldShowRequestPermissionRationale(this,
                            permissionToBeAsked[1])) {

                showExplanation();

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                askPermission();

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }else {
            getLocation();
        }
    }

    @SuppressLint("MissingPermission")
    private void getLocation(){

        providerClient = LocationServices.getFusedLocationProviderClient(this);

        providerClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        Log.d(TAG, "onSuccess: " + location.toString());

                        String latitude = String.valueOf(location.getLatitude());
                        String longitude = String.valueOf(location.getLongitude());

                        //tvLocation

                        currentLocation = location;

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: " + e.toString());

                    }
                });

    }

    private void askPermission() {
        /*String[] permissionToBeAsked = new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
        };
*/
        ActivityCompat.requestPermissions(this,
                permissionToBeAsked,
                MY_PERMISSIONS_REQUEST_LOCATION);
    }

    private void showExplanation() {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("Location Request")
                .setMessage("You need to allow the location permission")
                .setNegativeButton("Uninstall", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
uninstallApplication();
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        askPermission();
                    }
                })
                .create();
        alertDialog.show();
    }

    private void uninstallApplication(){
        Intent intent = new Intent(Intent.ACTION_DELETE);
        intent.setData(Uri.parse("package:mobileappscompany.w5d4location"));
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Log.d(TAG, "onRequestPermissionResult: Granted");
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.g

                    getLocation();


                } else {
                    Log.d(TAG, "onRequestPermissionResult: Denied");

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void goToMapsActivity(View view) {
        if(currentLocation != null) {
            Intent intent = new Intent(this, MapsActivity.class);
            intent.putExtra("location", currentLocation);
            startActivity(intent);


        }else {
            Toast.makeText(this, "No location to show", Toast.LENGTH_LONG);
        }
    }
}
