package com.example.aicctv;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.gun0912.tedpermission.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

public class Person extends AppCompatActivity {

    private ListView list;
    static ArrayList<String> listdata;
    ArrayAdapter<String> adapter;
    SearchView searchView;

    public ArrayList<String> getList(){
        return listdata;
    }
    public int getPosition(){
        return selectedPosition;
    }

    Button deleteButton;
    private int selectedPosition;

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference childreference = firebaseDatabase.getReference().child("00gpwls00/PhotoLink/");

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference().child("00gpwls00/Photo/");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);

        searchView = (SearchView)findViewById(R.id.SearchView1);
        list = (ListView) findViewById(R.id.listView1);
        listdata = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listdata);
        list.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        list.setOnItemClickListener(listener);

        //searchView 구현 코드
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextSubmit(String text) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        //db에서 가져와서 list 보여주는 코드
        childreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                adapter.clear();

                for (DataSnapshot messageData : dataSnapshot.getChildren()) {
                    String message = messageData.getKey();
                    if (!ObjectUtils.isEmpty(messageData.child("oneface").getValue())) {

                        long oneface_num = (long) messageData.child("oneface").getValue();
                        long need = 40 - oneface_num;
                        if (oneface_num >= 40) {
                            listdata.add(message + " [등록]");
                        } else {
                            listdata.add(message + " [미등록/" + need + "장 부족]");
                        }

                        //adapter.add(message);
                    }
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {            }
        });


        //list의 항목들 선택 코드
        AdapterView.OnItemClickListener listener= new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //선택한 항목의 position 저장한 후 listView에 선택 설정
                selectedPosition = position;
                list.setItemChecked(selectedPosition, true);
            }
        };

        FloatingActionButton floatingActionbutton = (FloatingActionButton) findViewById(R.id.float1);
        floatingActionbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent5 = new Intent(getApplicationContext(), Register.class);
                startActivityForResult(intent5, 1);
            }
        });
    }

    AdapterView.OnItemClickListener listener= new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            String selected_item = (String)adapterView.getItemAtPosition(position);
            Intent intent7 = new Intent(getApplicationContext(), Register_2.class);
            intent7.putExtra("selected_item",selected_item);
            startActivity(intent7);
        }
    };

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }
}