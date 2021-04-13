package com.apps.mguard.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.apps.mguard.R;

import java.util.List;

public class MyListAdapter extends BaseAdapter {
    // 필드
    Context context; //새로 생성된 객체가 지금 어떤 일이 일어나고 있는지 알 수 있도록 하는 객체 액티비티 객체는 컨텍스트 객체를 상속받습니다
    LayoutInflater inflater; //inflate 를 생성하기 위해 필요한 도구
    List<ItemData> itemData;

    // 생성자
    public MyListAdapter(Context context, List<ItemData> itemData) {
        this.context=context;
        this.itemData = itemData;
        this.inflater = LayoutInflater.from(context);
    }
    //메서드
    @Override
    public int getCount() {
        return itemData.size();
    }

    @Override
    public Object getItem(int position) {
        return itemData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView =convertView;
        if (listItemView==null){
            listItemView = inflater.inflate(R.layout.listview1,null);
        }
        TextView itemTitle = listItemView.findViewById(R.id.itemTitle);
        TextView itemContent = listItemView.findViewById(R.id.itemAmount);
        ItemData iData = itemData.get(position);
        itemTitle.setText(iData.getTitle());
        itemContent.setText(iData.getContent());
        return listItemView;
    }
}
