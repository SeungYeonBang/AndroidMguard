package com.apps.mguard;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Amount extends AppCompatActivity {
    Button btnCreateList;
    Spinner spinner1;
    TextView tvDate,tvAmount,tvInequ;
    ArrayAdapter<String> adapter;
    String useStatus[] = {"지출","수입"};
    String getDate;
    ImageButton btnCancle;
    Button[] numButtons = new Button[10];
    int[] numBtnIds={R.id.btnNum0,R.id.btnNum1,R.id.btnNum2,R.id.btnNum3,R.id.btnNum4,R.id.btnNum5,
            R.id.btnNum6,R.id.btnNum7,R.id.btnNum8,R.id.btnNum9};
    String num1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amount);

        ActionBar bar = getSupportActionBar();
        bar.setTitle("");
        bar.setTitle("뒤로가기");
        bar.setDisplayHomeAsUpEnabled(true);

        btnCreateList=findViewById(R.id.btnCreateList);
        spinner1=findViewById(R.id.spinner1);
        tvAmount=findViewById(R.id.tvAmount);
        tvInequ=findViewById(R.id.tvInequ);
        tvDate=findViewById(R.id.tvDate);
        btnCancle=findViewById(R.id.btnCancle);
        for (int i = 0; i < numBtnIds.length; i++) {
            numButtons[i] = findViewById(numBtnIds[i]);
        }

        Intent gIntent=getIntent();
        getDate=gIntent.getStringExtra("Date");
        tvDate.setText(getDate);


        // 어뎁터 연결
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,useStatus);
        spinner1.setAdapter(adapter);

        // spinner 선택 시
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        tvAmount.setText("");
                        tvInequ.setText("-");
                        break;
                    case 1:
                        tvAmount.setText("");
                        tvInequ.setText("+");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // 배열 버튼 클릭 시
        for (int i = 0; i < numBtnIds.length; i++) {
            final int index;
            index =i;
            numButtons[index].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    num1 = tvAmount.getText().toString()+numButtons[index].getText().toString();
                    tvAmount.setText(num1);
                }
            });
        }

        Log.i("test","num1 내역"+num1);
        //Toast.makeText(getApplicationContext(),"test : "+num1,Toast.LENGTH_LONG).show();

        //숫자 , 하기
        tvAmount.addTextChangedListener(new NumberTextWatcher(tvAmount));



        // 취소 버튼 클릭 시
        btnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvAmount.setText("");
            }
        });

        //목록 작성 버튼 클릭 시
        btnCreateList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (tvAmount.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(),"금액을 넣어 주세요",Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent = new Intent(getApplicationContext(), CreateList.class);
                    intent.putExtra("Amount",tvAmount.getText().toString());
                    intent.putExtra("Select",spinner1.getSelectedItem().toString());
                    intent.putExtra("Date",tvDate.getText().toString());
                    startActivityForResult(intent,100);
                }

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                Intent intent = new Intent(getApplicationContext(),DatePic.class);
                startActivity(intent);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==100){
            if(resultCode==RESULT_OK) {
                tvDate.setText(data.getStringExtra("date"));
                tvAmount.setText("");
            }
        }
    }

}