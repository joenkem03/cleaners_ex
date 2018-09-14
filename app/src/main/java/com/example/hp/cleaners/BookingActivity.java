package com.example.hp.cleaners;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.NumberFormat;

public class BookingActivity extends AppCompatActivity {

    public  TextView proptyTxt, addressTxt, phoneTxt, proptyTypeTxt, scheduleTxt, amntTxt, bookIdTxt, contactTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);


//        String.format("%1$-15s",getString(R.string.phoneLabel)).replace("\t","  ");

        proptyTxt = findViewById(R.id.propertyLabel);
        proptyTxt.setText(String.format("%1$-15s",getString(R.string.propertyLabel)).replace("\t","  "));

        phoneTxt = findViewById(R.id.phoneLabel);
        phoneTxt.setText(String.format("%1$-15s",getString(R.string.phoneLabel)).replace("\t","  "));

        addressTxt = findViewById(R.id.addressLabel);
        addressTxt.setText(String.format("%1$-15s",getString(R.string.addressLabel)).replace("\t","  "));

        proptyTypeTxt = findViewById(R.id.propertyTypeLabel);
        proptyTypeTxt.setText(String.format("%1$-15s",getString(R.string.propertyTypeLabel)).replace("\t","  "));

        scheduleTxt = findViewById(R.id.scheduleLabel);
        scheduleTxt.setText(String.format("%1$-15s",getString(R.string.scheduleLabel)).replace("\t","  "));

        amntTxt = findViewById(R.id.amountLabel);
        amntTxt.setText(String.format("%1$-15s",getString(R.string.amountLabel)).replace("\t","  "));

        bookIdTxt = findViewById(R.id.bookIdLabel);
        bookIdTxt.setText(String.format("%1$-15s",getString(R.string.bookIdLabel)).replace("\t","  "));

        contactTxt = findViewById(R.id.contactLabel);
        contactTxt.setText(String.format("%1$-15s",getString(R.string.contactLabel)).replace("\t","  "));




        final String serviceFacility = getIntent().getStringExtra("serviceFacility");
        TextView serviceFac = findViewById(R.id.propertyType);
        serviceFac.setText(serviceFacility);

        final String reqService = getIntent().getStringExtra("reqService");
        TextView reqServ = findViewById(R.id.bookingStatus);
        reqServ.setText("Thank you! Your request for our "+reqService+" has been successfully scheduled, details as follows.");

        final String serviceAmount = getIntent().getStringExtra("serviceAmount");
        TextView Amount = findViewById(R.id.amountName);
        if (serviceAmount.equals("0")){
            Amount.setText("Quotation Request");
        } else {

            NumberFormat formatAmount;
            formatAmount = NumberFormat.getCurrencyInstance();
            String ss2 = serviceAmount.replaceAll("\\..*", "");
            Double intt1 = Double.valueOf(ss2);
            String printAmount = formatAmount.format(intt1);
            printAmount = String.format(printAmount).replace("$","₦");
            Amount.setText(printAmount);
        }

        //final String facilitySize = getIntent().getStringExtra("facilitySize");
        //TextView facSize = findViewById(R.id.)

        //final String serviceProviders = getIntent().getStringExtra("serviceProviders");

        final String phoneNumber = getIntent().getStringExtra("phone");
        TextView phoneNo = findViewById(R.id.phoneNo);
        phoneNo.setText(phoneNumber);

        final String namePerson = getIntent().getStringExtra("serviceContact");
        TextView cName = findViewById(R.id.contactName);
        cName.setText(namePerson);

        final String scheduleDate = getIntent().getStringExtra("serviceScheduleDate");
        TextView dateSchedule = findViewById(R.id.scheduleName);
        dateSchedule.setText(scheduleDate);

        final String facilityName = getIntent().getStringExtra("Property");
        TextView facName = findViewById(R.id.propertyName);
        facName.setText(facilityName);

        final String serviceBookId = getIntent().getStringExtra("serviceBookId");
        final TextView bookingRef = findViewById(R.id.bookIdName);
        bookingRef.setText(serviceBookId);

        final String facilityAddress = getIntent().getStringExtra("fLocation");
        TextView facLocation = findViewById(R.id.addressName);
        facLocation.setText(facilityAddress);

        final String pay_advise = getIntent().getStringExtra("paymentAdvise");
        TextView paymentAdvise = findViewById(R.id.payAdvise);
        paymentAdvise.setText(pay_advise);
//        final String payerEmail = getIntent().getStringExtra("email");

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

        Button payButton = findViewById(R.id.confirmButton);
        if(serviceAmount.equals("0") ){
            payButton.setVisibility(View.INVISIBLE);
        }
        payButton.setOnClickListener(new android.view.View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                doExit();
                Intent makePay = new Intent(BookingActivity.this, PayActivity.class);
                makePay.putExtra("bookingRef", serviceBookId);
                makePay.putExtra("amount", serviceAmount);
                makePay.putExtra("phone", phoneNumber);
                makePay.putExtra("email", getIntent().getStringExtra("email"));
                makePay.putExtra("payerName", namePerson);
                BookingActivity.this.startActivity(makePay);
            }
        });
//
//        Button exitButton = findViewById(R.id.exitButton);
//        if(reqService.equals("Pest Control Service")){
//            exitButton.setVisibility(View.INVISIBLE);
//        }
//        exitButton.setOnClickListener(new android.view.View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                doExit();
//            }
//        });

    }


    @Override
    public void onBackPressed() {

        doExit();
    }

    /**
     * Exit the app if user select yes.
     */
    private void doExit() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                BookingActivity.this);

        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                //finish();
                //System.exit(0);
                goBack();
            }
        });

        alertDialog.setNegativeButton("No", null);

        alertDialog.setMessage("Do you want to exit?");
        alertDialog.setTitle(" ");
        alertDialog.show();
    }


    //signout
    public void goBack(){
        Intent landscreen= new Intent(this, MainActivity.class);
        landscreen.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        this.startActivity(landscreen);
        this.finish();
    }

}
