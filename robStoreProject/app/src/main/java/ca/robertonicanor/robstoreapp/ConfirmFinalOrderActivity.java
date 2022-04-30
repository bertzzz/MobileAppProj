package ca.robertonicanor.robstoreapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import ca.robertonicanor.robstoreapp.Prevalent.Prevalent;

public class ConfirmFinalOrderActivity extends AppCompatActivity {

    private EditText txte_name,
                     txte_phone,
                     txte_address,
                     txte_city,
                     txte_postal;

    private Button btn_confim_order;
    private String totalAmount = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_final_order);

        totalAmount = getIntent().getStringExtra("Total Price");
        Toast.makeText(this, "Total Price = $" + totalAmount, Toast.LENGTH_SHORT).show();

        btn_confim_order = findViewById(R.id.btn_confirm_order);
        txte_name = findViewById(R.id.txte_shipment_name);
        txte_phone = findViewById(R.id.txte_shipment_number);
        txte_address = findViewById(R.id.txte_shipment_address);
        txte_city = findViewById(R.id.txte_shipment_city);
        txte_postal = findViewById(R.id.txte_shipment_postal);

        btn_confim_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateFields();
            }
        });

    }

    private void ValidateFields() {
        if (TextUtils.isEmpty(txte_name.getText().toString())) {
            Toast.makeText(this, "Please enter your full name.", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(txte_phone.getText().toString())) {
            Toast.makeText(this, "Please enter your phone number.", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(txte_address.getText().toString())) {
            Toast.makeText(this, "Please enter your address.", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(txte_city.getText().toString())) {
            Toast.makeText(this, "Please enter your city.", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(txte_postal.getText().toString())) {
            Toast.makeText(this, "Please enter your postal code.", Toast.LENGTH_SHORT).show();
        }
        else {
            ConfirmOrder();
        }
    }

    private void ConfirmOrder() {
        final String saveCurrentDate,
               saveCurrentTime;

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        final DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference()
                .child("Orders")
                .child(Prevalent.currentOnlineUser.getUsername());

        HashMap<String, Object> ordersMap = new HashMap<>();
        ordersMap.put("totalAmount", totalAmount);
        ordersMap.put("name", txte_name.getText().toString());
        ordersMap.put("phone", txte_phone.getText().toString());
        ordersMap.put("address", txte_address.getText().toString());
        ordersMap.put("city", txte_address.getText().toString());
        ordersMap.put("postal", txte_postal.getText().toString());
        ordersMap.put("date", saveCurrentTime);
        ordersMap.put("time", saveCurrentDate);
        ordersMap.put("state", "pending");

        ordersRef.updateChildren(ordersMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    FirebaseDatabase.getInstance().getReference().child("Cart List")
                            .child("User View")
                            .child(Prevalent.currentOnlineUser.getUsername())
                            .removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isComplete()) {
                                        Toast.makeText(ConfirmFinalOrderActivity.this, "Your order has been placed successfully.", Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(ConfirmFinalOrderActivity.this, HomeActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                }
            }
        });
    }
}
