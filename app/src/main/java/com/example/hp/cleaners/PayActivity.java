package com.example.hp.cleaners;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.Date;

import co.paystack.android.Paystack;
import co.paystack.android.PaystackSdk;
import co.paystack.android.Transaction;
import co.paystack.android.model.Card;
import co.paystack.android.model.Charge;
import cz.msebera.android.httpclient.Header;

import static co.paystack.android.model.Charge.Bearer.account;


public class PayActivity extends AppCompatActivity {

    private String publicKey, cardNumber, cvv, tnxStatus, status_msg;
    private int expiryMonth = 0, expiryYear = 0;
    private EditText cardNumberText, cvvText,monthText, yearText;
    private Button payButton;
    private long lastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PaystackSdk.initialize(getApplicationContext());
        publicKey = "pk_test_d03c7ff7e952fec979386dff38e44685d17ab9f3";
        PaystackSdk.setPublicKey(publicKey);
        setContentView(R.layout.activity_pay);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        cardNumberText = findViewById(R.id.edit_card_number);
        cvvText = findViewById(R.id.edit_cvc);
        monthText = findViewById(R.id.edit_expiry_month);
        yearText = findViewById(R.id.edit_expiry_year);

        cardNumberText.addTextChangedListener((TextWatcher) new FourDigitCardFormatWatcher());




        payButton = findViewById(R.id.button_perform_local_transaction);
        payButton.setOnClickListener(new android.view.View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                doExit();
//                Intent makePay = new Intent(BookingActivity.this, PayActivity.class);
//                BookingActivity.this.startActivity(makePay);



                // This sets up the card and check for validity
                // This is a test card from paystack
//                cardNumber = "4084084084084081";
//                expiryMonth = 11; //any month in the future
//                expiryYear = 18; // any year in the future. '2018' would work also!
//                cvv = "408";  // cvv of the test card

                cardNumber = cardNumberText.getText().toString();
                cvv = cvvText.getText().toString();

                if(cardNumber == null || cardNumber.length() == 0 || cvv == null || cvv.length() == 0
                        || yearText.getText().toString().length() == 0 || monthText.getText().toString().length() == 0) {
                    Toast.makeText(getApplicationContext(), "field missing", Toast.LENGTH_LONG).show();
                    return;
                }

                expiryMonth = Integer.parseInt(monthText.getText().toString());
                expiryYear = Integer.parseInt(yearText.getText().toString());

                Card card = new Card(cardNumber, expiryMonth, expiryYear, cvv);
                if (card.isValid()) {


                    // preventing double, using threshold of 10000 ms
                    if (SystemClock.elapsedRealtime() - lastClickTime < 10000){
                        return;
                    }

                    lastClickTime = SystemClock.elapsedRealtime();
                    // charge card
                    performCharge(card);
                } else {
                    //do something
                    //Log.d("TAG", "e no work");


                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                            PayActivity.this);
                    alertDialog.setPositiveButton("Ok", null);

                    //alertDialog.setNegativeButton("No", null);

                    alertDialog.setMessage("Please input correct card details");
                    alertDialog.setTitle("Invalid Card Details");
                    alertDialog.show();
                }
            }
        });

    }


    // This is the subroutine you will call after creating the charge
    // adding a card and setting the access_code
    public void performCharge(Card card){

        final int koboAmount = Integer.parseInt(getIntent().getStringExtra("amount")) * 100;
        final String tnxRef = getIntent().getStringExtra("bookingRef");
        final String email = getIntent().getStringExtra("email");
        final String userPhone = getIntent().getStringExtra("phone");

        //create a Charge object
        Charge charge = new Charge();
        charge.setCard(card); //sets the card to charge
        charge.setCurrency("NGN");
        charge.setAmount(koboAmount);

//        charge.setPlan();
        charge.setSubaccount("");
        charge.setTransactionCharge(10);
        charge.setEmail(email);
        charge.setReference(tnxRef);
        charge.setBearer(account);
//        charge.putMetadata("metaData", "sxjxxjj")
        try {
            charge.putCustomField("Phone", userPhone);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        PaystackSdk.chargeCard(PayActivity.this, charge, new Paystack.TransactionCallback() {
            @Override
            public void onSuccess(Transaction transaction) {
                // This is called only after transaction is deemed successful.
                // Retrieve the transaction, and send its reference to your server
                // for verification.
//                Log.d("SUCCESS", transaction.toString());

                /* Post to Server*/
                //
                status_msg = "Ok";

                tnxStatus = "SUCCESSFUL";
                runPost(transaction, koboAmount, tnxRef, email, tnxStatus, status_msg, userPhone);


                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                            PayActivity.this);

                    alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //finish();
                            //System.exit(0);
                            goBack();
                        }
                    });

                    //alertDialog.setNegativeButton("No", null);

                    alertDialog.setMessage("Transaction was successful");
                    alertDialog.setTitle("SUCCESSFUL TRANSACTION");
                    alertDialog.show();

            }

            @Override
            public void beforeValidate(Transaction transaction) {
                // This is called only before requesting OTP.
                // Save reference so you may send to server. If
                // error occurs with OTP, you should still verify on server.
            }

            @Override
            public void onError(Throwable error, Transaction transaction) {
                //handle error here
//                Log.d("ERROR", transaction.toString());
                status_msg = String.valueOf(error);

                tnxStatus = "FAILED";
                runPost(transaction, koboAmount, tnxRef, email, tnxStatus, status_msg, userPhone);

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                        PayActivity.this);

                alertDialog.setPositiveButton("Exit", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //finish();
                        //System.exit(0);
                        goBack();
                    }
                });

                alertDialog.setNegativeButton("Retry", null);

                alertDialog.setMessage("Transaction Failed");
                alertDialog.setTitle("TRANSACTION FAILED");
                alertDialog.show();
            }

        });
    }

    private void runPost(Transaction transaction, int koboAmount, String tnxRef, String email, String tnxStatus, String status_msg, String userPhone) {


        Date date = new Date();
        String tnxDateVal= new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a").format(date);

        String url_req = "notification.json";
        RequestParams params = new RequestParams();
        params.put("transactionId", transaction);
        params.put("bookingRef", tnxRef);
        params.put("amount", koboAmount);
        params.put("email", email);
        params.put("Phone", userPhone);
        params.put("status_remark", status_msg);
        params.put("tnxStatus", tnxStatus);
        params.put("transactionDate", tnxDateVal);

        PostConnection.POST(params, url_req, new JsonHttpResponseHandler(){

//            @Override
//            public void onStart() {
//                super.onStart();
//            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                return;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                return;
            }
        });
    }


    //signout
    public void goBack(){
        Intent landscreen= new Intent(this, MainActivity.class);
        landscreen.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        this.startActivity(landscreen);
        this.finish();
    }

    /**
     * Formats the watched EditText to a credit card number
     */
    public static class FourDigitCardFormatWatcher implements TextWatcher {

        // Change this to what you want... ' ', '-' etc..
        private static final char space = ' ';

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }


        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            // Remove spacing char
            if (s.length() > 0 && (s.length() % 5) == 0) {
                final char c = s.charAt(s.length() - 1);
                if (space == c) {
                    s.delete(s.length() - 1, s.length());
                }
            }
            // Insert char where needed.
            if (s.length() > 0 && (s.length() % 5) == 0) {
                char c = s.charAt(s.length() - 1);
                // Only if its a digit where there should be a space we insert a space
                if (Character.isDigit(c) && TextUtils.split(s.toString(), String.valueOf(space)).length <= 3) {
                    s.insert(s.length() - 1, String.valueOf(space));
                }
            }
        }
    }



}
