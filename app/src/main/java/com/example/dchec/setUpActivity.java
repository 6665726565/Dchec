package com.example.dchec;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class setUpActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth ;
    private CardView emailCard;
    private ImageView shadowPage;
    private EditText userName , userNickName , userPhoneNumber , userLocalisation ;
    private Button registerBtn;
    private TextInputLayout setUpUserName ,setUpUserNickName , setUpPhoneNumber , setUpUserLocalisation;

    private FirebaseAuth mAuth;
    private DatabaseReference userRef ;
    private String currentUserId;
    private ProgressDialog progressDialog;
    private Boolean isProblem = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up);


        emailCard = findViewById(R.id.email_card);
        shadowPage = findViewById(R.id.shadow_page_setUp);
        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);


        userName = findViewById(R.id.user_name);
        userNickName = findViewById(R.id.user_nickname);
        userPhoneNumber = findViewById(R.id.user_phone_number);
        userLocalisation = findViewById(R.id.user_localisation);

        setUpUserName = findViewById(R.id.set_up_user_name);
        setUpUserNickName = findViewById(R.id.set_up_user__nick_name);
        setUpUserLocalisation = findViewById(R.id.set_up_user_localisation);
        setUpPhoneNumber = findViewById(R.id.set_up_user_phone_number);


        registerBtn = findViewById(R.id.setp_button);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = firebaseAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserId);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveAccountInformation();
            }
        });

        ChekingEmailVeriicated();

    }

    private void saveAccountInformation() {

        String savingUserName = userName.getText().toString();
        String savingNickName = userNickName.getText().toString();
        String savingPhoneNumber = userPhoneNumber.getText().toString();
        String savingLocalisation = userLocalisation.getText().toString();


        if (TextUtils.isEmpty(savingUserName)) {
            userName.setError("obligatoire");
        }

        if (TextUtils.isEmpty(savingNickName)) {
            userNickName.setError("obligatoire");
        }

        if (TextUtils.isEmpty(savingPhoneNumber)) {
            userPhoneNumber.setError("obligatoire");
        }

        if (TextUtils.isEmpty(savingLocalisation)) {
            savingLocalisation="";
        }


        if (TextUtils.isEmpty(savingNickName) || TextUtils.isEmpty(savingUserName) || TextUtils.isEmpty(savingPhoneNumber) ){
            isProblem = true ;
        }else{
            isProblem = false;
        }


        if (!isProblem){

            HashMap userMap = new HashMap();
            userMap.put("user name",savingUserName);
            userMap.put("nick name",savingNickName);
            userMap.put("phone number",savingPhoneNumber);
            userMap.put("localisation" , savingLocalisation);


            progressDialog.setTitle("Saving Information ..");
            progressDialog.setMessage("creating account is on progress ...");
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(false);

            userRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        sendUserToHomeActivity();
                        Toast.makeText(setUpActivity.this, "your account is created ", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }else {
                        Toast.makeText(setUpActivity.this, "eurro occured : "+ task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
            });

        }
    }

    private void sendUserToHomeActivity() {
        Intent mainActivity = new Intent(setUpActivity.this,HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainActivity);
        finish();
    }

    @Override
    protected void onRestart() {
        super.onRestart();


        ChekingEmailVeriicated();

    }

    private void ChekingEmailVeriicated() {
        firebaseAuth.getCurrentUser().reload().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (firebaseAuth.getCurrentUser().isEmailVerified()) {

                    shadowPage.setVisibility(View.GONE);
                    emailCard.setVisibility(View.GONE);
                    setUpUserName.setClickable(true);
                    setUpPhoneNumber.setClickable(true);
                    setUpUserNickName.setClickable(true);
                    setUpUserLocalisation.setClickable(true);
                    registerBtn.setClickable(true);
                } else {
                    shadowPage.setVisibility(View.VISIBLE);
                    emailCard.setVisibility(View.VISIBLE);
                    setUpUserName.setClickable(false);
                    setUpPhoneNumber.setClickable(false);
                    setUpUserNickName.setClickable(false);
                    setUpUserLocalisation.setClickable(false);
                    registerBtn.setClickable(false);
                    Toast.makeText(setUpActivity.this, "svp , verifier votre email", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}