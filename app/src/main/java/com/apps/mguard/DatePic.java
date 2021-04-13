package com.apps.mguard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DatePic extends AppCompatActivity {

    CalendarView calenderCho;
    Button btnCalCho;
    TextView tvCalCho;
    String myFormat = "yyyy년 MM월 dd일";    // 출력형식   2018년 11월 28일
    Calendar myCalendar = Calendar.getInstance();
    int cYear, cMonth, cDayOfMonth;

    final CalendarView.OnDateChangeListener myCalender = new CalendarView.OnDateChangeListener() {
        @Override
        public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_pic);

        calenderCho=findViewById(R.id.calenderCho);
        btnCalCho=findViewById(R.id.btnCalCho);
        tvCalCho=findViewById(R.id.tvCalCho);

        ActionBar bar = getSupportActionBar();
        bar.setTitle("");
        bar.setTitle("뒤로가기");
        bar.setDisplayHomeAsUpEnabled(true);

        updateLabel();

        //캘린더 선택 시
        calenderCho.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                cYear =year;
                cMonth=month;
                cDayOfMonth=dayOfMonth;
                myCalendar.set(Calendar.YEAR, cYear);
                myCalendar.set(Calendar.MONTH, cMonth);
                myCalendar.set(Calendar.DAY_OF_MONTH, cDayOfMonth);
                updateLabel();

            }
        });

        //선택버튼 선택 시
        btnCalCho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("test","tvCalCho 내역"+tvCalCho.getText().toString());
                //Toast.makeText(getApplicationContext(),"test : "+tvCalCho.getText().toString(),Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getApplicationContext(), Amount.class);
                intent.putExtra("Date",tvCalCho.getText().toString());
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateLabel() {

        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.KOREA);
        tvCalCho.setText(sdf.format(myCalendar.getTime()));
    }

}