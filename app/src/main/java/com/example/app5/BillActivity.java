package com.example.app5;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class BillActivity extends AppCompatActivity {

    FirebaseDatabase db1,db2,db3;
    ListView orderList;
    Button pay,cancel;
    TextView total;
    Integer count,len,amount=0;
    FirebaseAuth fAuth;
    String uID;

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill);


        db1 = FirebaseDatabase.getInstance();
        db2 = FirebaseDatabase.getInstance();
        db3 = FirebaseDatabase.getInstance();
        fAuth = FirebaseAuth.getInstance();

        orderList = findViewById(R.id.billListView);
        pay = findViewById(R.id.payButton);
        cancel = findViewById(R.id.cancelButton);
        total = findViewById(R.id.totalTextView);
        fAuth = FirebaseAuth.getInstance();

        uID = fAuth.getCurrentUser().getUid();

        ArrayList<String> bill = new ArrayList<>();
        ArrayList<String> items = new ArrayList<>();
        ArrayList<Integer> quantities = new ArrayList<>();
        ArrayList<Integer> prices = new ArrayList<Integer>();
        ArrayList<String> cost = new ArrayList<>();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_item_1, bill);
        orderList.setAdapter(adapter);

        db1.getReference("Cart"+uID).addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                items.clear();
                quantities.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    if(snapshot.getValue(Integer.class)>0) {
                        items.add(snapshot.getKey());
                        quantities.add(snapshot.getValue(Integer.class));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        db2.getReference("Prices").addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                prices.clear();
                bill.clear();
                cost.clear();
                count = 0;
                len = 0;
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    if(items.contains(snapshot.getKey())) {
                        String item = items.get(len);
                        Integer qty = quantities.get(len);
                        Integer price = snapshot.getValue(Integer.class);
                        prices.add(price);
                        bill.add(item + " = " + qty.toString() + " x " + price.toString());
                        Integer product = qty * price;
                        amount = amount + product;
                        len = len+1;
                    }
                    count = count+1;

                    if(len == items.size()){
                        total.setText("Total : " +amount.toString());
                        cost.add(amount.toString());
                        break;
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        pay.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                bill.add("Total : " + cost.get(0));
                bill.add("Details : ");
                Intent intent = new Intent(getApplicationContext(),DeliveryActivity.class);
                intent.putExtra("order",bill);
                startActivity(intent);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                db3.getReference().child("Cart"+uID).removeValue();
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });

    }
}