package com.example.aicctv;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.flags.Flag;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.gun0912.tedpermission.util.ObjectUtils;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import gun0912.tedbottompicker.TedBottomPicker;
import io.reactivex.disposables.Disposable;



public class Setting_menu extends AppCompatActivity {

    Button logout_button;
    Button exit_button;
    Button goback;

    private FirebaseAuth mAuth ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_menu);
        FirebaseApp.initializeApp(this);

        goback = (Button) findViewById(R.id.button);
        logout_button = (Button) findViewById(R.id.button2);
        exit_button = (Button) findViewById(R.id.button3);

        mAuth = FirebaseAuth.getInstance();

        logout_button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                updateUI(null);
            }
        });

        exit_button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                mAuth.getCurrentUser().delete();
                updateUI(null);
            }
        });

        goback.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                Intent back = new Intent(getApplicationContext(), Menu.class);
                startActivity(back);
                finish();
            }
        });
    }

    private void updateUI(FirebaseUser user) { //update ui code here
        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }







}


