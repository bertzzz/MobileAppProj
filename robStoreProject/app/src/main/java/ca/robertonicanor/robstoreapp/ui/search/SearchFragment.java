package ca.robertonicanor.robstoreapp.ui.search;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mapbox.mapboxsdk.plugins.annotation.Line;
import com.squareup.picasso.Picasso;

import ca.robertonicanor.robstoreapp.HomeActivity;
import ca.robertonicanor.robstoreapp.Model.Products;
import ca.robertonicanor.robstoreapp.Prevalent.Prevalent;
import ca.robertonicanor.robstoreapp.ProductDetailsActivity;
import ca.robertonicanor.robstoreapp.R;
import ca.robertonicanor.robstoreapp.ViewHolder.ProductViewHolder;

public class SearchFragment extends Fragment {

    private SearchViewModel searchViewModel;

    private Button btn_search;
    private EditText txt_input;
    private RecyclerView searchList;

    private String searchInput;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        if(Prevalent.currentOnlineUserType.isAdmin()){
            Intent i = new Intent(getActivity(), HomeActivity.class);
            startActivity(i);
            getActivity().overridePendingTransition(0, 0);

            Toast.makeText(getActivity(), "Customer Search disabled for Admin user.", Toast.LENGTH_SHORT).show();
        }

        searchViewModel =
                ViewModelProviders.of(this).get(SearchViewModel.class);
        View root = inflater.inflate(R.layout.fragment_search, container, false);

        txt_input = root.findViewById(R.id.search_ticket_name);
        btn_search = root.findViewById(R.id.btn_search);
        searchList = root.findViewById(R.id.search_list);
        searchList.setLayoutManager(new LinearLayoutManager(getActivity()));

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchInput = txt_input.getText().toString();
                onStart();
            }
        });

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();

        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference()
                .child("Products");

        FirebaseRecyclerOptions<Products> options =
                new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(reference.orderByChild("pname").startAt(searchInput), Products.class)
                .build();

        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter =
                new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolder productViewHolder, int i, @NonNull Products products) {

                        productViewHolder.txtproductName.setText(products.getPname());
                        productViewHolder.txtProductPrice.setText("$"+products.getPrice());
                        productViewHolder.txtProductDescription.setText(products.getDescription());
                        Picasso.get().load(products.getImage()).into(productViewHolder.imageView);

                        productViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getActivity(), ProductDetailsActivity.class);
                                intent.putExtra("pid", products.getPid());
                                startActivity(intent);
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
        searchList.setAdapter(adapter);
        adapter.startListening();
    }
}