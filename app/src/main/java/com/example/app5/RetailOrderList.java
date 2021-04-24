package com.example.app5;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RetailOrderList extends AppCompatActivity {

    TextView bill;
    ListView orderList;
    Button back;
    FirebaseDatabase db;
    FirebaseAuth fAuth;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retail_order_list);

        db = FirebaseDatabase.getInstance();
        fAuth = FirebaseAuth.getInstance();
        orderList = findViewById(R.id.itemsListView);
        back = findViewById(R.id.backButton);

        dialog = new Dialog(RetailOrderList.this);
        dialog.setContentView(R.layout.custom_dialog);

        bill = dialog.findViewById(R.id.billView);

        ArrayList<String> ids = new ArrayList<String>();
        ArrayList<String> details = new ArrayList<String>();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.list_item,ids);
        orderList.setAdapter(adapter);

        db.getReference().child("Orders").child("Retailer").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                ids.clear();
                details.clear();

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String id = snapshot.getKey();
                    String detailName = snapshot.getValue(String.class);

                    ids.add(id);
                    details.add(detailName);

                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        orderList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String order = details.get(i);
                String[] splitArray = order.split(",");
                String output = "";

                for(int x = 0;x<splitArray.length;x++){
                    if(x == 0){
                        output = output + splitArray[x].substring(1).trim() + "\n";
                    } else if (x == splitArray.length-1){
                        output = output + splitArray[x].substring(0,splitArray[x].length()-1).trim() + "\n";
                    } else {
                        output = output + splitArray[x].trim() + "\n";
                    }
                }

                bill.setText(output);
                bill.setMovementMethod(new ScrollingMovementMethod());
                dialog.show();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),WholesaleMenu.class));
            }
        });

    }
}