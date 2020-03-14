package com.example.aicctv;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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



public class Register extends AppCompatActivity {
    int i=0;
    int j=1;
    int k=0;
    int index;
    EditText editText_name;
    Button confirm_button,cancel_button;
    private ImageView iv_image;
    private List<Uri> selectedUriList;
    private Uri selectedUri;
    private Disposable singleImageDisposable;
    private Disposable multiImageDisposable;
    private ViewGroup mSelectedImagesContainer;
    private RequestManager requestManager;
    private StorageReference mStorageRef;
    private ImageView[] thumbnail;
    public static int UpdateCount;
    int DownCount,oneface;
    SimpleDateFormat formatter;
    private final String SAMPLE_CROPPED_IMAGE_NAME = "sample_image";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        FirebaseApp.initializeApp(this);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        editText_name = findViewById(R.id.name);
        confirm_button = findViewById(R.id.confirm_button);
        cancel_button = findViewById(R.id.cancel_button);
        iv_image = findViewById(R.id.iv_image);
        mSelectedImagesContainer = findViewById(R.id.selected_photos_container);
        requestManager = Glide.with(this);
        String name = editText_name.getText().toString();
        formatter = new SimpleDateFormat("yyyyMMdd_HHmmss");

        confirm_button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                if (selectedUriList!= null&&editText_name!=null) {
                    final ProgressDialog progressDialog = new ProgressDialog(Register.this);
                    progressDialog.setTitle("업로드중...");
                    progressDialog.show();
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageRef;
                    String name = editText_name.getText().toString();
                    FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
                    DatabaseReference childreference=firebaseDatabase.getReference().child("00gpwls00/PhotoLink/"+name);
                    ValueEventListener valueEventListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if(dataSnapshot.child("UpdateCount").getValue()!=null) {
                                UpdateCount = dataSnapshot.child("UpdateCount").getValue(Integer.class);
                                firebaseDatabase.getReference().child("00gpwls00/PhotoLink/" + name + "/" + "UpdateCount").setValue(UpdateCount + i - 1);
                            }else{
                                DownCount=0;
                                UpdateCount=0;
                                oneface=0;
                            //UpdateCount = dataSnapshot.getValue(Integer.class);
                            System.out.println("UpdateCount:"+UpdateCount);
                            firebaseDatabase.getReference().child("00gpwls00/PhotoLink/"+name+"/"+"UpdateCount").setValue(UpdateCount+i-1);
                            firebaseDatabase.getReference().child("00gpwls00/PhotoLink/"+name+"/"+"DownCount").setValue(DownCount);
                            firebaseDatabase.getReference().child("00gpwls00/PhotoLink/"+name+"/"+"oneface").setValue(oneface);

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    };
                    childreference.addListenerForSingleValueEvent(valueEventListener);

                    i = 1;

                    for (Uri uri : selectedUriList) {
                        Log.d("pics", "" + uri);
                        String filename;
                        String urlname;
                        //FirebaseStorage storage = FirebaseStorage.getInstance();
                        //Unique한 파일명을 만들자.

                        Date now = new Date();
                        filename = formatter.format(now) + "_" + i + ".png";
                        urlname = formatter.format(now) + "_" + i;

                        //storage 주소와 폴더 파일명을 지정해 준다.
                        storageRef = storage.getReferenceFromUrl("gs://aicctv-8f5ac.appspot.com").child("/00gpwls00/Photo/"+name+"/"+filename);

                        storageRef.putFile(uri)
                                //성공시
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        Toast.makeText(getApplicationContext(), "업로드 완료!"+"("+j+"/"+selectedUriList.size()+")", Toast.LENGTH_SHORT).show();
                                        j++;

                                                taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        Log.d("★★★★★★★★11111", uri.toString());
                                                        String imgurl=uri.toString();
                                                        firebaseDatabase.getReference().child("/00gpwls00/PhotoLink/"+name+"/"+urlname).setValue(imgurl);

                                                    }
                                                }).toString();

                                    }

                                })
                                //실패시
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(), "업로드 실패!", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                //진행중
                                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                        @SuppressWarnings("VisibleForTests") //
                                                double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                        //dialog에 진행률을 퍼센트로 출력해 준다
                                        progressDialog.setMessage("Uploaded " + ((int) progress) + "% ...");

                                    }
                                });
                        i++;
                    }
                    progressDialog.dismiss();


                    Intent intent6 = new Intent();
                    intent6.putExtra("name", name);
                    setResult(RESULT_OK, intent6);
                    finish();

                }

            }
        });

        cancel_button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                finish();
            }
        });

        setMultiShowButton();
}


    private void setMultiShowButton() {

        Button btnMultiShow = findViewById(R.id.gallery_button);
        btnMultiShow.setOnClickListener(view -> {

            PermissionListener permissionlistener = new PermissionListener() {
                @Override
                public void onPermissionGranted() {
                    TedBottomPicker.with(Register.this)
                            //.setPeekHeight(getResources().getDisplayMetrics().heightPixels/2)
                            .setPeekHeight(1600)
                            .showTitle(false)
                            .setCompleteButtonText("Done")
                            .setEmptySelectionText("No Select")
                            .setSelectedUriList(selectedUriList)
                            .showMultiImage(uriList -> {
                                selectedUriList = uriList;
                                showUriList(selectedUriList);
                            });

                }

                @Override
                public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                    Toast.makeText(Register.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
                }
            };
            checkPermission(permissionlistener);
        });
    }

    private void checkPermission(PermissionListener permissionlistener) {
        TedPermission.with(Register.this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }

    private void showUriList(List<Uri> uriList) {

        mSelectedImagesContainer.removeAllViews();

        iv_image.setVisibility(View.GONE);
        mSelectedImagesContainer.setVisibility(View.VISIBLE);

        int widthPixel = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());
        int heightPixel = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());

        thumbnail = new ImageView[70];
        k=0;
        for (Uri uri : uriList) {

            View imageHolder = LayoutInflater.from(this).inflate(R.layout.image_item, null);
            thumbnail[k] = imageHolder.findViewById(R.id.media_image);
            thumbnail[k].setId(k);
            thumbnail[k].setOnClickListener(PclickListener);


            requestManager
                    .load(uri.toString())
                    .apply(new RequestOptions().fitCenter())
                    .into(thumbnail[k]);

            mSelectedImagesContainer.addView(imageHolder);
            thumbnail[k].setLayoutParams(new FrameLayout.LayoutParams(widthPixel, heightPixel));
            k++;
        }

    }
    View.OnClickListener PclickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            System.out.println(v.getId());
            index =v.getId();
            Uri first = selectedUriList.get(index);
            startCrop(first);

        }
    };

    protected void onActivityResult(int requestCode, int resultCode,@Nullable Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==UCrop.REQUEST_CROP&&resultCode== RESULT_OK){
            Uri resultUri = UCrop.getOutput(data);
            if(resultUri!=null){
                System.out.println(resultUri.toString());
                selectedUriList.remove(index);
                selectedUriList.add(resultUri);
                showUriList(selectedUriList);

            }

        }

    }

    private void startCrop(@NonNull Uri uri) {
        Date now_ = new Date();
        String destinationFileName = formatter.format(now_);
        destinationFileName += ".png";


        UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), destinationFileName)));
        uCrop.withAspectRatio(1, 1);
        uCrop.withMaxResultSize(450, 450);
        uCrop.withOptions(getCropOptions());

        uCrop.start(Register.this);



    }

    private UCrop.Options getCropOptions() {
        UCrop.Options options = new UCrop.Options();
        options.setCompressionQuality(70);

        options.setHideBottomControls(false);
        options.setFreeStyleCropEnabled(true);
        options.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        options.setToolbarColor(getResources().getColor(R.color.colorPrimary));

        options.setToolbarTitle("사진에 하나의 얼굴만 포함되도록 잘라주세요");
        return options;
    }


    @Override
    protected void onDestroy() {
        if (singleImageDisposable != null && !singleImageDisposable.isDisposed()) {
            singleImageDisposable.dispose();
        }
        if (multiImageDisposable != null && !multiImageDisposable.isDisposed()) {
            multiImageDisposable.dispose();
        }
        super.onDestroy();
    }




}