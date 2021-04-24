package com.example.app5;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CancelOrder extends AppCompatActivity {

    TextView orderText;
    String file,uID;
    int del = 0;
    FirebaseDatabase database,db,db1,db2;
    FirebaseAuth fAuth;
    String start,end,items="",status = "Status : ",t;
    Button back,cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancel_order);

        orderText = findViewById(R.id.listTextView);
        database = FirebaseDatabase.getInstance();
        db = FirebaseDatabase.getInstance();
        db1 = FirebaseDatabase.getInstance();
        db2 = FirebaseDatabase.getInstance();
        fAuth= FirebaseAuth.getInstance();

        back = findViewById(R.id.backButton);
        cancel = findViewById(R.id.cancelButton);

        uID = fAuth.getCurrentUser().getUid();

        Map<String,Object> map = new HashMap<String,Object>();

        ArrayList<String> item = new ArrayList<String>();
        ArrayList<Integer> qty = new ArrayList<Integer>();
        ArrayList<Integer> dbq = new ArrayList<Integer>();

        item.clear();
        qty.clear();

        database.getReference("Latest_Order").addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                file = snapshot.child(uID).getValue(String.class);

                String[] order = file.substring(1,file.length()-1).split(",");
                for(int i = 0;i<order.length; i++){
                    del++;
                    if(order[i].contains("Total")){
                        break;
                    }
                }

                if(file.toLowerCase().contains("online")) {
                    end = order[order.length - 1].split(":")[1].trim();
                    //start = order[order.length - 2].split(":")[1].trim();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    SimpleDateFormat tdf = new SimpleDateFormat("hh aa");
                    Date date = new Date();
                    Date time = new Date();
                    start = sdf.format(date);
                    t = tdf.format(time);

                    if(start.equals(end)){
                        if(Integer.parseInt(t.split(" ")[0]) >=3 && t.split(" ")[1].contains("PM")){
                            status = status + "Order delivered";
                        }else{
                            status = status + "Order in transit";
                        }
                    }else{
                        if(Integer.parseInt(t.split(" ")[0]) >=9 && t.split(" ")[1].contains("PM")){
                            status = status + "Order dispatched";
                        }else{
                            status = status + "Order placed";
                        }
                    }
                    String data = file.substring(1,file.length()-1).replaceAll(", ","\n") + "\n" + status + "\nDelivery Executive : Sample Name";
                    orderText.setText(data);
                }else{
                    String data = file.substring(1,file.length()-1).replaceAll(",","\n");
                    orderText.setText(data);
                }

                for(int i = 0;i<del; i++){
                    items = items + "\n" + order[i].trim();
                }




                items = items.trim();

                String[] sp = items.split("\n");

                for(int i = 0;i<sp.length-1;i++) {
                    String a = sp[i].split("=")[0].trim();
                    Integer b = Integer.parseInt(sp[i].split("=")[1].split("x")[0].trim());
                    item.add(a);
                    qty.add(b);
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



       db.getReference("List").addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dbq.clear();

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    if(item.contains(snapshot.getKey())){
                        dbq.add(snapshot.getValue(Integer.class));
                    }
                }

                for(int i = 0;i<item.size();i++){
                    map.put(item.get(i),dbq.get(i)+qty.get(i));
                }

                cancel.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        db2.getReference().child("List").updateChildren(map);
                        Toast.makeText(getApplicationContext(),"Order Cancelled",Toast.LENGTH_SHORT).show();
                        db1.getReference().child("Latest_Order").child(uID).removeValue();
                        finish();
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}