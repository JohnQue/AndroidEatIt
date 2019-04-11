package com.example.androideatit;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androideatit.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.Calendar;

public class SignUp extends AppCompatActivity {

    private MaterialEditText edtPhone, edtName, edtPassword, edtBirth;
    private DatePickerDialog.OnDateSetListener mDataSetListener;
    private Button btnSignUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        edtName = (MaterialEditText) findViewById(R.id.edtName);

        edtPhone = (MaterialEditText) findViewById(R.id.edtPhone);
        edtPhone.setText(getIntent().getStringExtra("edtPhone"));
        edtPhone.setEnabled(false);

        edtPassword = (MaterialEditText) findViewById(R.id.edtPassword);

        edtBirth = (MaterialEditText) findViewById(R.id.edtBirth);
        edtBirth.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    Calendar cal = Calendar.getInstance();
                    int year = cal.get(Calendar.YEAR);
                    int month = cal.get(Calendar.MONTH);
                    int day = cal.get(Calendar.DAY_OF_MONTH);

                    mDataSetListener = new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            Toast.makeText(SignUp.this, year + "/" + month + "/" + dayOfMonth, Toast.LENGTH_SHORT).show();
                            String birth;
                            if(month < 10)
                                birth = Integer.toString(year) + "0" + Integer.toString(month) + Integer.toString(dayOfMonth);
                            else birth = Integer.toString(year) + Integer.toString(month) + Integer.toString(dayOfMonth);
                            edtBirth.setText(birth);
                        }
                    };
                    DatePickerDialog dialog = new DatePickerDialog(
                            SignUp.this,
                            android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                            mDataSetListener,
                            year, month, day);
                    dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();
                }
            }
        });
        btnSignUp = findViewById(R.id.btnSignUp);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("User");

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog mDialog = new ProgressDialog(SignUp.this);
                mDialog.setMessage("Please waiting...");
                mDialog.show();

                // 아이디, 이름, 번호 다 입력했는지 체크
               table_user.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //Check if already user phone
                        if(dataSnapshot.child(edtPhone.getText().toString()).exists()){
                            mDialog.dismiss();
                            Toast.makeText(SignUp.this, "Phone Number has already been registered", Toast.LENGTH_SHORT).show();
                        }else{
                            mDialog.dismiss();
                            User user = new User(edtName.getText().toString(), edtPassword.getText().toString());
                            table_user.child(edtPhone.getText().toString()).setValue(user);
                            Toast.makeText(SignUp.this, "Sign up successfully!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SignUp.this, StartActivity.class);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }
}
