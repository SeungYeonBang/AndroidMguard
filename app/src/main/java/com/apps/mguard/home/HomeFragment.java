package com.apps.mguard.home;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.apps.mguard.DatePic;
import com.apps.mguard.MyDBHelper;
import com.apps.mguard.NumberEdtWactcher;
import com.apps.mguard.R;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    ListView listView1;
    MyListAdapter adapter;
    List<ItemData> data;
    String title[] = {"오늘 현재 사용금","어제","이번 달 현재까지"};
    ImageButton ibPlus,ibSetting;
    String myFormat = "yyyy년 MM월 dd일";    // 출력형식   2018년 11월 28일
    Calendar myCalendar = Calendar.getInstance();
    TextView tvCal, tvPersent, tvRemain, tvOneUse, tvDay,tvInput;
    Context ct;
    View dialogView;
    EditText edtBudget;
    Button btnSelect;
    String dbValue="0";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        ct = container.getContext();

        tvCal = root.findViewById(R.id.tvCal);
        tvPersent = root.findViewById(R.id.tvPersent);
        tvRemain = root.findViewById(R.id.tvRemain);
        tvOneUse = root.findViewById(R.id.tvOneUse);
        tvDay=root.findViewById(R.id.tvDay);

        dialogView = (View)View.inflate(ct,R.layout.setting_dialog,null);
        edtBudget=dialogView.findViewById(R.id.edtBudget);
        tvInput=dialogView.findViewById(R.id.tvInput);
        btnSelect=dialogView.findViewById(R.id.btnSelect);
        ibPlus =root.findViewById(R.id.ibPlus);
        ibSetting=root.findViewById(R.id.ibSetting);

        updateLabel();

        // ListView 어뎁터 연결
        data = new ArrayList<>();

        listView1 = (ListView) root.findViewById(R.id.listView1);
        adapter = new MyListAdapter(getContext(),data);
        adapter.notifyDataSetChanged();
        listView1.setAdapter(adapter);

        dbDisposal();
        dbBudget();
        Log.i("테스트","create메서드 dbBudget() 후 값"+dbValue);
        dbSumSelect();
        Log.i("테스트","create메서드 dbSumSelect() 후 값"+dbValue);
        tvRemain.setText(dbValue);

        //+이미지 버튼 클릭 시
        ibPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), DatePic.class);
                startActivity(intent);
            }
        });

        //셋팅 버튼 클릭 시
        ibSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ct);
                builder.setIcon(R.drawable.ic_logo);
                builder.setTitle("예산 설정");
                builder.setMessage("한달 예산 금액을 입력 해 주세요");

                edtBudget.addTextChangedListener(new NumberEdtWactcher(edtBudget));

                if(edtBudget.getText().toString().equals("")){
                    tvInput.setText("0");
                }

                btnSelect.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tvInput.setText(edtBudget.getText().toString());
                        dbValue = tvInput.getText().toString();
                        Log.i("테스트","tvInput 값 : "+dbValue);
                        edtBudget.setText("");
                    }
                });

                builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dbSumSelect();
                        dbBudget();
                        tvRemain.setText(dbValue);
                        Toast.makeText(getContext(),"설정이 완료되었어요!",Toast.LENGTH_SHORT).show();

                    }
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Toast.makeText(getContext(),"예산 설정이 취소 되었어요!",Toast.LENGTH_SHORT).show();

                    }
                });

                builder.setView(dialogView);

                AlertDialog dialog = builder.create();

                ViewGroup dialogParentView = (ViewGroup) dialogView.getParent();

                if(dialogParentView !=null){
                    dialogParentView.removeView(dialogView);
                }

                dialog.setCancelable(false);
                dialog.show();

            }
        });

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        data.clear();
        dbDisposal();
        dbSumSelect();
        Log.i("테스트","onReseum 메서드 Select 메서드 호출 후 : "+dbValue);
        adapter.notifyDataSetChanged();


    }

    //날짜 출력 메서드
    private void updateLabel() {

        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.KOREA);
        tvCal.setText(sdf.format(myCalendar.getTime()));
    }

    //DB연결 메서드
    private void dbDisposal(){

        try {

            Calendar cal = new GregorianCalendar();
            cal.add(Calendar.DATE,-1);
            SimpleDateFormat dSdf = new SimpleDateFormat("yyyy년 MM월 dd일", Locale.KOREA);
            String yesterday = dSdf.format(cal.getTime());

            String myFormat = "yyyy년 MM월";
            SimpleDateFormat sdf=new SimpleDateFormat(myFormat, Locale.KOREA);
            String month = sdf.format(myCalendar.getTime());

            String selectToday = "SELECT amount FROM AmountTBL where useStatus = '지출'" +
                    " and date = '"+ tvCal.getText().toString()+"';";
            String selectYesterday = "SELECT amount FROM AmountTBL where useStatus = '지출'" +
                    " and date = '"+ yesterday+"';";
            String selectMonth = "SELECT amount FROM AmountTBL where useStatus = '지출' " +
                    "and date like '"+ month+"%';";

            MyDBHelper myDB = new MyDBHelper(ct);
            SQLiteDatabase sqlDB= myDB.getReadableDatabase();

            Cursor cursorToday, cursorYesterday, cursorMonth;
            cursorToday=sqlDB.rawQuery(selectToday,null);
            cursorYesterday=sqlDB.rawQuery(selectYesterday,null);
            cursorMonth=sqlDB.rawQuery(selectMonth,null);


                int todayAmount, yesterdayAmount, monthAmount;
                int resultT =0, resultY=0, resultM=0;
                ItemData itemData= new ItemData("","");

                while (cursorToday.moveToNext()){
                    todayAmount = cursorToday.getInt(0);
                    resultT += todayAmount;
                }
                data.add(new ItemData(title[0],NumberFormat.getInstance().format(resultT)+"￦"));

                while(cursorYesterday.moveToNext()){
                    yesterdayAmount = cursorYesterday.getInt(0);
                    resultY += yesterdayAmount;
                }
                data.add(new ItemData(title[1],NumberFormat.getInstance().format(resultY)+"￦"));

                while(cursorMonth.moveToNext()){
                    monthAmount = cursorMonth.getInt(0);
                    resultM += monthAmount;
                }
                data.add(new ItemData(title[2],NumberFormat.getInstance().format(resultM)+"￦"));

                cursorToday.close();
                cursorYesterday.close();
                cursorMonth.close();
                sqlDB.close();

        }catch (SQLiteException e){
            e.printStackTrace();
        }

    }

    private void dbBudget(){

        SQLiteDatabase sqlDB=SQLiteDatabase.openDatabase(
                "data/data/com.apps.mguard/databases/mguardDB",null,
                SQLiteDatabase.OPEN_READONLY);

        String myFormat = "yyyy년 MM월";
        SimpleDateFormat sdf=new SimpleDateFormat(myFormat, Locale.KOREA);
        String month = sdf.format(myCalendar.getTime());
        String selectMonth = "SELECT sum(amount) FROM AmountTBL where useStatus = '지출' " +
                "and date like '"+ month+"%';";

        int  monthAmount=0;

        Cursor cursorRemain;
        cursorRemain = sqlDB.rawQuery(selectMonth,null);

        while (cursorRemain.moveToNext()){
            monthAmount = cursorRemain.getInt(0);
            Log.i("테스트","resultM while 값 : "+monthAmount);
        }

        String SELECT_QUERY = "select * from SumTBL;";
        Cursor cursor;
        cursor=sqlDB.rawQuery(SELECT_QUERY,null);

        String selectJoin = " select (select budgetSum from SumTBL)-(select sum(amount) from AmountTBL" +
                " where AmountTBL.date like '"+ month+"%' and AmountTBL.useStatus='지출') as 'myvalue';";

        Cursor cursor1 = sqlDB.rawQuery(selectJoin,null);

        while (cursor.moveToNext()){
            Log.i("테스트","while 수행1");
            int remainingAmount;

            if(cursor.getInt(0)-monthAmount < 0){
                remainingAmount = 0;
            }else {

                remainingAmount= cursor.getInt(0)-monthAmount;
                Log.i("테스트","cursor.getInt(0) : "+cursor.getInt(0));
                Log.i("테스트"," remainingAmount : "+remainingAmount);
            }
            dbValue= NumberFormat.getInstance().format(remainingAmount);
            Log.i("테스트"," dbValue text 값 : "+dbValue);
            dbSumUpdate();
            Log.i("테스트","while 수행2 : "+cursor.getCount());
        }

        cursorRemain.close();
        cursor.close();
        sqlDB.close();

    }

    private void dbSumInsert(){

        SQLiteDatabase sqlDB=SQLiteDatabase.openDatabase(
                "data/data/com.apps.mguard/databases/mguardDB",null, SQLiteDatabase.OPEN_READWRITE);

        sqlDB.execSQL("insert into SumTBL values("+Integer.parseInt(dbValue)+");");

        Log.i("테스트","dbSumInsert() 메서드 호출 : "+dbValue);

            sqlDB.close();
    }
    private void dbSumUpdate(){

        String removeComma =dbValue.replace(",","");
        Log.i("테스트","dbUpdate전 value값 : "+dbValue);

        SQLiteDatabase sqlDB=SQLiteDatabase.openDatabase(
                "data/data/com.apps.mguard/databases/mguardDB",null,
                SQLiteDatabase.OPEN_READWRITE);
        sqlDB.execSQL("update SumTBL set budgetsum ="+Integer.parseInt(removeComma)+";");

        Log.i("테스트","dbUpdate후 value값 : "+Integer.parseInt(removeComma));

        sqlDB.close();
    }

    private void dbSumSelect(){
        SQLiteDatabase sqlDB=SQLiteDatabase.openDatabase(
                "data/data/com.apps.mguard/databases/mguardDB",null,
                SQLiteDatabase.OPEN_READONLY);

        String SELECT_QUERY = "select * from SumTBL;";
        Cursor cursor;
        cursor=sqlDB.rawQuery(SELECT_QUERY,null);

        if(cursor.getCount()==0){
            dbSumInsert();
            Log.i("테스트","select문 Insert 실행 : " + dbValue);
        }else {
            dbSumUpdate();
            Log.i("테스트","select문 update 실행 : " + dbValue);
        }

        while (cursor.moveToNext()){
            dbValue = NumberFormat.getInstance().format(cursor.getInt(0));
            Log.i("테스트","select문 실행 : " + dbValue);
        }
        cursor.close();
        sqlDB.close();
    }

}