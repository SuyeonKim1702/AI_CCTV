package com.example.aicctv;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.mp4parser.muxer.Edit;

public class Visitor_popup extends Activity {
    private static int change_name = 0;

    Visitor_photo v = new Visitor_photo();
    String photo_name= v.getPhotoName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.popup_activity);

        EditText txtText = findViewById(R.id.nameinput);
        Intent intent = getIntent();
        String data = intent.getStringExtra("data");
        txtText.setText(data);

        Button delete = findViewById(R.id.delete);
        Button confirm = findViewById(R.id.confirm);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                change_name = 1;
                Toast.makeText(getApplicationContext(), "사진이 삭제되었습니다.",Toast.LENGTH_SHORT).show();
                mOnClose(v);
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!data.equals(txtText.getText().toString())){
                    change_name = 1;
                    Toast.makeText(getApplicationContext(), "사진 위치가 변경되었습니다.",Toast.LENGTH_SHORT).show();
                }
                mOnClose(v);
            }
        });
    }

    public void mOnClose(View v){
        //데이터 전달하기
        Intent intent = new Intent();
        intent.putExtra("result", change_name);
        setResult(RESULT_OK, intent);
        finish();
    }

    public int getChange_name(){
        return change_name;
    }
}
