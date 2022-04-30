package ca.robertonicanor.robstoreapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import ca.robertonicanor.robstoreapp.ui.home.HomeFragment;

public class AdminCategoryActivity extends AppCompatActivity {

    private ImageView productA,
                      productB;

    private Button btnLogout,
                   btnValOrders,
                   btnViewExistingProd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_category);

        productA = findViewById(R.id.product_a);
        productB = findViewById(R.id.product_b);

        btnLogout = findViewById(R.id.btn_logout);
        btnValOrders = findViewById(R.id.btn_check_orders);
        btnViewExistingProd = findViewById(R.id.btn_exist_prod);

        btnViewExistingProd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AdminCategoryActivity.this, HomeActivity.class);
                startActivity(i);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AdminCategoryActivity.this, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();
            }
        });

        btnValOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AdminCategoryActivity.this, AdminNewOrdersActivity.class);
                startActivity(i);
            }
        });

        productA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminCategoryActivity.this, AdminAddNewProductActivity.class);
                intent.putExtra("category", "productA");
                startActivity(intent);
            }
        });

        productB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminCategoryActivity.this, AdminAddNewProductActivity.class);
                intent.putExtra("category", "productB");
                startActivity(intent);
            }
        });
    }
}
