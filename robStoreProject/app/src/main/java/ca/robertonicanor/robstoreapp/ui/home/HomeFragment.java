package ca.robertonicanor.robstoreapp.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import ca.robertonicanor.robstoreapp.Model.Products;
import ca.robertonicanor.robstoreapp.Prevalent.Prevalent;
import ca.robertonicanor.robstoreapp.ProductDetailsActivity;
import ca.robertonicanor.robstoreapp.ProductManagerActivity;
import ca.robertonicanor.robstoreapp.R;
import ca.robertonicanor.robstoreapp.ViewHolder.ProductViewHolder;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private DatabaseReference productsRef;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        productsRef = FirebaseDatabase.getInstance().getReference().child("Products");

        recyclerView = root.findViewById(R.id.recycler_menu);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(root.getContext());
        recyclerView.setLayoutManager(layoutManager);

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Products> options =
                new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(productsRef, Products.class)
                .build();

        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter =
                new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolder productViewHolder,
                                                    int i,
                                                    @NonNull Products products) {

                        productViewHolder.txtproductName.setText(products.getPname());
                        productViewHolder.txtProductPrice.setText("$"+products.getPrice());
                        productViewHolder.txtProductDescription.setText(products.getDescription());
                        Picasso.get().load(products.getImage()).into(productViewHolder.imageView);

                        productViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (Prevalent.currentOnlineUserType.isAdmin()){
                                    Intent intent = new Intent(getActivity(), ProductManagerActivity.class);
                                    intent.putExtra("pid", products.getPid());
                                    startActivity(intent);
                                }
                                else {
                                    Intent intent = new Intent(getActivity(), ProductDetailsActivity.class);
                                    intent.putExtra("pid", products.getPid());
                                    startActivity(intent);
                                }
                            }
                        });

                    }

                    @NonNull
                    @Override
                    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items_layout,
                                                                                     parent,
                                                                         false);
                        ProductViewHolder holder = new ProductViewHolder(view);
                        return holder;
                    }
                };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}