package com.example.androideatit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androideatit.Common.Common;
import com.example.androideatit.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private BackPressCloseHandler backPressCloseHandler;
    private String edtPhone, edtPassword;
    TextView txtSlogan;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        backPressCloseHandler = new BackPressCloseHandler(this);
        //로그인 되어있으면 바로 홈으로 아니면, 회원가입/로그인 뜨는 창으로
        if(isLogined()){
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            final DatabaseReference table_user = database.getReference("User");
            table_user.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.child(edtPhone).getValue(User.class);
                    Intent homeIntent = new Intent(MainActivity.this, Home.class);
                    Common.currentUser = user;
                    Toast.makeText(MainActivity.this, user.getName()+"님 환영합니다!", Toast.LENGTH_SHORT).show();
                    startActivity(homeIntent);
                    finish();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else{
            Intent startIntent = new Intent(MainActivity.this, StartActivity.class);
            startActivity(startIntent);
            finish();
        }
        txtSlogan = findViewById(R.id.txtSlogan);
        Typeface face = Typeface.createFromAsset(getAssets(),"fonts/NABILA.TTF");
        txtSlogan.setTypeface(face);
    }

    @Override
    public void onBackPressed() {
        backPressCloseHandler.onBackPressed();
    }

    public boolean isLogined(){
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        edtPhone = pref.getString("edtPhone", null);
        edtPassword = pref.getString("edtPassword", null);
        return edtPhone != null && edtPassword != null;
    }
}
