package ca.robertonicanor.robstoreapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class ProductManagerActivity extends AppCompatActivity {

    private Button btnApplyChges, btnDeleteProd;
    private EditText name, price, desc;
    private ImageView imageView;
    private String prodID = "";

    private DatabaseReference productRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_manager);

        prodID  = getIntent().getStringExtra("pid").toString();
        productRef = FirebaseDatabase.getInstance()
                .getReference()
                .child("Products")
                .child(prodID);

        btnApplyChges = findViewById(R.id.mng_product_btn);
        btnDeleteProd = findViewById(R.id.mng_delete_btn);
        name = findViewById(R.id.mng_product_name);
        price = findViewById(R.id.mng_product_price);
        desc = findViewById(R.id.mng_product_description);
        imageView = findViewById(R.id.mng_product_Image);
        
        ViewProductDetails();

        btnApplyChges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApplyChanges();
            }
        });

        btnDeleteProd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteProduct();
            }
        });
    }

    private void DeleteProduct() {
        productRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(ProductManagerActivity.this, "Product Deleted Successfully.", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(ProductManagerActivity.this, AdminCategoryActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private void ApplyChanges() {
        String pName = name.getText().toString();
        String pPrice = price.getText().toString();
        String pDesc = desc.getText().toString();

        if(pName.equals("")){
            Toast.makeText(this, "Please enter product name.", Toast.LENGTH_SHORT).show();
        }
        else if(pPrice.equals("")){
            Toast.makeText(this, "Please enter product price.", Toast.LENGTH_SHORT).show();
        }
        else if(pDesc.equals("")){
            Toast.makeText(this, "Please enter product description.", Toast.LENGTH_SHORT).show();
        }
        else {

            HashMap<String, Object> productMap = new HashMap<>();
            productMap.put("pid", prodID);
            productMap.put("description", pDesc);
            productMap.put("price", pPrice);
            productMap.put("pname", pName);

            productRef.updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(ProductManagerActivity.this, "Changes Applied Successfully.", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(ProductManagerActivity.this, AdminCategoryActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            });
        }
    }

    private void ViewProductDetails() {
        productRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String pName = dataSnapshot.child("pname").getValue().toString();
                    String pPrice = dataSnapshot.child("price").getValue().toString();
                    String pDesc = dataSnapshot.child("description").getValue().toString();
                    String pImage = dataSnapshot.child("image").getValue().toString();

                    name.setText(pName);
                    price.setText(pPrice);
                    desc.setText(pDesc);
                    Picasso.get().load(pImage).into(imageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
