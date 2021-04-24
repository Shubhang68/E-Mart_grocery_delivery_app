package com.example.app5;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {

    Button backCart,payment;
    ListView cartList;
    FirebaseAuth fAuth;
    FirebaseDatabase cartData;
    DatabaseReference cartRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        cartList = findViewById(R.id.priceListView);
        backCart = findViewById(R.id.shopButton);
        payment = findViewById(R.id.proceedButton);
        cartData = FirebaseDatabase.getInstance();

        fAuth = FirebaseAuth.getInstance();
        String uID = fAuth.getCurrentUser().getUid();

        cartRef = cartData.getReference().child("Cart"+uID);

        ArrayList<String> arrayList = new ArrayList<String>();
        ArrayList<String> itemC = new ArrayList<String>();
        ArrayList<Integer> qtyC = new ArrayList<>();
        ArrayList<Integer> positionsC = new ArrayList<>();

        ArrayAdapter arrayAdapter = new ArrayAdapter<String>(this,R.layout.list_item,arrayList);
        cartList.setAdapter(arrayAdapter);

        cartRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayList.clear();
                itemC.clear();
                qtyC.clear();
                positionsC.clear();
                Integer index = 0;
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String s = snapshot.getKey();
                    String q = snapshot.getValue().toString();

                    if(Integer.parseInt(q) != 0){
                        arrayList.add(s + " : " + q + "kgs");
                        itemC.add(s);
                        qtyC.add(Integer.parseInt(q));
                        positionsC.add(index);
                    }
                    index = index + 1;
                }
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),BillActivity.class);
                //intent.putExtra("itemsArray",itemC);
                //intent.putExtra("quantitiesArray",qtyC);
                //intent.putExtra("positionArray",positionsC);
                //intent.putExtra("balanceData",balanceData);
                startActivity(intent);
            }
        });

        backCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),ShopActivity.class));
            }
        });

    }
}