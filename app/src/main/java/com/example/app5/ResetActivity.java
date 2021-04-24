package com.example.app5;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ResetActivity extends AppCompatActivity {

    FirebaseDatabase db,db1,db2;
    FirebaseAuth fAuth;
    Button button;

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);

        db = FirebaseDatabase.getInstance();
        db1 = FirebaseDatabase.getInstance();
        db2 = FirebaseDatabase.getInstance();
        fAuth = FirebaseAuth.getInstance();
        button = findViewById(R.id.button);

        String uID = fAuth.getCurrentUser().getUid();

        Map<String, Object> map = new HashMap<>();

        ArrayList<String> items = new ArrayList<String>();
        ArrayList<Integer> qty = new ArrayList<Integer>();
        ArrayList<Integer> dbq = new ArrayList<Integer>();

        db1.getReference("Cart"+uID).addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                items.clear();
                qty.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    if(snapshot.getValue(Integer.class)>0){
                        items.add(snapshot.getKey());
                        qty.add(snapshot.getValue(Integer.class));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        db2.getReference("List").addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dbq.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    if(items.contains(snapshot.getKey())){
                        dbq.add(snapshot.getValue(Integer.class));
                    }
                }

                for(int i = 0;i<items.size();i++){
                    map.put(items.get(i),dbq.get(i)-qty.get(i));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.getReference().child("List").updateChildren(map);
                db.getReference().child("Cart"+uID).removeValue();
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });

    }
}