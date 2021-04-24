package com.example.app5;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

public class CustomerMenu extends AppCompatActivity {

    Button lastOrder,logout,shop,feedback;
    TextView name;
    FirebaseAuth fAuth;
    FirebaseDatabase db,db1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_menu);

        db = FirebaseDatabase.getInstance();
        db1 = FirebaseDatabase.getInstance();
        fAuth = FirebaseAuth.getInstance();

        lastOrder = findViewById(R.id.ordersButton);
        logout = findViewById(R.id.logoutButton);
        shop = findViewById(R.id.shoppingButton);
        feedback = findViewById(R.id.feedbackButton);

        name = findViewById(R.id.nameText);

        String uID = fAuth.getCurrentUser().getUid();

        db.getReference().child("Users").child(uID).addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String adder = snapshot.child("title").getValue(String.class);
                name.setText("Name : " + adder);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        shop.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),ShopActivity.class));
            }
        });

        lastOrder.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                db1.getReference().child("Latest_Order").addValueEventListener(new ValueEventListener(){
                    int count = 0;
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                            if(snapshot.getKey().contains(uID)){
                                count++;
                            }
                        }
                        if(count == 1){
                            startActivity(new Intent(getApplicationContext(),CancelOrder.class));
                        }else{
                            Toast.makeText(getApplicationContext(),"You haven't placed any order yet",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        logout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                fAuth.signOut();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

        feedback.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), FeedbackActivity.class));
            }
        });

    }
}