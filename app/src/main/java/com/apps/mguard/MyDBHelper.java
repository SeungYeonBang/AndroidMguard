package com.apps.mguard;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;
import java.util.ArrayList;

//DB클래스
public class MyDBHelper extends SQLiteOpenHelper {

    //  DB생성 생성자
    public MyDBHelper(@Nullable Context context) {

        super(context, "mguardDB", null, 1);
    }

    //테이블 생성 메서드
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table AmountTBL(useStatus text not null,  amount integer not null, " +
                "date text not null, content text, Paymentmethod text);");

        db.execSQL("create table SumTBL(budgetsum integer);");
    }

    // 초기화 메서드
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}