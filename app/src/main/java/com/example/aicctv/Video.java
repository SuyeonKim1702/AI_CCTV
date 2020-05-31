package com.example.aicctv;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;

public class Video extends AppCompatActivity {

    private ListView list;
    ArrayList<String> listdata;
    ArrayAdapter<String> adapter;

    private int selectedPosition;

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference childreference = firebaseDatabase.getReference().child("00gpwls00/FaceResult/");

    //FirebaseStorage storage = FirebaseStorage.getInstance();
    //StorageReference storageRef = storage.getReference().child("00gpwls00/Video/");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        list = (ListView) findViewById(R.id.listView1);
        listdata = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listdata);
        list.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        list.setOnItemClickListener(listener2);

        //db에서 가져와서 list 보여주는 코드
        childreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                adapter.clear();

                for (DataSnapshot messageData : dataSnapshot.getChildren()) {
                    String message = messageData.getKey();
                    String name = (String) messageData.getValue();
                    listdata.add(message + "(" + name + ")");
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });



    }
    AdapterView.OnItemClickListener listener2= new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            String selected_item = (String) adapterView.getItemAtPosition(position);
            Intent intent10 = new Intent(getApplicationContext(), Video_each.class);
            intent10.putExtra("selected_item", selected_item);
            startActivity(intent10);
        }
    };
}