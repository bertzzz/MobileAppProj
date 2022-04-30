package ca.robertonicanor.robstoreapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rey.material.widget.CheckBox;

import ca.robertonicanor.robstoreapp.Model.UsersModel;
import ca.robertonicanor.robstoreapp.Prevalent.Prevalent;
import ca.robertonicanor.robstoreapp.Prevalent.UserType;
import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {


    private Button btnLogin;
    private CheckBox chkBoxRememberMe;
    private EditText inputUsername,
            inputPassword;
    private TextView txtvAdminLink,
            txtvUserLink;
    private ProgressDialog loadingBar;

    private String parentDbName = "Users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputUsername = findViewById(R.id.etxt_login_username);
        inputPassword = findViewById(R.id.etxt_login_password);
        btnLogin = findViewById(R.id.login_btn);
        txtvAdminLink = findViewById(R.id.txtV_admin_panel_link);
        txtvUserLink = findViewById(R.id.txtV_notadmin_panel_link);
        loadingBar = new ProgressDialog(this);

        chkBoxRememberMe = findViewById(R.id.chbox_rememberMe);
        Paper.init(this);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        txtvAdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnLogin.setText("Login Admin");
                txtvAdminLink.setVisibility(View.INVISIBLE);
                txtvUserLink.setVisibility(View.VISIBLE);
                parentDbName = "Admins";
            }
        });

        txtvUserLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnLogin.setText("Login");
                txtvAdminLink.setVisibility(View.VISIBLE);
                txtvUserLink.setVisibility(View.INVISIBLE);
                parentDbName = "Users";
            }
        });
    }

    private void loginUser() {
        String userName = inputUsername.getText().toString();
        String password = inputPassword.getText().toString();

        if (TextUtils.isEmpty(userName)){
            Toast.makeText(this, "Please enter your User Name...", Toast.LENGTH_LONG).show();
        }
        else if (TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please enter your Password...", Toast.LENGTH_LONG).show();
        }
        else {
            loadingBar.setTitle("Logging In");
            loadingBar.setMessage("Getting things ready for you...");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            AllowAccessToAccount(userName, password);
        }
    }

    private void AllowAccessToAccount(String userName, String password) {

        if(chkBoxRememberMe.isChecked()){
            Paper.book().write(Prevalent.USER_NAME_KEY, userName);
            Paper.book().write(Prevalent.USER_PASSWORD_KEY, password);
        }

        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.child(parentDbName).child(userName).exists()) {
                    UsersModel usersData = dataSnapshot.child(parentDbName)
                            .child(userName)
                            .getValue(UsersModel.class);

                    if(usersData.getUsername().equals(userName)){

                        if(usersData.getPassword().equals(password)) {
                            if(parentDbName.equals("Admins")){
                                Toast.makeText(LoginActivity.this, "Log In Success.", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                                Intent intent = new Intent(LoginActivity.this, AdminCategoryActivity.class);
                                Prevalent.currentOnlineUserType = new UserType(true);
                                Prevalent.currentOnlineUser = usersData;
                                startActivity(intent);
                            }
                            else if (parentDbName.equals("Users")){
                                Toast.makeText(LoginActivity.this, "Log In Success, welcome "+userName+".", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                Prevalent.currentOnlineUserType = new UserType(false);
                                Prevalent.currentOnlineUser = usersData;
                                startActivity(intent);
                            }
                        }
                        else {
                            Toast.makeText(LoginActivity.this, "Password is incorrect.", Toast.LENGTH_LONG).show();
                            loadingBar.dismiss();
                        }
                    }
                }
                else {
                    Toast.makeText(LoginActivity.this,
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
