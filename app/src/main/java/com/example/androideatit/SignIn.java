package com.example.androideatit;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.androideatit.Common.Common;
import com.example.androideatit.Model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rengwuxian.materialedittext.MaterialEditText;

public class SignIn extends AppCompatActivity {
    EditText logId, logPassword;
    CheckBox edtCheck;
    Button btnSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        logPassword = (MaterialEditText)findViewById(R.id.logPassword);
        logId = (MaterialEditText)findViewById(R.id.logId);
        edtCheck = findViewById(R.id.edtCheck);
        btnSignIn = findViewById(R.id.btnSignIn);


        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog mDialog = new ProgressDialog(SignIn.this);
                mDialog.setMessage("Please waiting...");
                mDialog.show();

                db.collection("User")
                        .document(logId.getText().toString())
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                mDialog.dismiss();
                                Log.d("TAG", documentSnapshot.getId() + " => " +documentSnapshot.getData());
                                String getPwd = documentSnapshot.getString("password");
                                if(logPassword.getText().toString().equals(getPwd)){
                                    if(edtCheck.isChecked()){
                                        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = pref.edit();
                                        Toast.makeText(SignIn.this, documentSnapshot.getString("name")+"님 환영합니다!", Toast.LENGTH_SHORT).show();
                                        editor.putString("edtId", logId.getText().toString());
                                        editor.putString("edtPassword", logPassword.getText().toString());
                                        editor.apply();
                                    }
                                    String name = documentSnapshot.getString("name");
                                    String birth = documentSnapshot.getString("birthDate");
                                    String password = documentSnapshot.getString("password");
                                    String phone = documentSnapshot.getString("phone");
                                    User user = new User(name, birth, password, phone);
                                    Intent homeIntent = new Intent(SignIn.this, Home.class);
                                    homeIntent.putExtra("edtId", logId.getText().toString());
                                    Common.currentUser = user;
                                    startActivity(homeIntent);
                                    finish();
                                }else {
                                    Toast.makeText(SignIn.this, "아이디 혹은 비밀번호가 틀렸습니다.", Toast.LENGTH_SHORT).show();
                                    logPassword.setText("");
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                mDialog.dismiss();
                                Log.w("TAG", "Error updating document", e);
                                Toast.makeText(SignIn.this, "올바르지 않은 접속입니다.", Toast.LENGTH_SHORT).show();
                                logId.setText("");
                                logPassword.setText("");
                            }
                        });
            }
        });
    }
}
