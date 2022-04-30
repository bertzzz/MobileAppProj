package ca.robertonicanor.robstoreapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ca.robertonicanor.robstoreapp.Model.Orders;
import ca.robertonicanor.robstoreapp.Prevalent.Prevalent;

public class AdminNewOrdersActivity extends AppCompatActivity {

    private RecyclerView ordersList;
    private DatabaseReference ordersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_new_orders);

        ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders");

        ordersList = findViewById(R.id.order_list);
        ordersList.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Orders> options =
                new FirebaseRecyclerOptions.Builder<Orders>()
                .setQuery(ordersRef, Orders.class)
                .build();

        FirebaseRecyclerAdapter<Orders, AdminOrdersViewHolder> adapter =
                new FirebaseRecyclerAdapter<Orders, AdminOrdersViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull AdminOrdersViewHolder adminOrdersViewHolder, int i, @NonNull Orders orders) {

                        adminOrdersViewHolder.name.setText("Name: " + orders.getName());
                        adminOrdersViewHolder.phoneNumber.setText("Phone: " + orders.getPhone());
                        adminOrdersViewHolder.totalPrice.setText("Total Amount: $" + orders.getTotalAmount());
                        adminOrdersViewHolder.dateTime.setText("Processed at: " + orders.getDate() + " " + orders.getTime());
                        adminOrdersViewHolder.shippingAddress.setText("Address: " + orders.getAddress());
                        adminOrdersViewHolder.shippingCity.setText("City: " + orders.getCity());
                        adminOrdersViewHolder.shippingPostal.setText("Postal: " + orders.getPostal());

                        adminOrdersViewHolder.btnOrderDetails.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                String uID = getRef(i).getKey();

                                Intent i = new Intent(AdminNewOrdersActivity.this, OrderProductDetailsActivity.class);
                                i.putExtra("username", uID);
                                startActivity(i);
                            }
                        });

                        adminOrdersViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CharSequence options[] = new CharSequence[] {
                                  "Yes",
                                  "No"
                                };
                                AlertDialog.Builder builder = new AlertDialog.Builder(AdminNewOrdersActivity.this);
                                builder.setTitle("Mark order as shipped?");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if(which == 0){
                                            String uID = getRef(i).getKey();

                                            RemoveOrder(uID);
                                        }
                                        else {
                                            // Close Dialog and do nothing.
                                        }
                                    }
                                });
                                builder.show();
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public AdminOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_layout, parent, false);
                        return new AdminOrdersViewHolder(view);
                    }
                };

        ordersList.setAdapter(adapter);
        adapter.startListening();
    }

    private void RemoveOrder(String uID) {

        ordersRef.child(uID).removeValue();
    }


    public static class AdminOrdersViewHolder extends RecyclerView.ViewHolder {

        public TextView name,
                phoneNumber,
                totalPrice,
                dateTime,
                shippingAddress,
                shippingCity,
                shippingPostal;

        public Button btnOrderDetails;

        public AdminOrdersViewHolder(View v) {
            super(v);

            name = v.findViewById(R.id.order_user_name);
            phoneNumber = v.findViewById(R.id.order_phone_number);
            totalPrice = v.findViewById(R.id.order_total_price);
            dateTime = v.findViewById(R.id.order_date_time);
            shippingAddress = v.findViewById(R.id.order_address);
            shippingCity = v.findViewById(R.id.order_city);
            shippingPostal = v.findViewById(R.id.order_postal);

            btnOrderDetails = v.findViewById(R.id.btn_open_order_details);
        }
    }
}
