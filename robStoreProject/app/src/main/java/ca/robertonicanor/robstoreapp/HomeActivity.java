package ca.robertonicanor.robstoreapp;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import java.security.CryptoPrimitive;

import ca.robertonicanor.robstoreapp.Prevalent.Prevalent;
import ca.robertonicanor.robstoreapp.ui.cart.CartFragment;
import ca.robertonicanor.robstoreapp.ui.settings.SettingsFragment;
import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class HomeActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private TextView userNameTxtV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Paper.init(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Home");
        setSupportActionBar(toolbar);

        FloatingActionButton fabAddOrder = findViewById(R.id.fab);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_cart,
                R.id.nav_search, R.id.nav_settings, R.id.nav_logout)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        View headerView = navigationView.getHeaderView(0);
        userNameTxtV = headerView.findViewById(R.id.user_profile_name);
        CircleImageView profileImageV = headerView.findViewById(R.id.user_profile_image);

        userNameTxtV.setText(Prevalent.currentOnlineUser.getName());

        fabAddOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabAddOrder.hide();

                CartFragment fragment = new CartFragment();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.nav_host_fragment, fragment);
                transaction.commit();
            }
        });

        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller,
                                             @NonNull NavDestination destination,
                                             @Nullable Bundle arguments) {

                Intent intent;
                fabAddOrder.hide();

                switch (destination.getId()){
                    case R.id.nav_cart:
                        /*Toast.makeText(HomeActivity.this, "Cart", Toast.LENGTH_SHORT).show();*/

                        break;

                    case R.id.nav_search:
                        /*Toast.makeText(HomeActivity.this, "Search", Toast.LENGTH_SHORT).show();*/

                        break;

                    case R.id.nav_settings:
                        /*Toast.makeText(HomeActivity.this, "Settings", Toast.LENGTH_SHORT).show();*/

                        break;

                    case R.id.nav_logout:

                        Paper.book().destroy();
                        intent = new Intent(HomeActivity.this, MainActivity.class);
                        startActivity(intent);

                        finish();

                        break;

                        default:
                            /*Toast.makeText(HomeActivity.this, "Home", Toast.LENGTH_SHORT).show();*/
                            if(!Prevalent.currentOnlineUserType.isAdmin()) {
                                fabAddOrder.show();
                            }

                            break;
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        userNameTxtV.setText(Prevalent.currentOnlineUser.getName());

        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


}
