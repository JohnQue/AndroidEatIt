package com.example.androideatit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androideatit.Common.Common;
import com.example.androideatit.Model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private BackPressCloseHandler backPressCloseHandler;
    private String edtId, edtPassword;
    TextView txtSlogan;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        backPressCloseHandler = new BackPressCloseHandler(this);
        //로그인 되어있으면 바로 홈으로 아니면, 회원가입/로그인 뜨는 창으로
        if(isLogined()){
            final FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("User").document(edtId)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            String name = documentSnapshot.getString("name");
                            String birth = documentSnapshot.getString("birthDate");
                            String password = documentSnapshot.getString("password");
                            String phone = documentSnapshot.getString("phone");
                            User user = new User(name, birth, password, phone);
                            Toast.makeText(MainActivity.this, name+"님 환영합니다!", Toast.LENGTH_SHORT).show();
                            Intent homeIntent = new Intent(MainActivity.this, Home.class);
                            Common.currentUser = user;
                            startActivity(homeIntent);
                            finish();
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
        edtId = pref.getString("edtId", null);
        edtPassword = pref.getString("edtPassword", null);
        return edtId != null && edtPassword != null;
    }
}
