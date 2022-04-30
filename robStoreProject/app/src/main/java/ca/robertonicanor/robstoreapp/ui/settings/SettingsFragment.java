package ca.robertonicanor.robstoreapp.ui.settings;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import org.w3c.dom.Text;

import java.util.HashMap;

import ca.robertonicanor.robstoreapp.HomeActivity;
import ca.robertonicanor.robstoreapp.Prevalent.Prevalent;
import ca.robertonicanor.robstoreapp.R;
import ca.robertonicanor.robstoreapp.ui.home.HomeFragment;
import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsFragment extends Fragment {

    private SettingsViewModel settingsViewModel;
    private EditText txtE_fullname,
                     txtE_address;
    private TextView txtV_close,
                     txtV_save;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        if(Prevalent.currentOnlineUserType.isAdmin()){
            Intent i = new Intent(getActivity(), HomeActivity.class);
            startActivity(i);
            getActivity().overridePendingTransition(0, 0);

            Toast.makeText(getActivity(), "Customer Settings disabled for Admin user.", Toast.LENGTH_SHORT).show();
        }

       settingsViewModel =
                ViewModelProviders.of(this).get(SettingsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_settings, container, false);

        txtE_fullname = root.findViewById(R.id.settings_full_name);
        txtE_address = root.findViewById(R.id.settings_address);
        txtV_close = root.findViewById(R.id.close_settings);
        txtV_save = root.findViewById(R.id.update_account_settings);

        DisplayUserInfo(
                txtE_fullname,
                txtE_address);

        txtV_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        txtV_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    SaveAllChanges();
            }
        });

        return root;
    }

    private void SaveAllChanges(){

        if(TextUtils.isEmpty(txtE_fullname.getText().toString())){
            Toast.makeText(getContext(), "Name field cannot be blank.", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(txtE_address.getText().toString())){
            Toast.makeText(getContext(), "Address field cannot be blank.", Toast.LENGTH_SHORT).show();
        }
        else {
            SaveProfile();
        }
    }

    private void SaveProfile(){

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("name", txtE_fullname.getText().toString());
        userMap.put("address", txtE_address.getText().toString());

        databaseReference.child(Prevalent.currentOnlineUser.getUsername()).updateChildren(userMap);

        Prevalent.currentOnlineUser.setName(txtE_fullname.getText().toString());

        getActivity().onBackPressed();
        Toast.makeText(getContext(), "Profile Update Successfully.", Toast.LENGTH_SHORT).show();
    }

    private void DisplayUserInfo(
            EditText txtE_fullname,
            EditText txtE_address) {

        DatabaseReference userRef = FirebaseDatabase.getInstance()
                .getReference()
                .child(Prevalent.currentOnlineUser.getUsername());

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    if(dataSnapshot.child("username").exists()){

                        String name,
                               address;

                        name = dataSnapshot.child("name").getValue().toString();
                        address = dataSnapshot.child("address").getValue().toString();

                        txtE_fullname.setText(name);
                        txtE_address.setText(address);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}