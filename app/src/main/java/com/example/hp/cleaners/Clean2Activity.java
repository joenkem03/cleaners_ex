package com.example.hp.cleaners;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;

import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import android.widget.Spinner;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import android.widget.DatePicker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class Clean2Activity extends AppCompatActivity {

    private long lastClickTime = 0;
    private DatePicker datePicker;
    private Calendar calendar;
    private EditText servSchedule;
    private EditText foundLocation;
    private int year, month, day;
    private static final int REQUEST_LOCATION = 1;
    private LocationManager mLocationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clean);
        /*
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */
        ActivityCompat.requestPermissions(Clean2Activity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        servSchedule = findViewById(R.id.date);
        foundLocation = findViewById(R.id.address);



        final Spinner cleanSpinner = findViewById(R.id.cleanSelect);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> cleanSpinnerArrayAdapter = ArrayAdapter.createFromResource(this,
                R.array.cleanType_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        cleanSpinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        cleanSpinner.setAdapter(cleanSpinnerArrayAdapter);
        cleanSpinner.setVisibility(View.INVISIBLE);


        final String serviceFacility = getIntent().getStringExtra("serviceFacility");
        final String reqService = getIntent().getStringExtra("reqService");
        final String serviceAmount = getIntent().getStringExtra("serviceAmount");
        final String facilitySize = getIntent().getStringExtra("facilitySize");
        final String serviceProviders = getIntent().getStringExtra("serviceProviders");



        Button sendButton = findViewById(R.id.sendButton);

        sendButton.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {

                        final String cleanTypeChoice = "Request";

                        EditText selectDate = findViewById(R.id.date);
                        final String schedule = selectDate.getText().toString();

                        EditText facilityName = findViewById(R.id.facilityName);
                        final String facName = facilityName.getText().toString();

                        EditText facilityAddress = findViewById(R.id.address);
                        final String facAddress = facilityAddress.getText().toString();

                        EditText cName = findViewById(R.id.name);
                        final String contactName = cName.getText().toString();

                        EditText u_phone = findViewById(R.id.phone);
                        final String phone = u_phone.getText().toString();

                        EditText u_mail = findViewById(R.id.emailAddress);
                        final String email = u_mail.getText().toString();

                        if(cleanTypeChoice == null || cleanTypeChoice.length() == 0 ||schedule == null
                                || schedule.length() == 0 || facName == null || facName.length() == 0
                                || facAddress == null || facAddress.length() == 0 || contactName == null
                                || contactName.length() == 0 || phone == null || phone.length() == 0 ||
                                email == null || email.length() == 0){
                            Toast.makeText(getApplicationContext(), "field missing", Toast.LENGTH_LONG).show();
                            return;
                        }

                        // Invoke RESTful Web Service with Http parameters
                        //call_POST(params, url_req, email, password);
                        if(Validate.isEmailValid(email)) {


                            // preventing double, using threshold of 10000 ms
                            if (SystemClock.elapsedRealtime() - lastClickTime < 10000) {
                                return;
                            }

                            lastClickTime = SystemClock.elapsedRealtime();

                            runPost(facName, facAddress, contactName, phone, email, schedule, serviceFacility, reqService, facilitySize, serviceProviders, cleanTypeChoice, serviceAmount);
                        }else {
                            Toast.makeText(getApplicationContext(), "Email not valid", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                }
        );
    }


    @SuppressWarnings("deprecation")
    public void setDate(View view) {

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);

        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        showDate(year, month+1, day);

        showDialog(999);
        //Toast.makeText(getApplicationContext(), "ca",
        //      Toast.LENGTH_SHORT)
        //    .show();
    }


    @SuppressWarnings("deprecation")
    public void setAddress(View view) {

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if(!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            noGpsAlertMessage();
        }
        else  if(mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            getLocation();
        }

    }

    private void getLocation() {
        if(ActivityCompat.checkSelfPermission(Clean2Activity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (Clean2Activity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(Clean2Activity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {
            Location location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if(location != null) {
                double lat = location.getLatitude();
                double lng = location.getLongitude();
                String lattitude = String.valueOf(lat);
                String longitude = String.valueOf(lng);

                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> addressLists = null;
                try {
                    addressLists = geocoder.getFromLocation(lat, lng, 1);



                    //String[] addressArray = new String[addressList.size()];
                    //addressArray = addressList.toArray(addressArray);
                    Address addressList = addressLists.get(0);
                    ArrayList<String> addressFound = new ArrayList<String>();

                    // Fetch the address lines using getAddressLine,
                    // join them, and send them to the thread.
                    for(int i = 0; i <= addressList.getMaxAddressLineIndex(); i++) {
                        addressFound.add(addressList.getAddressLine(i));
                    }

                    //foundLocation.setText(lattitude+","+longitude);
                    foundLocation.setText(TextUtils.join(System.getProperty("line.separator"), addressFound));
                    foundLocation.setFocusableInTouchMode(true);
                    foundLocation.setFocusable(true);
                } catch (IOException e) {
                    Toast.makeText(this, "Your location not found, please enter your address", Toast.LENGTH_LONG).show();
                    foundLocation.setFocusableInTouchMode(true);
                    foundLocation.setFocusable(true);
                    //e.printStackTrace();
                }
            } else{
                Toast.makeText(this, "Your location not found, please enter your address", Toast.LENGTH_LONG).show();
                foundLocation.setFocusableInTouchMode(true);
                foundLocation.setFocusable(true);
            }

        }
    }

    private void noGpsAlertMessage() {
        AlertDialog.Builder alertBuild = new AlertDialog.Builder(this);
        alertBuild.setMessage("Please Turn ON")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        AlertDialog alertDialog = alertBuild.create();
        alertDialog.show();
    }


    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this,
                    myDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    showDate(arg1, arg2 + 1, arg3);
                }
            };

    private void showDate(int year, int month, int day) {
        //servSchedule.setText(new StringBuilder().append(day).append("/").append(month).append("/").append(year));
        servSchedule.setText(new StringBuilder().append(year).append("-").append(month).append("-").append(day));
    }


    private void runPost(String facName, String facAddress, String contactName, String phone, String email, String schedule, String serviceFacility, String reqService, String facilitySize, String serviceProviders, String cleanTypeChoice, String serviceAmount) {

        String url_req = "bookService.json";


        // Instantiate Http Request Param Object
        RequestParams params = new RequestParams();
        // When Name Edit View, Email Edit View and Password Edit View have values other than Null

        // Put Http parameter username with value of Email Edit View control
        params.put("facilityName", facName);
        params.put("facilityAddress", facAddress);
        params.put("contactPerson", contactName);
        params.put("serviceSchedule", schedule);
        params.put("email", email);
        params.put("phone", phone);
        params.put("cleanType", cleanTypeChoice);
        params.put("serviceFacility", serviceFacility);
        params.put("reqService", reqService);
        params.put("serviceAmount", serviceAmount);
        params.put("facilitySize", facilitySize);
        params.put("serviceProviders", serviceProviders);

        Log.e("SEEE", params.toString());

        PostConnection.POST(params, url_req, new JsonHttpResponseHandler() {

            @Override
            public void onStart() {
                // called before request is started

                Toast.makeText(getApplicationContext(), "Sending", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //super.onSuccess(statusCode, headers, response);
                if (statusCode == 200) {

                    Toast.makeText(getApplication(), "OK", Toast.LENGTH_LONG).show();

                    String b_serviceFacility = null, b_reqService = null, b_serviceAmount = null, b_facilitySize = null, b_serviceProviders = null;
                    String b_phone = null, b_email = null, b_person = null, b_fName = null, b_fAddress = null, b_schedule = null, b_serviceId = null, b_payAdvise = null;
                    //String user_igr = null, user_mda = null, user_id = null, user_role = null, user_access = null;
                    //String yesterday_collection = "0", thismonth_collection = "0", today_collection = "0", lastmonth_collection = "0";
                    try {
                        b_serviceFacility = response.getString("serviceFacility");
                        b_reqService = response.getString("reqService");
                        b_serviceAmount = response.getString("serviceAmount");
                        b_facilitySize = response.getString("facilitySize");
                        b_phone = response.getString("phone");
                        b_email = response.getString("email");
                        b_person = response.getString("contact");
                        b_fName = response.getString("facilityName");
                        b_fAddress = response.getString("facilityAddress");
                        b_schedule = response.getString("serviceSchedule");
                        b_serviceId = response.getString("serviceBooking");
                        b_payAdvise = response.getString("payAdvise");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    //Toast.makeText(getApplicationContext(), "OK "+lastmonth_collection+"...."+mda_name, Toast.LENGTH_LONG).show();


                    Intent myIntent = new Intent(Clean2Activity.this, BookingActivity.class);
                    myIntent.putExtra("serviceFacility", b_serviceFacility);
                    myIntent.putExtra("reqService", b_reqService);
                    myIntent.putExtra("serviceAmount", b_serviceAmount);
                    myIntent.putExtra("facilitySize", b_facilitySize);
                    myIntent.putExtra("phone", b_phone);
                    myIntent.putExtra("email", b_email);
                    myIntent.putExtra("fLocation", b_fAddress);
                    myIntent.putExtra("Property", b_fName);
                    myIntent.putExtra("paymentAdvise", b_payAdvise);
                    myIntent.putExtra("serviceBookId", b_serviceId);
                    myIntent.putExtra("serviceScheduleDate", b_schedule);
                    myIntent.putExtra("serviceContact", b_person);

                    Clean2Activity.this.startActivity(myIntent);

                }
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)

                try {
                    if (statusCode == 404) {
                        Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                    }
                    // When Http response code is '500'
                    else if (statusCode == 500) {
                        Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                    }
                    // When Http response code other than 404, 500
                    else {
                        Toast.makeText(getApplicationContext(), responseString, Toast.LENGTH_LONG).show();
                    }
                } catch (Throwable throwable1) {
                    throwable.printStackTrace();
                }


            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }

        });
    }

}