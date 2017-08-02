package com.example.master.temprovideocrfinal2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class RegistrationActivity extends AppCompatActivity {

    TextView regText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        regText=(TextView)findViewById(R.id.regText);

        //LicenceDetail licenceDetail = (LicenceDetail)getIntent().getSerializableExtra();
        //regText.setText(licenceDetail.getLicense_id()+licenceDetail.getExpiry_date());
        //Toas

        Intent intent = this.getIntent();
        //Bundle bundle = intent.getExtras();
        //LicenceDetail licenceDetail =(LicenceDetail)bundle.getSerializable("LicenceDetail");

        String licenceNumber=intent.getStringExtra("licenceNumber");
        String expiryDate =intent.getStringExtra("expiryDate");



        regText.setText("Liscence Number: "+licenceNumber+"\nExpiry Date: "+expiryDate);
    }
}
