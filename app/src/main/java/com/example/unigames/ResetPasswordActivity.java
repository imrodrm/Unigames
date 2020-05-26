package com.example.unigames;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText mEditTextEmail;
    private Button mButtonReset;

    private FirebaseAuth mAuth;

    private String email = "";

    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        mAuth = FirebaseAuth.getInstance();

        mDialog = new ProgressDialog(this);

        mEditTextEmail = (EditText) findViewById(R.id.editTextEmailChange);
        mButtonReset = (Button) findViewById(R.id.btnResetPassword);

        mButtonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = mEditTextEmail.getText().toString();

                if(!email.isEmpty()) {
                    mDialog.setMessage("Espere...");
                    mDialog.setCanceledOnTouchOutside(false);
                    mDialog.show();
                    resetPassword();
                } else {
                    Toast.makeText(ResetPasswordActivity.this,"Please, fill the email gap",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void resetPassword() {
        //mAuth.setLanguageCode("es");
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(ResetPasswordActivity.this,"The email was sent",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ResetPasswordActivity.this,"We couldn't send the email, try again in a few minutes",Toast.LENGTH_SHORT).show();
                }
                mDialog.dismiss();
            }
        });
    }
}
