package com.apps.mguard.detaillist;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteCantOpenDatabaseException;
import android.database.sqlite.SQLiteDatabase;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.apps.mguard.MainActivity;
import com.apps.mguard.MonthDalesDate;
import com.apps.mguard.NumberTextWatcher;
import com.apps.mguard.R;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class DetailListFragment extends Fragment {
    ListView detailList;
    MyListAdapter2 adapter2;
    List<ListData> data;
    Context ct;
    TextView tvMonth, tvNoList,tvDSumExport,tvDSumIncome;
    ImageView btnBeforeM, btnNextM;
    String myFormat = "yyyy년 MM월";    // 출력형식   2018년 11월
    SimpleDateFormat sdf=new SimpleDateFormat(myFormat, Locale.KOREA);
    Calendar myCalendar = Calendar.getInstance();
    ImageView imgDiaClose;
    TextView tvDiaDate,tvDiaSelect,tvDiaResult,tvDiaContenttvDiaPaymentMethod,tvDiaContent;
    View dialogView;
    String result;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_detail_list, container, false);


        tvMonth=root.findViewById(R.id.tvMonth);
        tvNoList=root.findViewById(R.id.tvNoList);
        btnBeforeM=root.findViewById(R.id.btnBeforeM);
        btnNextM=root.findViewById(R.id.btnNextM);
        tvDSumExport=root.findViewById(R.id.tvDSumExport);
        tvDSumIncome=root.findViewById(R.id.tvDSumIncome);

        if(getArguments() != null){
            result=getArguments().getString("fragment");
        }


        // 년월 보임
        sdf = new SimpleDateFormat(myFormat, Locale.KOREA);
        tvMonth.setText(sdf.format(myCalendar.getTime()));

        // 어뎁터 연결
        ct = container.getContext();
        data=new ArrayList<ListData>();
        dbDisposal();
        fillMonthSales();

        detailList=(ListView)root.findViewById(R.id.detailList);
        adapter2= new MyListAdapter2(ct,data);
        adapter2.notifyDataSetChanged();
        detailList.setAdapter(adapter2);

        //이전월 버튼 클릭 시
        btnBeforeM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myCalendar.add(myCalendar.MONTH,-1);
                tvMonth.setText(sdf.format(myCalendar.getTime()));
                adapter2= new MyListAdapter2(ct,data);
                detailList.setAdapter(adapter2);
                data.clear();
                dbDisposal();
                fillMonthSales();

            }
        });


        //다음월 버튼 클릭 시
        btnNextM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myCalendar.add(myCalendar.MONTH,+1);
                tvMonth.setText(sdf.format(myCalendar.getTime()));
                adapter2= new MyListAdapter2(ct,data);
                detailList.setAdapter(adapter2);
                data.clear();
                dbDisposal();
                fillMonthSales();

            }
        });

        //리스트뷰 클릭 시 다이얼로그
        detailList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ct);
                dialogView = (View)View.inflate(ct,R.layout.detail_diallog,null);
                imgDiaClose=dialogView.findViewById(R.id.imgDiaClose);
                tvDiaSelect=dialogView.findViewById(R.id.tvDiaSelect);
                tvDiaContenttvDiaPaymentMethod=dialogView.findViewById(R.id.tvDiaPaymentMethod);
                tvDiaDate=dialogView.findViewById(R.id.tvDiaDate);
                tvDiaResult=dialogView.findViewById(R.id.tvDiaResult);
                tvDiaContent=dialogView.findViewById(R.id.tvDiaContent);

                tvDiaDate.setText(data.get(position).getDate());
                tvDiaContent.setText(data.get(position).getContent());
                tvDiaContenttvDiaPaymentMethod.setText(data.get(position).getPaymentmethod());
                tvDiaSelect.setText(data.get(position).getUseStatus());
                tvDiaResult.setText(NumberFormat.getInstance().format(data.get(position).getAmount()));

                builder.setView(dialogView);

                AlertDialog dialog = builder.create();
                dialog.setCancelable(false);
                dialog.show();
                imgDiaClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

            }
        });


        return root;
    }
    //db메서드
    private void dbDisposal(){
        //Toast.makeText(ct, "test", Toast.LENGTH_SHORT).show();

        try {
            String SELECT_QUERY = "SELECT * FROM AmountTBL where date like '"+
                    tvMonth.getText().toString()+"%' order by date desc;";


            SQLiteDatabase sqlDB=SQLiteDatabase.openDatabase(
                    "data/data/com.apps.mguard/databases/mguardDB",null,
                    SQLiteDatabase.OPEN_READONLY);

            Cursor cursor;
            cursor=sqlDB.rawQuery(SELECT_QUERY,null);

            while(cursor.moveToNext()){

                data.add(new ListData(
                        cursor.getString(0),
                        cursor.getInt(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4)));
            }

            cursor.close();
            sqlDB.close();

            if(data.size()==0){
                tvNoList.setVisibility(View.VISIBLE);
            }else {
                tvNoList.setVisibility(View.GONE);
            }

        }catch (SQLiteCantOpenDatabaseException e){
            e.printStackTrace();
        }

    }
    //월 합계 DB메서드
    private void fillMonthSales(){

        try {
            SQLiteDatabase sqlDB=SQLiteDatabase.openDatabase(
                    "data/data/com.apps.mguard/databases/mguardDB",null,
                    SQLiteDatabase.OPEN_READONLY);

            //db자료 select
            Cursor cursorIncome, cursorExport;

            cursorIncome = sqlDB.rawQuery("select amount from AmountTBL " +
                    "where date like '"+tvMonth.getText().toString()+"%' and useStatus = '수입';",null);

            cursorExport = sqlDB.rawQuery("select amount from AmountTBL " +
                    "where date like '"+tvMonth.getText().toString()+"%' and useStatus = '지출';",null);

            int incom=0, export=0;

            while (cursorIncome.moveToNext()){
                if(cursorIncome.getInt(0)==0){
                    incom=0;
                    tvDSumIncome.setText("0");
                }
                incom += cursorIncome.getInt(0);

            }
            while (cursorExport.moveToNext()){
                if(cursorExport.getInt(0)==0){
                    export=0;
                    tvDSumExport.setText("0");
                }
                export += cursorExport.getInt(0);

            }

            tvDSumExport.addTextChangedListener(new NumberTextWatcher(tvDSumExport));
            tvDSumIncome.addTextChangedListener(new NumberTextWatcher(tvDSumIncome));
            tvDSumExport.setText(Integer.toString(export));
            tvDSumIncome.setText(Integer.toString(incom));


            cursorIncome.close();
            cursorExport.close();
            sqlDB.close();

        }catch (SQLiteCantOpenDatabaseException e){
            e.printStackTrace();
        }

    }

}