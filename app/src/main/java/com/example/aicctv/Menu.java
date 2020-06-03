package com.example.aicctv;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class Menu extends AppCompatActivity {
    private FirebaseAuth mAuth ;
    Button btnLogout, btnRevoke,button4;
    TextView nickName,state;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference().child("00gpwls00");
    private DatabaseReference databaseReference2 = firebaseDatabase.getReference();

    private ArrayList<String> numlist = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        nickName = (TextView)findViewById(R.id.textView10);
        state = (TextView)findViewById(R.id.textView12);
        //firebase의 database를 listview로 표현
        databaseReference.child("Contact_number").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot numberData : dataSnapshot.getChildren()) {
                    // child 내에 있는 데이터만큼 반복합니다.
                    String msg2 = (String) numberData.child("number").getValue();
                    numlist.add(msg2);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {            }
        });

        //db에서 가져와서 list 보여주는 코드
        databaseReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                for (DataSnapshot messageData : dataSnapshot.getChildren()) {
                    nickName.setText((String)messageData.child("Nickname").getValue()+"님 안녕하세요");
                    state.setText((String)messageData.child("State").getValue());
                    if(((String)messageData.child("State").getValue()).equals("안전"))
                        state.setTextColor(Color.parseColor("#0067a3"));
                    else
                        state.setTextColor(Color.parseColor("#b6181e"));

                    // System.out.println(messageData.getKey());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });




        button4 = (Button) findViewById(R.id.button4);
        button4.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                Intent intent10 = new Intent(getApplicationContext(), Setting_menu.class);
                startActivity(intent10);
            }
        });


        //실시간 모니터링 버튼
        Button button = (Button) findViewById(R.id.monitoring);
        button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                Intent intent1 = new Intent(getApplicationContext(), Monitoring.class);
                startActivity(intent1);
            }
        });

        //녹화 화면 시청 버튼
        Button button2 = (Button) findViewById(R.id.video);
        button2.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                Intent videoo = new Intent(getApplicationContext(), Video.class);
                startActivity(videoo);
            }
        });

        //등록 인물 관리 버튼
        Button button3 = (Button) findViewById(R.id.person);
        button3.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                Intent image = new Intent(getApplicationContext(), Person.class);
                startActivity(image);
            }
        });

        //긴급 연락망 관리 버튼
        Button button4 = (Button) findViewById(R.id.connect);
        button4.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                Intent connection = new Intent(getApplicationContext(), Contact.class);
                startActivity(connection);
            }
        });

        //방문자 사진 관리 버튼
        Button button5 = (Button) findViewById(R.id.visitor);
        button5.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                Intent visit = new Intent(getApplicationContext(), Visitor.class);
                startActivity(visit);
            }
        });

        //긴급 메세지 수정 버튼
        Button button6 = (Button) findViewById(R.id.emerText_change);
        button6.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                Intent change_mes = new Intent(getApplicationContext(), Help_messa_change.class);
                startActivity(change_mes);
            }
        });

        //도움 요청 버튼
        SmsManager sms = SmsManager.getDefault();
        Button button7 = (Button) findViewById(R.id.Emergency);
        button7.setOnClickListener(new View.OnClickListener(){

            public void onClick(View view) {
                int count = 0;
                if(!numlist.isEmpty()) {
                    count = numlist.size();
                }

                Help_messa_change h = new Help_messa_change();
                String help_mes = h.getM();

                try {
                    SmsManager smsManager = SmsManager.getDefault();
                    //list에 저장되어 있는 번호들에 모두 메세지 전송
                    for (int i = 0; i <count; i++){
                        String phoneNum = (String)numlist.get(i);
                        System.out.println(phoneNum);
                        sms.sendTextMessage(phoneNum, null, help_mes, null, null);
                    }
                    Toast.makeText(getApplicationContext(), help_mes+"도움 요청 메시지 전송 완료", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "도움 요청 실패", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });

        mAuth = FirebaseAuth.getInstance();



    }


}