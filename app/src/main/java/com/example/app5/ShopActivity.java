package com.example.app5;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ShopActivity extends AppCompatActivity {
    ListView customerList;
    FirebaseDatabase database,database2;
    DatabaseReference reference;
    FirebaseAuth fAuth;
    SearchView searchBar;
    Context context;
    Button cartButton,add;
    Dialog dialog;
    TextView item,status;
    EditText qty;
    ImageView img;
    Drawable drawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        customerList = findViewById(R.id.priceListView);
        database = FirebaseDatabase.getInstance();
        database2 = FirebaseDatabase.getInstance();
        searchBar = findViewById(R.id.searchBarView);
        context = this;
        cartButton = findViewById(R.id.shopButton);
        fAuth = FirebaseAuth.getInstance();

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.item_data);

        item = dialog.findViewById(R.id.itemTextView);
        status = dialog.findViewById(R.id.statusTextView);
        qty = dialog.findViewById(R.id.quantityEditText);
        add = dialog.findViewById(R.id.addButton);
        img = dialog.findViewById(R.id.imageView);

        String uID = fAuth.getCurrentUser().getUid();

        ArrayList<String> list = new ArrayList<String>();
        ArrayList<Integer> qtyList = new ArrayList<Integer>();

        ArrayAdapter adapter = new ArrayAdapter<String>(this,R.layout.list_item,list);
        customerList.setAdapter(adapter);

        reference = database.getReference("List");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                qtyList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String item = snapshot.getKey();
                    Integer qty = snapshot.getValue(Integer.class);
                    list.add(item);
                    qtyList.add(qty);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.getFilter().filter(s);
                return false;
            }
        });

        customerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                database2.getReference("Prices").addValueEventListener(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String product = list.get(i);

                        String name = product.toLowerCase();
                        int id = getResources().getIdentifier(name, "drawable", getPackageName());
                        drawable = getResources().getDrawable(id);
                        img.setImageDrawable(drawable);

                        Integer q = qtyList.get(i);
                        Integer price = dataSnapshot.child(product).getValue(Integer.class);
                        item.setText(product + " - " + price.toString() + "/kg");
                        if(q > 0){
                            qty.setVisibility(View.VISIBLE);
                            add.setText("Add to Cart");
                            status.setText("Status : Available");
                        }else{
                            qty.setVisibility(View.INVISIBLE);
                            add.setText("Back");
                            status.setText("Status : Available in 24 hrs");
                        }
                        dialog.show();

                        add.setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View view) {
                                if(add.getText().toString().equals("Add to Cart")){
                                    Integer input = Integer.parseInt(qty.getText().toString());
                                    if(input == null){
                                        Toast.makeText(getApplicationContext(), "Enter a quantity", Toast.LENGTH_SHORT).show();

                                    } else if(input>q){
                                        Toast.makeText(getApplicationContext(), "Try a smaller quantity", Toast.LENGTH_SHORT).show();
                                        qty.getText().clear();
                                    }else{
                                        Map<String, Object> cartMap = new HashMap<String, Object>();
                                        cartMap.put(product, input);
                                        qty.getText().clear();
                                        database.getReference().child("Cart" + uID).updateChildren(cartMap);
                                        dialog.cancel();
                                    }
                                }else{
                                    dialog.cancel();
                            }
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        cartButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),CartActivity.class);
                startActivity(intent);
            }
        });
    }
}