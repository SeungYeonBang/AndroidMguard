package com.apps.mguard.claender;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteCantOpenDatabaseException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.apps.mguard.MyDBHelper;
import com.apps.mguard.R;
import com.apps.mguard.detaillist.ListData;
import com.apps.mguard.detaillist.MyListAdapter2;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CalenderFragment extends Fragment {
    ListView amountList;
    MyListAdapter2 adapter;
    List<ListData> data;
    TextView tvCalResult, tvSumE, tvSumI, tvNoCal;
    CalendarView mainCalenderView;
    Context ct;
    String myFormat = "yyyy년 MM월 dd일";    // 출력형식   2018년 11월 28일
    Calendar myCalendar = Calendar.getInstance();
    int cYear, cMonth, cDayOfMonth;
    SimpleDateFormat sdf;
    int pos;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_calender, container, false);

        amountList = (ListView) root.findViewById(R.id.amountList);
        tvCalResult = (TextView) root.findViewById(R.id.tvCalResult);
        tvSumE = (TextView) root.findViewById(R.id.tvSumE);
        tvSumI = (TextView) root.findViewById(R.id.tvSumI);
        mainCalenderView = (CalendarView) root.findViewById(R.id.mainCalenderView);
        tvNoCal=root.findViewById(R.id.tvNoCal);
        ct = container.getContext();
        sdf = new SimpleDateFormat(myFormat, Locale.KOREA);
        tvCalResult.setText(sdf.format(myCalendar.getTime()));

        // ListView 어뎁터 연결
        data=new ArrayList<ListData>();
        dbDisposal();

        adapter= new MyListAdapter2(ct,data);
        adapter.notifyDataSetChanged();
        amountList.setAdapter(adapter);

        if(adapter.getCount()==0){
            tvCalResult.setText(sdf.format(myCalendar.getTime()));
            adapter.notifyDataSetChanged();
            tvNoCal.setVisibility(View.VISIBLE);
            tvSumI.setText("₩0");
            tvSumE.setText("₩0");
        }
        if(adapter.getCount()>0){
            data.clear();
            adapter.notifyDataSetChanged();
            tvNoCal.setVisibility(View.GONE);
            dbDisposal();
        }

        //캘린더뷰 날짜 변경 시
        mainCalenderView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {

                cYear =year;
                cMonth=month;
                cDayOfMonth=dayOfMonth;
                myCalendar.set(Calendar.YEAR, cYear);
                myCalendar.set(Calendar.MONTH, cMonth);
                myCalendar.set(Calendar.DAY_OF_MONTH, cDayOfMonth);
                tvCalResult.setText(sdf.format(myCalendar.getTime()));
                adapter.notifyDataSetChanged();
                dbDisposal();

                if(adapter.getCount()>0){
                    data.clear();
                    adapter.notifyDataSetChanged();
                    tvNoCal.setVisibility(View.GONE);
                    dbDisposal();
                }

                if(adapter.getCount()==0){
                    tvCalResult.setText(sdf.format(myCalendar.getTime()));
                    adapter.notifyDataSetChanged();
                    tvNoCal.setVisibility(View.VISIBLE);
                    tvSumI.setText("₩0");
                    tvSumE.setText("₩0");
                }
                Log.i("test","dapter : "+adapter.getCount());
                //Toast.makeText(getContext(),"test : "+adapter.getCount(),Toast.LENGTH_SHORT).show();
            }
        });

        amountList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                AlertDialog.Builder builder = new AlertDialog.Builder(ct);
                builder.setTitle("항목 삭제");
                builder.setMessage("리스트를 삭제하시겠습니까?");
                builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pos = position;
                        int posIn = amountList.getCheckedItemPosition();
                        Log.i("test","msg : "+posIn);
                        Log.i("test","msg : "+ListView.INVALID_POSITION);

                        dbDelete();
                        data.remove(position);
                        amountList.clearChoices();
                        adapter.notifyDataSetChanged();
                        data.clear();
                        dbDisposal();
                        adapter.notifyDataSetChanged();

                        Toast.makeText(getContext(),"삭제가 완료되었습니다",Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getContext(),"삭제가 취소되었습니다",Toast.LENGTH_SHORT).show();

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();



                return false;
            }
        });

        return root;
    }

    //DB연결(select)
    public void dbDisposal(){

        try {

             String SELECT_QUERY = "SELECT * FROM AmountTBL where date = '"+
                tvCalResult.getText().toString()+"' order by date desc;";

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

                int sumI=0, sumE=0;

                if(cursor.getString(0).equals("지출")){

                    for(int i=0; i<data.size(); i++){
                        sumE+=data.get(i).getAmount();
                    }

                    tvCalResult.setText(sdf.format(myCalendar.getTime()));
                    tvSumE.setText(NumberFormat.getCurrencyInstance(Locale.KOREA).format(sumE));
                }
                if(cursor.getString(0).equals("수입")){

                    for(int i=0; i<data.size(); i++){
                        sumI+=data.get(i).getAmount();
                    }

                    tvCalResult.setText(sdf.format(myCalendar.getTime()));
                    tvSumI.setText(NumberFormat.getCurrencyInstance(Locale.KOREA).format(sumI));
                }

            }

            cursor.close();
            sqlDB.close();

        }catch (SQLiteCantOpenDatabaseException e){
            e.printStackTrace();
        }

    }
    public void dbDelete(){
        String deleteQuert="delete from AmountTBL where useStatus='"+data.get(pos).getUseStatus()+
                "'and amount="+data.get(pos).getAmount()+
                " and date='"+tvCalResult.getText().toString()+
                "' and content='"+data.get(pos).getContent()+
                "' and Paymentmethod='"+data.get(pos).getPaymentmethod()+"';";

        SQLiteDatabase sqlDB=SQLiteDatabase.openDatabase(
                "data/data/com.apps.mguard/databases/mguardDB",null,
                SQLiteDatabase.OPEN_READWRITE);
        sqlDB.execSQL(deleteQuert);
        sqlDB.close();
    }

}