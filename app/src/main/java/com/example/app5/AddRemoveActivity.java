package com.example.app5;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddRemoveActivity extends AppCompatActivity {

    Button add,remove,back;
    FirebaseDatabase db;
    ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_remove);

        add = findViewById(R.id.addButton);
        remove = findViewById(R.id.removeButton);
        back = findViewById(R.id.goBackButton);

        list = findViewById(R.id.itemsListView);
        db = FirebaseDatabase.getInstance();

        ArrayList<String> items = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.list_item,items);
        list.setAdapter(adapter);

        add.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialogBox = new AlertDialog.Builder(AddRemoveActivity.this);
                dialogBox.setTitle("Details:");

                final EditText details = new EditText(AddRemoveActivity.this);
                details.setInputType(InputType.TYPE_CLASS_TEXT);
                details.setHint("Name of the item,price,quantity");
                dialogBox.setView(details);

                dialogBox.setPositiveButton("Add", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String data = details.getText().toString();
                        String[] arr = data.split(",");
                        String n = arr[0].trim();
                        Integer p = Integer.parseInt(arr[1].trim());
                        Integer q = Integer.parseInt(arr[2].trim());

                        Map<String,Object> map1 = new HashMap<String,Object>();
                        Map<String,Object> map2 = new HashMap<String,Object>();
                        Map<String,Object> map3 = new HashMap<String,Object>();

                        map1.put(n,p);
                        map2.put(n,q);
                        map3.put(n,0);

                        db.getReference().child("Prices").updateChildren(map1);
                        db.getReference().child("List").updateChildren(map2);

                        Toast.makeText(getApplicationContext(),"Item added to database", Toast.LENGTH_SHORT).show();

                    }
                });

                dialogBox.setNegativeButton("Cancel",new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

                dialogBox.show();

            }
        });


        remove.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                list.setVisibility(View.VISIBLE);
                db.getReference().child("List").addValueEventListener(new ValueEventListener(){
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        items.clear();
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                            items.add(snapshot.getKey());
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        String selection = items.get(i);

                        AlertDialog.Builder dialogBox = new AlertDialog.Builder(AddRemoveActivity.this);
                        dialogBox.setTitle("Are you sure you want to delete " + selection.toLowerCase() + "?");

                        dialogBox.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                db.getReference().child("Prices").child(selection).removeValue();
                                db.getReference().child("List").child(selection).removeValue();
                                Toast.makeText(getApplicationContext(),"Item deleted",Toast.LENGTH_SHORT).show();
                            }
                        });

                        dialogBox.setNegativeButton("No",new DialogInterface.OnClickListener(){

                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });

                        dialogBox.show();

                    }
                });

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