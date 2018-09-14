package com.example.hp.cleaners;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

import static java.lang.Boolean.FALSE;
import static java.lang.Integer.parseInt;

public class MainActivity extends AppCompatActivity {

    private long lastClickTime = 0;
    private static final int REQUEST_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*if(getIntent().getBooleanExtra("EXIT", false)){
            finish();
        }*/
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);




        final Spinner mySpinner = findViewById(R.id.facilitySelect);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> spinnerArrayAdapter = ArrayAdapter.createFromResource(this,
                R.array.facilities_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        mySpinner.setAdapter(spinnerArrayAdapter);





        final Spinner unitSpinner = findViewById(R.id.unitSelect);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> unitSpinnerArrayAdapter = ArrayAdapter.createFromResource(this,
                R.array.units_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        unitSpinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        unitSpinner.setAdapter(unitSpinnerArrayAdapter);




        //...


        Button sendButton = findViewById(R.id.sendButton);

        sendButton.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v){

                        if (checkConnection() == null) {
                            Toast.makeText(getApplicationContext(), "Please Check Data Connection", Toast.LENGTH_LONG).show();
                            return;
                        };
                        final String unitMeasure = unitSpinner.getSelectedItem().toString();
                        final String serviceFac = mySpinner.getSelectedItem().toString();

                        EditText facilitySize = findViewById(R.id.facilitySize);
                        final String facSize = facilitySize.getText().toString();

                        int selectedId = 0;

                        RadioGroup cleanSer = findViewById(R.id.clean_pest);
                        //EditText userPassword = (EditText)findViewById(R.id.userPassword);
                        selectedId = cleanSer.getCheckedRadioButtonId();



                        // find the radiobutton by returned id
                        RadioButton cleanServ = findViewById(selectedId);
                        final String cleanServe = cleanServ.getText().toString();



                        if(selectedId == 0 || facSize == null || facSize.length() == 0 || serviceFac == null
                                || serviceFac.length() == 0 || unitMeasure == null || unitMeasure.length() == 0) {
                            Toast.makeText(getApplicationContext(), "field missing", Toast.LENGTH_LONG).show();
                            return;
                        }

                        if((parseInt(facSize) < 100 && unitMeasure.equals("Square Meters (sqm)")) || (parseInt(facSize) < 1 && unitMeasure.equals("Rooms/Office"))) {
                            Toast.makeText(getApplicationContext(), "Invalid Size", Toast.LENGTH_LONG).show();
                            return;
                        }

                        if((parseInt(facSize) > 500 && unitMeasure.equals("Square Meters (sqm)")) || (parseInt(facSize) > 4 && unitMeasure.equals("Rooms/Office"))) {

//                            Toast.makeText(getApplicationContext(), "Large Size", Toast.LENGTH_LONG).show();

                            Intent myIntent = new Intent(MainActivity.this, Clean2Activity.class);
                            myIntent.putExtra("serviceFacility", serviceFac);
                            myIntent.putExtra("reqService", cleanServe);
                            myIntent.putExtra("serviceAmount", "0");
                            myIntent.putExtra("facilitySize", facSize+" "+unitMeasure);
                            myIntent.putExtra("serviceProviders", unitMeasure);
                            //myIntent.putExtra("serviceProvidersL", response_provider);

                            MainActivity.this.startActivity(myIntent);
                            return;
                        }



                        if(!(cleanServe.equals("Cleaning Service"))) {

//                        Toast.makeText(getApplicationContext(), "Large Size", Toast.LENGTH_LONG).show();

                        Intent myIntent = new Intent(MainActivity.this, PestActivity.class);
                        myIntent.putExtra("serviceFacility", serviceFac);
                        myIntent.putExtra("reqService", cleanServe);
                        myIntent.putExtra("serviceAmount", 0);
                        myIntent.putExtra("facilitySize", facSize+" "+unitMeasure);
                        myIntent.putExtra("serviceProviders", unitMeasure);
                        //myIntent.putExtra("serviceProvidersL", response_provider);

                        MainActivity.this.startActivity(myIntent);
                        return;
                    }

                        // preventing double, using threshold of 10000 ms
                        if (SystemClock.elapsedRealtime() - lastClickTime < 10000){
                            return;
                        }

                        lastClickTime = SystemClock.elapsedRealtime();

                        runPost(facSize, cleanServe, serviceFac, unitMeasure);
                    }


                    private NetworkInfo checkConnection() {

                        ConnectivityManager conMgr =  (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
                        return netInfo;
                    }
                }
        );
    }

    private void runPost(String facSize, String cleanServe, String serviceFac, String unitMeasure){

        String url_req = "cleanService.json";


        // Instantiate Http Request Param Object
        RequestParams params = new RequestParams();
        // When Name Edit View, Email Edit View and Password Edit View have values other than Null

        // Put Http parameter username with value of Email Edit View control
        params.put("facilitySize", facSize);
        params.put("facility", serviceFac);
        params.put("unitMeasure", unitMeasure);
        params.put("requestService", cleanServe);

        //Log.e("TAG", params.toString());
        // Invoke RESTful Web Service with Http parameters
        //call_POST(params, url_req, email, password);
        PostConnection.POST(params, url_req, new JsonHttpResponseHandler(){

            @Override
            public void onStart() {
                // called before request is started

                Toast.makeText(getApplicationContext(), "Sending", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                //super.onSuccess(statusCode, headers, response);
                if(statusCode == 200) {

                    Toast.makeText(getApplication(), "OK", Toast.LENGTH_LONG).show();

                    String serviceFacility = null;
                    String reqService = null;
                    String serviceAmount = null;
                    String facilitySize = null;
                    int servSize = 0;

                    try {
                        serviceFacility = response.getJSONObject(0).getString("serviceFacility");
                        reqService = response.getJSONObject(0).getString("reqService");
                        serviceAmount = response.getJSONObject(0).getString("serviceAmount");
                        facilitySize = response.getJSONObject(0).getString("facilitySize");
                        servSize = response.getJSONObject(0).getInt("servSize");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    //Toast.makeText(getApplicationContext(), "OK "+lastmonth_collection+"...."+mda_name, Toast.LENGTH_LONG).show();

                    if (servSize > 0) {

                        JSONObject serviceProviders = null;
                        try {
                            serviceProviders = response.getJSONObject(0).getJSONObject("serviceProviders");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        ArrayList<String> list_response = new ArrayList<>();
                        String[] list_re = new String[]{};
                        String response_provider = null;
                        for (int i = 1; i <= servSize; i++) {
                                //i = i +1;
                                String j = Integer.toString(i);

                            //response_object = serviceProviders.getJSONObject("1");
                            try {
                                response_provider = serviceProviders.getString(j);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                            //list_response.add(response_name+" ("+response_ncode+")  \n₦" +response_amount);
                            list_response.add(response_provider);
                        }



                        //Toast.makeText(getApplicationContext(), response_provider, Toast.LENGTH_LONG).show();

                        Intent myIntent = new Intent(MainActivity.this, PestActivity.class);
                        myIntent.putExtra("serviceFacility", serviceFacility);
                        myIntent.putExtra("reqService", reqService);
                        myIntent.putExtra("serviceAmount", serviceAmount);
                        myIntent.putExtra("facilitySize", facilitySize);
                        myIntent.putStringArrayListExtra("serviceProviders", list_response);
                        //myIntent.putExtra("serviceProvidersL", response_provider);

                        MainActivity.this.startActivity(myIntent);

                    } else {
                        //doMessage(serviceAmount);

                        //String serviceProviders = null;
                        /*
                        try {
                            serviceProviders = response.getString("serviceProviders");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        */

                        String serviceProviders = null;
                        try {
                            serviceProviders = response.getJSONObject(0).getString("serviceProviders");
                            //servSize = response.getJSONObject(0).getInt("servSize");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(
                                MainActivity.this);

                        final String finalServiceFacility = serviceFacility;
                        final String finalReqService = reqService;
                        final String finalServiceAmount = serviceAmount;
                        final String finalFacilitySize = facilitySize;
                        final String finalServiceProviders = serviceProviders;
                        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //finish();
                                //System.exit(0);
                                //goBack();
                                Intent myIntent = new Intent(MainActivity.this, CleanActivity.class);
                                myIntent.putExtra("serviceFacility", finalServiceFacility);
                                myIntent.putExtra("reqService", finalReqService);
                                myIntent.putExtra("serviceAmount", finalServiceAmount);
                                myIntent.putExtra("facilitySize", finalFacilitySize);
                                myIntent.putExtra("serviceProviders", finalServiceProviders);

                                MainActivity.this.startActivity(myIntent);
                            }
                        });

                        alertDialog.setNegativeButton("Cancel", null);

                        alertDialog.setMessage(String.format("%1$-20s",serviceAmount).replace(",","\n") +" click Ok to continue");
                        alertDialog.setTitle(" ");
                        alertDialog.show();}
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
                } catch (Throwable throwable1){
                    throwable.printStackTrace();
                }


            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }

        });
    }

    /*
    private void doMessage(String messageVal) {

        android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(
                MainActivity.this);

        alertDialog.setPositiveButton("This service will cost "+ messageVal +" click Ok to continue", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                //finish();
                //System.exit(0);
                //goBack();
                return;
            }
        });

        alertDialog.setNegativeButton("No", null);

        alertDialog.setMessage("Do you want to exit?");
        alertDialog.setTitle("AppTitle");
        alertDialog.show();
    }


    private void setUpActionBar() {
        // Make sure we're running on Honeycomb or higher to use ActionBar APIs
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            ActionBar actionBar = getActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
    */
}
