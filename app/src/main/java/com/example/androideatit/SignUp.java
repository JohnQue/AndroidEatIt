package com.example.androideatit;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import com.example.androideatit.Model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rengwuxian.materialedittext.MaterialAutoCompleteTextView;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.Calendar;

import info.hoang8f.widget.FButton;

public class SignUp extends AppCompatActivity {

    private MaterialEditText edtId, edtPhone, edtName, edtPassword;
    private MaterialAutoCompleteTextView edtBirth;
    private DatePickerDialog.OnDateSetListener mDataSetListener;
    private FButton btnBirthChoose, btnSignUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        edtId = (MaterialEditText) findViewById(R.id.edtId);
        edtName = (MaterialEditText) findViewById(R.id.edtName);

        edtPhone = (MaterialEditText) findViewById(R.id.edtPhone);
        edtPhone.setText(getIntent().getStringExtra("edtPhone"));
        edtPhone.setEnabled(false);

        edtPassword = (MaterialEditText) findViewById(R.id.edtPassword);

        btnBirthChoose = (FButton)findViewById(R.id.btnBirthChoose);

        edtBirth = (MaterialAutoCompleteTextView) findViewById(R.id.edtBirth);
        edtBirth.setEnabled(false);

        btnBirthChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Calendar cal = Calendar.getInstance();
                    int year = cal.get(Calendar.YEAR);
                    int month = cal.get(Calendar.MONTH);
                    int day = cal.get(Calendar.DAY_OF_MONTH);

                    mDataSetListener = new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            String birth = Integer.toString(year);
                            if(month < 10)
                                birth += "0";
                            birth += Integer.toString(month);
                            if(dayOfMonth < 10)
                                birth += "0";
                            birth += Integer.toString(dayOfMonth);
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

        });
        btnSignUp = findViewById(R.id.btnSignUp);

        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog mDialog = new ProgressDialog(SignUp.this);
                mDialog.setMessage("Please waiting....");
                mDialog.show();

                User user = new User(edtPhone.getText().toString(),
                        edtName.getText().toString(),
                        edtPassword.getText().toString(),
                        edtBirth.getText().toString());
                // 아이디, 이름, 번호 다 입력했는지 체크
                db.collection("User").document(edtId.getText().toString())
                        .set(user)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("TAG", "SignUp Successfully!");
                                Toast.makeText(SignUp.this, "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(SignUp.this, StartActivity.class);
                                startActivity(intent);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(SignUp.this, "이미 등록된 아이디 입니다.", Toast.LENGTH_SHORT).show();
                                Log.w("TAG", "Signup Failed!", e);
                            }
                        });
            }
        });
    }
}
