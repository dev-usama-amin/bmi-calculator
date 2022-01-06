package com.oneedge.bmicalculator;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zerobranch.layout.SwipeLayout;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyViewHolder> {

    private Context context;
    private List<HistoryModel> data;
    private DataBaseHelper helper;
    private HistoryAdapter.onItemClick onItemClick;
    private static final String TAG = "HistoryAdapter";

    public HistoryAdapter(Context context, List<HistoryModel> data,onItemClick onItemClick) {
        this.context = context;
        this.data = data;
        this.onItemClick = onItemClick;
        helper = new DataBaseHelper(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.history_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        final HistoryModel model = data.get(position);
        holder.bmi.setText("BMI = "+model.getBmi());
        holder.bmr.setText("BMR = "+model.getBmr());
        holder.date.setText(model.getDate());
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!holder.swipe_layout.isClosed()){
                    onItemClick.onDelete(model,holder.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView bmi,bmr,date;
        private ImageView delete;
        private SwipeLayout swipe_layout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            bmi = itemView.findViewById(R.id.bmi);
            bmr = itemView.findViewById(R.id.bmr);
            date = itemView.findViewById(R.id.date);
            delete = itemView.findViewById(R.id.right_view);
            swipe_layout = itemView.findViewById(R.id.swipe_layout);
        }
    }

    public interface onItemClick{
        void onDelete(HistoryModel model,int pos);
    }
}
