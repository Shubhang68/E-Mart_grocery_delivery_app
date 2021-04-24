package com.example.app5;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class DeliveryActivity extends AppCompatActivity {

    Button offline,online,proceed;
    TextView dateSet,storeText;
    FirebaseDatabase db1,db2,db3,db4,db5;
    FirebaseAuth fAuth;
    DatePickerDialog.OnDateSetListener datePicker;
    String datePicked;
    Random rand = new Random();
    String select;
    Dialog dialog;
    ListView list;

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery);

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.shop);
        list = dialog.findViewById(R.id.shopList);

        Bundle bundle = getIntent().getExtras();
        ArrayList<String>order = bundle.getStringArrayList("order");

        ArrayList<String> category = new ArrayList<String>();
        ArrayList<String> text = new ArrayList<String>();

        ArrayList<String> shops = new ArrayList<String>();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,R.layout.list_item,shops);
        ArrayList<Integer> coords = new ArrayList<>();
        coords.clear();
        list.setAdapter(adapter);

        db1 = FirebaseDatabase.getInstance();
        db2 = FirebaseDatabase.getInstance();
        db3 = FirebaseDatabase.getInstance();
        db4 = FirebaseDatabase.getInstance();
        db5 = FirebaseDatabase.getInstance();
        fAuth = FirebaseAuth.getInstance();

        online = findViewById(R.id.onlineButton);
        offline = findViewById(R.id.offlineButton);
        proceed = findViewById(R.id.proceedButton);

        dateSet = findViewById(R.id.dateTextView);
        storeText = findViewById(R.id.storeTextView);

        String id = String.valueOf(rand.nextInt(10000));
        String uID = fAuth.getCurrentUser().getUid();

        db5.getReference("Users").child(uID).addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String[] textData = snapshot.child("address").getValue(String.class).split(",");
                coords.add(Integer.parseInt(textData[textData.length-2].trim()));
                coords.add(Integer.parseInt(textData[textData.length-1].trim()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        online.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                offline.setClickable(false);
                storeText.setVisibility(View.VISIBLE);
                dateSet.setVisibility(View.INVISIBLE);
                order.add("Mode of delivery : Online");
                text.add("Mode of delivery : Online");
                Calendar cal = Calendar.getInstance();

                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                order.add("Order date : " + String.valueOf(day) + "/" + String.valueOf(month + 1) + "/" + String.valueOf(year));
                text.add("Order date : " + String.valueOf(day) + "/" + String.valueOf(month + 1) + "/" + String.valueOf(year));
                order.add("Delivery date : " + String.valueOf(day+1) + "/" + String.valueOf(month + 1) + "/" + String.valueOf(year));
                text.add("Delivery date : " + String.valueOf(day+1) + "/" + String.valueOf(month + 1) + "/" + String.valueOf(year));
                proceed.setVisibility(View.VISIBLE);
            }
        });

        offline.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                storeText.setVisibility(View.VISIBLE);
                order.add("Mode of delivery : Offline");
                text.add("Mode of delivery : Offline");
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                order.add("Order date : " + String.valueOf(day) + "/" + String.valueOf(month + 1) + "/" + String.valueOf(year));
                text.add("Order date : " + String.valueOf(day) + "/" + String.valueOf(month + 1) + "/" + String.valueOf(year));
            }
        });

        db1.getReference("Users").child(uID).addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                category.clear();
                category.add(snapshot.child("category").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        storeText.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (category.get(0).equals("Customer")) {
                    select = "R";
                } else {
                    select = "W";
                }
                db2.getReference(select).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Integer min = 10000000;
                        int count = 0;
                        int pos = 0;
                        shops.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String company = snapshot.getKey() + ", " + snapshot.getValue(String.class);
                            shops.add(company);
                            String[] coor = snapshot.getValue(String.class).split(",");
                            Integer lat = Integer.parseInt(coor[coor.length-2].trim());
                            Integer lon = Integer.parseInt(coor[coor.length-1].trim());
                            Integer dis = ((coords.get(0)-lat)*(coords.get(0)-lat) + (coords.get(1)-lon)*(coords.get(1)-lon));
                            if(dis < min){
                                pos = count;
                                min = dis;
                                shops.remove(pos);
                                shops.add(0,company);
                            }


                            count = count + 1;
                        }

                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                dialog.show();

                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        dialog.cancel();
                        String storeName = "Pickup Point : " + shops.get(i).split(",")[0].trim();
                        storeText.setText(storeName);
                        order.add(storeName);
                        text.add(storeName);
                        if(order.toString().toLowerCase().contains("offline")){
                            dateSet.setVisibility(View.VISIBLE);
                        }

                    }
                });
            }
        });

        dateSet.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(
                        DeliveryActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        datePicker,year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        datePicker = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                datePicked = String.valueOf(day) + "/" + String.valueOf(month+1) + "/" + String.valueOf(year);
                Intent intent = new Intent(Intent.ACTION_INSERT);
                intent.setData(CalendarContract.Events.CONTENT_URI);
                intent.putExtra(CalendarContract.Events.TITLE,"Pick up E-MART order");
                intent.putExtra(CalendarContract.Events.EVENT_LOCATION,order.get(order.size()-1).split(":"));
                intent.putExtra(CalendarContract.Events.ALL_DAY,"true");
                intent.putExtra(CalendarContract.Events.LAST_DATE,datePicked);

                if(intent.resolveActivity(getPackageManager()) != null){
                    startActivity(intent);
                }else{
                    Toast.makeText(DeliveryActivity.this, "There is no app that support this action", Toast.LENGTH_SHORT).show();
                }
                if(intent.resolveActivity(getPackageManager()) != null){
                    startActivity(intent);
                }else{
                    Toast.makeText(DeliveryActivity.this, "There is no app that support this action", Toast.LENGTH_SHORT).show();
                }


                dateSet.setClickable(false);
                offline.setClickable(false);
                online.setClickable(false);
                proceed.setVisibility(View.VISIBLE);
                dateSet.setText("Pickup Date : " + datePicked);
                order.add("Pickup Date : " + datePicked);
                text.add("Pickup Date : " + datePicked);
            }
        };

        proceed.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String oID = uID + id;
                String oString = order.toString();
                String message = "Order ID : " + oID + "\n" + text.toString().substring(1,text.toString().length()-1).replaceAll(",","\n") + "\n\nTeam E-MART";
                SmsManager sms = SmsManager.getDefault();
                sms.sendTextMessage( "+912300523005",null,message,null,null);
                Map<String,Object> orderMap = new HashMap<String,Object>();
                orderMap.put(oID,oString);
                db3.getReference("Orders").child(category.get(0)).updateChildren(orderMap);
                Map<String,Object> lastOrder = new HashMap<String,Object>();
                lastOrder.put(uID,oString);
                db4.getReference("Latest_Order").updateChildren(lastOrder);
                startActivity(new Intent(getApplicationContext(),ResetActivity.class));
            }
        });
    }
}