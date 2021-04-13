package com.apps.mguard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.apps.mguard.home.HomeFragment;

public class CreateList extends AppCompatActivity {
    TextView tvResultAmount, tvSelect, tvDate2;
    String getAmount, getSelect, getDate;
    Spinner spPaymentmethod;
    String[] payments ={"현금","신용카드","체크카드"};
    ArrayAdapter<String> adapter;
    Button btnCompletion, btnHome;
    EditText edtContent;
    MyDBHelper myDB;
    SQLiteDatabase sqlStart;
    LinearLayout createLinear;
    Amount amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_list);

        ActionBar bar = getSupportActionBar();
        bar.setTitle("");
        bar.setTitle("뒤로가기");
        bar.setDisplayHomeAsUpEnabled(true);

        tvSelect=findViewById(R.id.tvSelect);
        tvDate2=findViewById(R.id.tvDate2);
        tvResultAmount=findViewById(R.id.tvResultAmount);
        spPaymentmethod=findViewById(R.id.spPaymentMethod);
        btnCompletion=findViewById(R.id.btnCompletion);
        btnHome=findViewById(R.id.btnHome);
        edtContent=findViewById(R.id.edtContent);
        createLinear=findViewById(R.id.createLinear);
        amount=new Amount();

        myDB = new MyDBHelper(this);

        Intent gIntent=getIntent();
        getAmount =gIntent.getStringExtra("Amount");
        getSelect=gIntent.getStringExtra("Select");
        getDate=gIntent.getStringExtra("Date");

        tvResultAmount.setText(getAmount);
        tvSelect.setText(getSelect);
        tvDate2.setText(getDate);
        Log.i("test","tvSelect 내역"+ tvDate2.getText().toString());
        Log.i("test","tvSelect 내역"+ tvSelect.getText().toString());

        if(getSelect.equals("수입")){
            spPaymentmethod.setVisibility(View.INVISIBLE);
            createLinear.setVisibility(View.INVISIBLE);
            edtContent.setHint("입금 내용을 작성 해 주세요");
        }else if(getSelect.equals("지출")){
            spPaymentmethod.setVisibility(View.VISIBLE);
            createLinear.setVisibility(View.VISIBLE);
            edtContent.setHint("출금 내역을 작성 해 주세요");
        }

        // 어뎁터 연결
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,payments);
        spPaymentmethod.setAdapter(adapter);

        //완료 버튼 클릭 시 (DB 레코드 추가)
        btnCompletion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getAmount.equals("") || getSelect.equals("")) {
                    showToast("자료를 저장하지 못하였습니다.");
                }else {
                    String removeComma = getAmount.replace(",","");
                    sqlStart=myDB.getWritableDatabase();
                    sqlStart.execSQL("insert into AmountTBL values('"+
                            getSelect+"',"+
                            Integer.parseInt(removeComma)+",'"+
                            getDate +"','"+
                            edtContent.getText().toString()+"','"+
                            spPaymentmethod.getSelectedItem().toString()+"');");
                    sqlStart.close();
                    showToast("저장완료!");
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    amount.finish();
                    finish();

                }

            }
        });

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CreateList.this);
                builder.setTitle("주의");
                builder.setMessage("작성이 얼마 남지 않았어요! 그래도 그만 두시겠어요?");
                builder.setPositiveButton("그만 작성 할래요", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showToast("작성을 취소 하였습니다.");
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        amount.finish();
                        finish();

                    }
                });
                builder.setNegativeButton("계속 작성 할 거예요!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showToast("계속 작성을 해 주세요");
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });


    }

    //토스트 메서드
    void showToast(String msg){
        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                Intent intent = new Intent(getApplicationContext(),Amount.class);
                intent.putExtra("date",tvDate2.getText().toString());
                setResult(RESULT_OK,intent);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}