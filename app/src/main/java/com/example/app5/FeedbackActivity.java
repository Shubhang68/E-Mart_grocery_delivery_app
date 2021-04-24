package com.example.app5;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class FeedbackActivity extends AppCompatActivity {

    EditText feedback;
    Button cancel,submit;
    FirebaseAuth fAuth;
    FirebaseDatabase db;

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        cancel = findViewById(R.id.cancelButton);
        feedback = findViewById(R.id.feedbackView);
        submit = findViewById(R.id.submitButton);

        fAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();

        submit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(feedback.getText().toString().trim().isEmpty()){
                    Toast.makeText(getApplicationContext(),"Please enter something",Toast.LENGTH_SHORT).show();
                }else{
                    String num = String.valueOf(new Random().nextInt(100000));
                    String entry = feedback.getText().toString();
                    String uID = fAuth.getCurrentUser().getUid() + num;
                    Map<String, Object> map = new HashMap<>();
                    map.clear();
                    map.put(uID, entry);
                    db.getReference("Feedback").updateChildren(map);
                    String message = "We have received your feedback and we'll start working on it as soon as possible. Thank you.\n\nTeam E-MART";
                    SmsManager sms = SmsManager.getDefault();
                    sms.sendTextMessage( "+912300523005",null,message,null,null);
                    Toast.makeText(getApplicationContext(), "Feedback Submitted", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}