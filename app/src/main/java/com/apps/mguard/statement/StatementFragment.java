package com.apps.mguard.statement;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteCantOpenDatabaseException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.apps.mguard.MonthDalesDate;
import com.apps.mguard.NumberTextWatcher;
import com.apps.mguard.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;


public class StatementFragment extends Fragment {
    BarChart barChart;
    ArrayList<BarEntry> barEntryArrayList;
    ArrayList<String> lableNames;
    ImageView btnBefore, btnNext;
    String myFormat = "yyyy년 MM월";    // 출력형식   2018년 11월
    SimpleDateFormat sdf=new SimpleDateFormat(myFormat, Locale.KOREA);
    TextView tvYearMonth;
    Calendar myCalendar = Calendar.getInstance();
    Context ct ;
    ArrayList<IBarDataSet> dataSets = new ArrayList<>();
    BarData barData;
    TextView tvSumIncome, tvSumExport;

    ArrayList<MonthDalesDate> monthDalesDateArrayList = new ArrayList<>();



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_statement, container, false);

        // 년월 보임
        tvYearMonth = (TextView)root.findViewById(R.id.tvYearMonth);
        sdf = new SimpleDateFormat(myFormat, Locale.KOREA);
        tvYearMonth.setText(sdf.format(myCalendar.getTime()));

        //xml 연결
        btnBefore=(ImageView)root.findViewById(R.id.btnBefore);
        btnNext=(ImageView)root.findViewById(R.id.btnNext);
        tvSumIncome=(TextView)root.findViewById(R.id.tvSumIncome);
        tvSumExport=(TextView)root.findViewById(R.id.tvSumExport);
        barChart=root.findViewById(R.id.barChart);

        fillMonthSales();
        drawChart();

        //이전월 버튼 클릭 시
        btnBefore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myCalendar.add(myCalendar.MONTH,-1);
                tvYearMonth.setText(sdf.format(myCalendar.getTime()));
                monthDalesDateArrayList.clear();
                fillMonthSales();
                drawChart();
            }
        });


        //다음월 버튼 클릭 시
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myCalendar.add(myCalendar.MONTH,+1);
                tvYearMonth.setText(sdf.format(myCalendar.getTime()));
                monthDalesDateArrayList.clear();
                fillMonthSales();
                drawChart();
            }
        });

        return root;

    }
    private void drawChart(){
        barEntryArrayList = new ArrayList<>();
        lableNames= new ArrayList<>();
        barEntryArrayList.clear();

        for(int i=0; i<monthDalesDateArrayList.size(); i++){
            String useStatus = monthDalesDateArrayList.get(i).getUseStatus();
            int amount = monthDalesDateArrayList.get(i).getAmount();
            barEntryArrayList.add(new BarEntry(i,amount));
            lableNames.add(useStatus);
        }



        BarDataSet barDataSet = new BarDataSet(barEntryArrayList,"수입 지출");
        barDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        Description description = new Description();
        description.setText("");
        barChart.setDescription(description);
        barData = new BarData(barDataSet);
        barChart.setData(barData);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(lableNames));

        //x축 설정정
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(lableNames.size());
        barChart.animateY(1500);
        barChart.invalidate();

        //y축 오른쪽 제거
        YAxis yAxisR = barChart.getAxisRight();
        yAxisR.setDrawLabels(false);
        yAxisR.setDrawAxisLine(false);
        yAxisR.setDrawGridLines(false);

        //라벨 위치
        Legend legend = barChart.getLegend();
        legend.setPosition(Legend.LegendPosition.ABOVE_CHART_RIGHT);

    }

    private void fillMonthSales(){

        try {
            SQLiteDatabase sqlDB=SQLiteDatabase.openDatabase(
                    "data/data/com.apps.mguard/databases/mguardDB",null,
                    SQLiteDatabase.OPEN_READONLY);

            //db자료 select
            Cursor cursorIncome, cursorExport;

            cursorIncome = sqlDB.rawQuery("select amount from AmountTBL " +
                    "where date like '"+tvYearMonth.getText().toString()+"%' and useStatus = '수입';",null);

            cursorExport = sqlDB.rawQuery("select amount from AmountTBL " +
                    "where date like '"+tvYearMonth.getText().toString()+"%' and useStatus = '지출';",null);

            monthDalesDateArrayList.clear();
            int incom=0, export=0;

            while (cursorIncome.moveToNext()){
                if(cursorIncome.getInt(0)==0){
                    incom=0;
                    tvSumIncome.setText("0");
                }
                incom += cursorIncome.getInt(0);

            }
            while (cursorExport.moveToNext()){
                if(cursorExport.getInt(0)==0){
                    export=0;
                    tvSumExport.setText("0");
                }
                export += cursorExport.getInt(0);

            }
            monthDalesDateArrayList.add(new MonthDalesDate("지출",export));
            monthDalesDateArrayList.add(new MonthDalesDate("수입",incom));

            tvSumExport.addTextChangedListener(new NumberTextWatcher(tvSumExport));
            tvSumIncome.addTextChangedListener(new NumberTextWatcher(tvSumIncome));
            tvSumExport.setText(Integer.toString(export));
            tvSumIncome.setText(Integer.toString(incom));
            Log.i("test","incom"+incom);
            Log.i("test","export"+export);

            cursorIncome.close();
            cursorExport.close();
            sqlDB.close();

        }catch (SQLiteCantOpenDatabaseException e){
            e.printStackTrace();
        }

    }

}