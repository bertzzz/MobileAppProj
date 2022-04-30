package ca.robertonicanor.robstoreapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ca.robertonicanor.robstoreapp.Model.UsersModel;
import ca.robertonicanor.robstoreapp.Prevalent.Prevalent;
import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {


    private Button btnJoinNow,
            btnLogin;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Paper.init(this);

        btnJoinNow = findViewById(R.id.main_join_now_btn);
        btnLogin = findViewById(R.id.main_login_btn);
        loadingBar = new ProgressDialog(this);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        btnJoinNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        String userNameKey = Paper.book().read(Prevalent.USER_NAME_KEY);
        String userPasswordKey = Paper.book().read(Prevalent.USER_PASSWORD_KEY);

        if(!TextUtils.isEmpty(userNameKey) && !TextUtils.isEmpty(userPasswordKey)){

            AllowAccess(userNameKey, userPasswordKey);

            loadingBar.setTitle("Already Logged In.");
            loadingBar.setMessage("Getting things ready for you...");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
        }
    }

    private void AllowAccess(String userName, String password) {

        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.child("Users").child(userName).exists()) {
                    UsersModel usersData = dataSnapshot.child("Users")
                            .child(userName)
                            .getValue(UsersModel.class);

                    if(usersData.getUsername().equals(userName)){

                        if(usersData.getPassword().equals(password)) {
                            Toast.makeText(MainActivity.this, "Log In Success.", Toast.LENGTH_LONG).show();
                            loadingBar.dismiss();

                            Prevalent.currentOnlineUser = usersData;
                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                            startActivity(intent);
                        }
                        else {
                            Toast.makeText(MainActivity.this, "Password is incorrect.", Toast.LENGTH_LONG).show();
                            loadingBar.dismiss();
                        }
                    }
                }
                else {
                    Toast.makeText(MainActivity.this,
                            "Account with this username: "+userName+" does not exist.",
                            Toast.LENGTH_LONG).show();
                    loadingBar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
