package com.example.aicctv;

import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;

public class Visitor_photo extends AppCompatActivity{

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference().child("00gpwls00");

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private ChildEventListener mChild;

    private ArrayList<Integer> images = new ArrayList<Integer>();
    DisplayMetrics mMetrics;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.visitor_each);

        Visitor v = new Visitor();
        String selectedItem = v.getItem();

        //firebase에 저장되어 있는 이미지 list로 저장
        databaseReference.child("VideoPhoto").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot visitorData : dataSnapshot.getChildren()) {
                    // child 내에 있는 데이터만큼 반복합니다.
                    Integer msg2 = (Integer) visitorData.child(selectedItem).getValue();
                    images.add(msg2);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {            }
        });

        GridView videophoto_view = (GridView) findViewById(R.id.video_photo_each);
        videophoto_view.setAdapter(new ImageAdapter(this));
        mMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(mMetrics);
    }

    public class ImageAdapter extends BaseAdapter{
        private Context mContext;

        public ImageAdapter(Context c){
            mContext = c;
        }
        public int getCount(){
            return images.size();
        }
        public Object getItem(int position){
            return images.get(position);
        }
        public long getItemId(int position){
            return position;
        }
        public View getView(int position, View convertView, ViewGroup parent){
            int rowWidth = (mMetrics.widthPixels) / 3;

            ImageView imageView;
            if(convertView==null){
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(rowWidth, rowWidth));
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                imageView.setPadding(1,1,1,1);
            }else{
                imageView = (ImageView) convertView;
            }
            imageView.setImageResource(images.get(position));
            return imageView;
        }
    }
}
