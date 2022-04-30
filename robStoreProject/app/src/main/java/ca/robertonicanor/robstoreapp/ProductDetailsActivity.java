package ca.robertonicanor.robstoreapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import ca.robertonicanor.robstoreapp.Model.Products;
import ca.robertonicanor.robstoreapp.Prevalent.Prevalent;

public class ProductDetailsActivity extends AppCompatActivity {

    private Button addToCartBtn;
    private ImageView productImage;
    private ElegantNumberButton itemsQtyBtn;
    private TextView productPrice,
                     productName,
                     productDesc;
    private String productID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        productID = getIntent().getStringExtra("pid");


        productImage = findViewById(R.id.product_image_details);
        productName = findViewById(R.id.product_name_details);
        productDesc = findViewById(R.id.product_desc_details);
        productPrice = findViewById(R.id.product_price_details);
        addToCartBtn = findViewById(R.id.product_add_to_cart_btn);
        itemsQtyBtn = findViewById(R.id.number_qty_btn);

        getProductDetails(productID);

        addToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddItemToCartList();
            }
        });
    }

    private void AddItemToCartList() {
        String saveCurrentDate,
               saveCurrentTime;

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");

        final HashMap<String, Object> cartMap = new HashMap<>();
        cartMap.put("pid", productID);
        cartMap.put("pname", productName.getText().toString());
        cartMap.put("price", productPrice.getText().toString());
        cartMap.put("date", saveCurrentDate);
        cartMap.put("time", saveCurrentDate);
        cartMap.put("quantity", itemsQtyBtn.getNumber());

        cartListRef.child("User View")
                   .child(Prevalent.currentOnlineUser
                   .getUsername()).child("Products")
                   .child(productID)
                   .updateChildren(cartMap)
                   .addOnCompleteListener(new OnCompleteListener<Void>() {
                       @Override
                       public void onComplete(@NonNull Task<Void> task) {
                           if(task.isSuccessful()){
                               cartListRef.child("Admin View")
                                       .child(Prevalent.currentOnlineUser
                                       .getUsername()).child("Products")
                                       .child(productID)
                                       .updateChildren(cartMap)
                                       .addOnCompleteListener(new OnCompleteListener<Void>() {
                                           @Override
                                           public void onComplete(@NonNull Task<Void> task) {
                                               if(task.isSuccessful()){
                                                   Toast.makeText(ProductDetailsActivity.this, "Added to Cart", Toast.LENGTH_SHORT).show();

                                                   Intent intent = new Intent(ProductDetailsActivity.this, HomeActivity.class);
                                                   startActivity(intent);
                                               }
                                           }
                                       });
                           }
                       }
                   });
    }

    private void getProductDetails(String productID) {

        DatabaseReference productRef = FirebaseDatabase
                .getInstance()
                .getReference()
                .child("Products");

        productRef.child(productID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Products products = dataSnapshot.getValue(Products.class);

                    productName.setText(products.getPname());
                    productPrice.setText(products.getPrice());
                    productDesc.setText(products.getDescription());
                    Picasso.get().load(products.getImage()).into(productImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
