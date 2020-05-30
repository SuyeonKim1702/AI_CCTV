package com.example.aicctv;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Help_messa_change extends AppCompatActivity{

    public static String helpmessage = "위험한 상황에 처했습니다. 도움을 요청합니다.";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.emergencytext_change);

        Button btn = (Button) findViewById(R.id.change_button);
        TextView mess = (TextView) findViewById(R.id.EmergencyText);
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                helpmessage = mess.getText().toString();
                Toast.makeText(Help_messa_change.this, "수정되었습니다.", Toast.LENGTH_SHORT).show();

            }
        });
    }
    public String getM(){
        return helpmessage;
    }
}
