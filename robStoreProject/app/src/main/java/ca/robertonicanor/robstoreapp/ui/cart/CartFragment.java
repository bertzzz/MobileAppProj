package ca.robertonicanor.robstoreapp.ui.cart;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableRow;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ca.robertonicanor.robstoreapp.ConfirmFinalOrderActivity;
import ca.robertonicanor.robstoreapp.HomeActivity;
import ca.robertonicanor.robstoreapp.Model.CartModel;
import ca.robertonicanor.robstoreapp.Prevalent.Prevalent;
import ca.robertonicanor.robstoreapp.ProductDetailsActivity;
import ca.robertonicanor.robstoreapp.R;
import ca.robertonicanor.robstoreapp.ViewHolder.CartViewHolder;

public class CartFragment extends Fragment {

    private CartViewModel cartViewModel;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Button nextProcessBtn;
    private TextView txtTotalAmount;
    private TextView backBtnTxt;

    private double overTotalPrice;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        if(Prevalent.currentOnlineUserType.isAdmin()){
            Intent i = new Intent(getActivity(), HomeActivity.class);
            startActivity(i);
            getActivity().overridePendingTransition(0, 0);

            Toast.makeText(getActivity(), "Customer Cart disabled for Admin user.", Toast.LENGTH_SHORT).show();
        }

        cartViewModel =
                ViewModelProviders.of(this).get(CartViewModel.class);
        View root = inflater.inflate(R.layout.fragment_cart, container, false);

        recyclerView = root.findViewById(R.id.cart_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        nextProcessBtn = root.findViewById(R.id.next_process_btn);
        txtTotalAmount = root.findViewById(R.id.txtV_totalprice_cartlist);
        backBtnTxt = root.findViewById(R.id.back_btn_txt);

        nextProcessBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getActivity(), ConfirmFinalOrderActivity.class);
                i.putExtra("Total Price", String.valueOf(overTotalPrice));
                startActivity(i);
                getActivity().overridePendingTransition(600, 600);

                overTotalPrice = 0;

            }
        });

        backBtnTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getActivity(), HomeActivity.class);
                startActivity(i);
                getActivity().overridePendingTransition(0, 0);
            }
        });

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();

        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");

        FirebaseRecyclerOptions<CartModel> options = new FirebaseRecyclerOptions.Builder<CartModel>()
                .setQuery(cartListRef.child("User View")
                .child(Prevalent.currentOnlineUser.getUsername())
                .child("Products"), CartModel.class)
                .build();

        FirebaseRecyclerAdapter<CartModel, CartViewHolder> adapter = new FirebaseRecyclerAdapter<CartModel, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder cartViewHolder, int i, @NonNull CartModel cartModel) {
                cartViewHolder.txtProductQuantity.setText("Qty = "+cartModel.getQuantity());
                cartViewHolder.txtProductPrice.setText("Price: $"+cartModel.getPrice());
                cartViewHolder.txtProductName.setText(cartModel.getPname());

                double indiProdTypeTPrice = ((Double.valueOf(cartModel.getPrice()))) * Double.valueOf(cartModel.getQuantity());
                overTotalPrice = overTotalPrice + indiProdTypeTPrice;

                txtTotalAmount.setText("Total Price = $" + String.valueOf(overTotalPrice));
                
                cartViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CharSequence options[] = new CharSequence[]
                                {
                                  "Edit",
                                  "Delete"
                                };
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("Cart Options:");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int index) {
                                if(index == 0){
                                    Intent intent = new Intent(getContext(), ProductDetailsActivity.class);
                                    intent.putExtra("pid", cartModel.getPid());
                                    startActivity(intent);
                                }
                                if(index == 1){
                                    cartListRef.child("User View")
                                               .child(Prevalent.currentOnlineUser.getUsername())
                                               .child("Products")
                                               .child(cartModel.getPid())
                                               .removeValue()
                                               .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                   @Override
                                                   public void onComplete(@NonNull Task<Void> task) {
                                                     if(task.isSuccessful()){
                                                         Toast.makeText(getContext(), "Item removed successfully.", Toast.LENGTH_SHORT).show();

                                                         Intent intent = new Intent(getContext(), HomeActivity.class);
                                                         startActivity(intent);
                                                     }    
                                                   }
                                               });
                                }
                            }
                        });

                        builder.show();
                    }
                });
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

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

}