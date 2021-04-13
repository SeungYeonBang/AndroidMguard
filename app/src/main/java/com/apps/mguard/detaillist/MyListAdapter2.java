package com.apps.mguard.detaillist;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.apps.mguard.R;

import java.text.NumberFormat;
import java.util.List;

public class MyListAdapter2 extends BaseAdapter {
    // 필드
    Context context; //새로 생성된 객체가 지금 어떤 일이 일어나고 있는지 알 수 있도록 하는 객체 액티비티 객체는 컨텍스트 객체를 상속받습니다
    LayoutInflater inflater; //inflate 를 생성하기 위해 필요한 도구
    List<ListData> listData;



    public MyListAdapter2(Context context, List<ListData> listData) {
        this.context = context;
        this.listData = listData;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return listData.size();
    } //크기만큼 리스트뷰를 뿌려줌

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listItemView =convertView;

        try {

            if (listItemView==null){
                listItemView = inflater.inflate(R.layout.detail_listview,null);
            }
            TextView tvListAmount = listItemView.findViewById(R.id.tvListAmount);
            TextView tvListDate = listItemView.findViewById(R.id.tvListDate);
            TextView tvListContent = listItemView.findViewById(R.id.tvListContent);
            TextView tvListPayMethod = listItemView.findViewById(R.id.tvListPayMethod);

            ListData lData = listData.get(position);

            if(lData.getUseStatus().equals("수입")){
                tvListAmount.setTextColor(Color.BLUE);
            }else {
                tvListAmount.setTextColor(Color.RED);
            }

            tvListAmount.setText(NumberFormat.getInstance().format(lData.getAmount()));
            tvListDate.setText(lData.getDate());
            tvListContent.setText(lData.getContent());
            tvListPayMethod.setText(lData.getPaymentmethod());

        }catch (IndexOutOfBoundsException e){
            Toast.makeText(context, "자료를 가져올 수 없습니다",Toast.LENGTH_SHORT).show();

        }

        return listItemView;
    }

}
