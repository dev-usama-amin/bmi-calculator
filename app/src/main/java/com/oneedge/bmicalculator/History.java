package com.oneedge.bmicalculator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class History extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<HistoryModel> data;
    private DataBaseHelper helper;
    private TextView no_result;
    private HistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        no_result = findViewById(R.id.no_result);
        recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        data = new ArrayList<>();
        helper = new DataBaseHelper(getApplicationContext());
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                finish();
            }
        });
        fetchData();
    }

    private void fetchData(){
        Cursor cursor = helper.getData();
        while (cursor.moveToNext()){
            HistoryModel model = new HistoryModel();
            model.setId(cursor.getString(cursor.getColumnIndex(DataBaseHelper.KEY_ID)));
            model.setBmi(cursor.getString(cursor.getColumnIndex(DataBaseHelper.BMI)));
            model.setBmr(cursor.getString(cursor.getColumnIndex(DataBaseHelper.BMR)));
            model.setDate(cursor.getString(cursor.getColumnIndex(DataBaseHelper.DATE)));
            data.add(model);
        }
        cursor.close();
        if (data.size()>0){
            no_result.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }else{
            no_result.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
        adapter = new HistoryAdapter(getApplicationContext(), data, new HistoryAdapter.onItemClick() {
            @Override
            public void onDelete(HistoryModel model, int pos) {
                helper.deleteData(model.getId());
                data.remove(pos);
                adapter.notifyDataSetChanged();
                if (data.size()>0){
                    no_result.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }else{
                    no_result.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
            }
        });
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
        finish();
    }
}