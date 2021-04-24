package com.example.app5;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    TextView category;
    EditText phone, email, password,name,address;
    Button login, register,customer,retail,wholesale;
    FirebaseAuth fAuth;
    String optionChosen;
    FirebaseDatabase database,database2;
    Dialog dialog;

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        name = findViewById(R.id.nameEditText);
        email = findViewById(R.id.mailEditText);
        phone = findViewById(R.id.phoneEditText);
        password = findViewById(R.id.passwordEditText);
        address = findViewById(R.id.addressEditText);
        category = findViewById(R.id.categoryEditText);

        login = findViewById(R.id.loginButton);
        register = findViewById(R.id.regButton);

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.options);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        customer = dialog.findViewById(R.id.cButton);
        retail = dialog.findViewById(R.id.rButton);
        wholesale = dialog.findViewById(R.id.wButton);

        category.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                dialog.show();
            }
        });

        customer.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                optionChosen = "Customer";
                category.setText(optionChosen);
                dialog.cancel();
            }
        });

        retail.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                optionChosen = "Retailer";
                category.setText(optionChosen);
                dialog.cancel();
            }
        });

        wholesale.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                optionChosen = "Wholesaler";
                category.setText(optionChosen);
                dialog.cancel();
            }
        });

        fAuth = FirebaseAuth.getInstance();

        if(fAuth.getCurrentUser() != null){
            fAuth.signOut();
        }

        database = FirebaseDatabase.getInstance();
        database2 = FirebaseDatabase.getInstance();

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = name.getText().toString();
                String mail = email.getText().toString().trim();
                String ph = phone.getText().toString().trim();
                String pass = password.getText().toString().trim();
                String add = address.getText().toString();
                String[] adds = add.split(",");

                fAuth.createUserWithEmailAndPassword(mail, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            User user = new User(title,mail,ph,pass,add,optionChosen);
                            String uID = fAuth.getCurrentUser().getUid();
                            Map<String,Object> catMap = new HashMap<>();
                            catMap.put(uID,optionChosen);
                            database.getReference("Users")
                                    .child(uID).setValue(user)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {

                                                Toast.makeText(getApplicationContext(), "Registered Successfully", Toast.LENGTH_SHORT).show();
                                                if(optionChosen.equals("Customer")){
                                                    Intent intent = new Intent(getApplicationContext(), CustomerMenu.class);
                                                    startActivity(intent);
                                                }else if(optionChosen.equals("Retailer")){
                                                    Map<String,Object> r = new HashMap<>();
                                                    r.put(title,add +", ph : " + ph + "," + adds[adds.length-2].trim() + "," + adds[adds.length-1].trim());
                                                    database2.getReference().child("R").updateChildren(r);
                                                    Intent intent = new Intent(getApplicationContext(), RetailMenu.class);
                                                    startActivity(intent);
                                                }else{
                                                    Map<String,Object> w = new HashMap<>();
                                                    w.put(title,add +"\nPhone : " + ph +"," + adds[adds.length-2].trim() + "," +adds[adds.length-1].trim());
                                                    database2.getReference().child("W").updateChildren(w);
                                                    Intent intent = new Intent(getApplicationContext(),WholesaleMenu.class);
                                                    startActivity(intent);
                                                }
                                            } else {
                                                Toast.makeText(getApplicationContext(), "Fail", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(getApplicationContext(), "Fail", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });
    }
}