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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private Button btnRegister;
    private EditText inputFullName,
            inputUsername,
            inputPassword;

    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btnRegister = findViewById(R.id.btn_register);
        inputFullName = findViewById(R.id.etxt_register_fname);
        inputUsername = findViewById(R.id.etxt_register_username);
        inputPassword = findViewById(R.id.etxt_register_password);
        loadingBar = new ProgressDialog(this);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAccount();
            }

        });
    }

    private void CreateAccount() {
        String fname = inputFullName.getText().toString();
        String userName = inputUsername.getText().toString();
        String password = inputPassword.getText().toString();

        if (TextUtils.isEmpty(fname)){
            Toast.makeText(this, "Please enter your Full Name...", Toast.LENGTH_LONG).show();
        }
        else if (TextUtils.isEmpty(userName)){
            Toast.makeText(this, "Please enter your User Name...", Toast.LENGTH_LONG).show();
        }
        else if (TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please enter your Password...", Toast.LENGTH_LONG).show();
        }
        else {
            loadingBar.setTitle("Create Account");
            loadingBar.setMessage("Please wait, creating account for "+fname+".");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            ValidateUserName(fname, userName, password);
        }
    }

    private void ValidateUserName(String fname,
                                  String userName,
                                  String password) {

        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if((!dataSnapshot.child("Users").child(userName).exists())){

                    HashMap<String, Object> userDataMap = new HashMap<>();
                    userDataMap.put("fullname", fname);
                    userDataMap.put("username", userName);
                    userDataMap.put("password", password);

                    RootRef.child("Users").child(userName).updateChildren(userDataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()) {
                                        Toast.makeText(RegisterActivity.this,
                                                "Account created successfully",
                                                Toast.LENGTH_LONG).show();
                                        loadingBar.dismiss();

                                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                    }
                                    else {
                                        loadingBar.dismiss();
                                        Toast.makeText(RegisterActivity.this,
                                                "Network error: Please try again.",
                                                Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
                else {

                    Toast.makeText(RegisterActivity.this,
                            "This "+userName+" already exists!",
                            Toast.LENGTH_LONG).show();
                    loadingBar.dismiss();

  /*                  Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(intent);*/
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
