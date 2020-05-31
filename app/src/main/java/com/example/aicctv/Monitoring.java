
package com.example.aicctv;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.gun0912.tedpermission.util.ObjectUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import org.mp4parser.Container;
import org.mp4parser.muxer.FileDataSourceImpl;
import org.mp4parser.muxer.Movie;
import org.mp4parser.muxer.builder.DefaultMp4Builder;
import org.mp4parser.muxer.tracks.h264.H264TrackImpl;

import static java.io.File.createTempFile;


public class Monitoring extends AppCompatActivity {
    FirebaseStorage storage2;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference childreference;
    StorageReference videoRef;
    File localFile;
    TextView textView;
    ProgressDialog dialog;
    VideoView videoView;
    String videoname;
    File videopath;
    String[] link;
    String target,storagelink;
    int delete_count=0;
    int delete_count_link=0;
    int delete_count_link2=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoring);
        videoView = (VideoView)findViewById(R.id.videoView);
        textView= (TextView)findViewById(R.id.textview);
        firebaseDatabase=FirebaseDatabase.getInstance();
        childreference=firebaseDatabase.getReference().child("00gpwls00/VideoLink/");
        storage2 = FirebaseStorage.getInstance();


        dialog = ProgressDialog.show(this, "실시간 영상 가져오기", "로딩 중 입니다.", true, true);

        Handler mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 0) {
                    dialog.dismiss();
                    playVideo(videoname);
                }

            }
        } ;

        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                ValueEventListener valueEventListener = new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int k=0;
                        long count = dataSnapshot.getChildrenCount()-2;
                        int int_count = (int)count;
                        link=new String[int_count];
                        for (DataSnapshot messageData : dataSnapshot.getChildren()) {
                            link[k]=messageData.getKey();
                            k++;
                            if(k==int_count) break;
                        }
                        //확인용
                        for(int i=0;i<link.length;i++)
                            System.out.println(link[i]);
                        System.out.println("링크 갯수:"+link.length);
                        target=link[link.length-1];
                        storagelink=target+".mp4";
                        System.out.println("storage link:"+storagelink);
                        videoRef = storage2.getReferenceFromUrl("gs://aicctv-8f5ac.appspot.com").child("/00gpwls00/Video/"+storagelink);
                        downloadVideo(videoRef);
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        mHandler.sendEmptyMessage(0);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                };
                childreference.addListenerForSingleValueEvent(valueEventListener); //맨 처음 한번만 호출됨

            }
        });
        th.start();

    }

    public void removeDir(File file) {
        File[] childFileList = file.listFiles();
        if (!(ObjectUtils.isEmpty(childFileList))) {
            for (File childFile : childFileList) {
                childFile.delete();    //하위 파일
            }
        }
    }

    public void playVideo(String videoname) {

        MediaController controller = new MediaController(com.example.aicctv.Monitoring.this);
        videoView.setMediaController(controller);
        videoView.requestFocus();
        String path = getFilesDir()+"/realtime"+"/"+videoname;
        videoView.setVideoPath(path);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                dialog.dismiss();
                String datename = target.substring(0,4)+"년 "+target.substring(4,6)+"월 "+target.substring(6,8)+"일 "+target.substring(9,11)+"시 "+target.substring(11,13)+"분 "+target.substring(13,15)+"초";
                textView.setText(datename);
                Toast.makeText(com.example.aicctv.Monitoring.this,
                        "동영상이 준비되었습니다.재생을 시작합니다.", Toast.LENGTH_SHORT).show();
                videoView.seekTo(0);
                videoView.start();
                // deleteVideo();
            }
        });

    }
    public void downloadVideo(StorageReference videoRef){
        try {
            videopath=new File(getFilesDir()+"/realtime");
            if(!videopath.exists()) {
                videopath.mkdir();
            }
            removeDir(videopath); //내부 저장소 내의 파일 모조리 삭제
            videoname=target+".mp4";
            localFile = new File(getFilesDir()+"/realtime",videoname);

            videoRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    // Local temp file has been created
                    Log.d("Success ","영상 다운로드 완료");
                    playVideo(videoname);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void deleteVideo() {
        //link에 속한 애들 length-1만큼 돌려서 삭제

        for (delete_count = 0; delete_count < link.length - 1; delete_count++) {
            storage2.getReferenceFromUrl("gs://aicctv-8f5ac.appspot.com").child("/00gpwls00/Video/" + link[delete_count] + ".h264").delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // removeValue 말고 setValue(null)도 삭제가능
                            childreference.child(link[delete_count_link++]).removeValue()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(link[delete_count_link2++], "파일,링크 삭제완료");

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(link[delete_count_link2++], "링크 삭제실패");
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(link[delete_count] + ".h264", "파일,링크 삭제실패");
                }
            });
        }
    }


}