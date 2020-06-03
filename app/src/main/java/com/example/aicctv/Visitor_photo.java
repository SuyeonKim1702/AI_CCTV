package com.example.aicctv;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class Visitor_photo extends AppCompatActivity{
    int i=0;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private ChildEventListener mChild;
    Bitmap bitmap;
    private ArrayList<Bitmap> images = new ArrayList<Bitmap>();
    DisplayMetrics mMetrics;
    ArrayList list_=new ArrayList();
    final long ONE_MEGABYTE = 1024 * 1024* 30;
    StorageReference storageRef;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference childreference;
    GridView videophoto_view;

    private String name;
    public String getPhotoName(){
        return name;
    }

    private int changeORnot = 0;
    private int pos= 0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.visitor_each);

        Visitor v = new Visitor();

        Intent intent = getIntent(); /*데이터 수신*/

        name = intent.getExtras().getString("selected_item");/*String형*/

        childreference = firebaseDatabase.getReference("00gpwls00/VideoPhoto/"+name);

        FirebaseStorage storage = FirebaseStorage.getInstance();

        childreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                for (DataSnapshot messageData : dataSnapshot.getChildren()) {
                    String message = messageData.getKey();
                    list_.add(message);
                }

                for (Object element : list_) {

                    String element_=(String)element;
                    storageRef = storage.getReferenceFromUrl("gs://aicctv-8f5ac.appspot.com").child("/00gpwls00/VideoPhoto/"+name+"/"+element+".png");
                    storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            // Data for "images/island.jpg" is returns, use this as needed
                            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                            images.add(bitmap);
                            //  if(i==list_.size()-1)
                            videophoto_view.setAdapter(new ImageAdapter(getApplicationContext()));
                            //i++;
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle any errors
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        videophoto_view = (GridView) findViewById(R.id.video_photo_each);
        mMetrics = getResources().getDisplayMetrics();
        videophoto_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //리스트 뷰의 항목이선택되면 여기서 처리.
            @Override  //컨트롤
            public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
                PopupClick(view);
                pos = position;
            }//onItemClick
        });
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

            ImageView imageView = new ImageView(mContext);
            imageView.setImageBitmap(images.get(position));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setLayoutParams(new GridView.LayoutParams(rowWidth, rowWidth));
            return imageView;
        }
    }

    public void PopupClick(View v){
        Intent intent = new Intent(this,Visitor_popup.class);
        intent.putExtra("data", name);
        startActivityForResult(intent, 1);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                //데이터 받기
                changeORnot = data.getIntExtra("result", 0);
                if(changeORnot == 1){
                    images.remove(pos);
                    videophoto_view.setAdapter(new ImageAdapter(getApplicationContext()));
                }
            }
        }
    }
}