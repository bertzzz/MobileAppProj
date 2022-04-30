package ca.robertonicanor.robstoreapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ca.robertonicanor.robstoreapp.Model.CartModel;
import ca.robertonicanor.robstoreapp.Model.Products;
import ca.robertonicanor.robstoreapp.ViewHolder.CartViewHolder;

public class OrderProductDetailsActivity extends AppCompatActivity {

    private RecyclerView ordersList;
    RecyclerView.LayoutManager layoutManager;

    private DatabaseReference cartlistRef;
    private String userName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_product_details);

        userName = getIntent().getStringExtra("username");
        ordersList = findViewById(R.id.order_list);
        ordersList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        ordersList.setLayoutManager(layoutManager);

        cartlistRef = FirebaseDatabase.getInstance().getReference()
                .child("Cart List")
                .child("Admin View")
                .child(userName)
                .child("Products");
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<CartModel> options =
                new FirebaseRecyclerOptions.Builder<CartModel>()
                .setQuery(cartlistRef, CartModel.class)
                .build();

        FirebaseRecyclerAdapter<CartModel, CartViewHolder> adapter =
                new FirebaseRecyclerAdapter<CartModel, CartViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull CartViewHolder cartViewHolder, int i, @NonNull CartModel cartModel) {

                        cartViewHolder.txtProductQuantity.setText("Qty = "+cartModel.getQuantity());
                        cartViewHolder.txtProductPrice.setText("Price: $"+cartModel.getPrice());
                        cartViewHolder.txtProductName.setText(cartModel.getPname());
                    }

                    @NonNull
                    @Override
                    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.cart_items_layout, parent, false);
                        CartViewHolder holder = new CartViewHolder(view);

                        return holder;
                    }
                };

        ordersList.setAdapter(adapter);
        adapter.startListening();
    }
}
