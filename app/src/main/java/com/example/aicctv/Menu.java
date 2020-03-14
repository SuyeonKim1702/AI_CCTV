package com.example.aicctv;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.appcompat.app.AppCompatActivity;

public class Menu extends AppCompatActivity implements View.OnClickListener{
    private FirebaseAuth mAuth ;
    Button btnLogout, btnRevoke;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Button button = (Button) findViewById(R.id.monitoring);
        button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                Intent intent1 = new Intent(getApplicationContext(), Monitoring.class);
                startActivity(intent1);
            }
        });

        Button button2 = (Button) findViewById(R.id.video);
        button2.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                Intent videoo = new Intent(getApplicationContext(), Video.class);
                startActivity(videoo);
            }
        });

        Button button3 = (Button) findViewById(R.id.person);
        button3.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                Intent image = new Intent(getApplicationContext(), Person.class);
                startActivity(image);
            }
        });

        Button button4 = (Button) findViewById(R.id.connect);
        button4.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                Intent connection = new Intent(getApplicationContext(), Contact.class);
                startActivity(connection);
            }
        });

        mAuth = FirebaseAuth.getInstance();
        btnLogout = (Button)findViewById(R.id.btn_logout);
        btnRevoke = (Button)findViewById(R.id.btn_revoke);

        btnLogout.setOnClickListener(this);
        btnRevoke.setOnClickListener(this);

    }

    private void signOut() {    //로그아웃 함수
        FirebaseAuth.getInstance().signOut();
    }

    private void revokeAccess() {   //회원탈퇴 함수
        mAuth.getCurrentUser().delete();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_logout:
                signOut();
                updateUI(null);
                break;
            case R.id.btn_revoke:
                revokeAccess();
                updateUI(null);
                break;
        }
    }

    private void updateUI(FirebaseUser user) { //update ui code here
        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
