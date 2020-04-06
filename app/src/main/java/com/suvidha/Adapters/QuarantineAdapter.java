package com.suvidha.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.suvidha.Models.ReportModel;
import com.suvidha.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class QuarantineAdapter extends RecyclerView.Adapter<QuarantineAdapter.MyHolder> {
    public List<ReportModel> data;
    public Context ctx;
    public QuarantineAdapter(Context ctx, List<ReportModel> data) {
        this.ctx = ctx;
        this.data = data;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.item_report,parent,false);
        return new MyHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        ReportModel val = data.get(position);

        Date date1= null;
        try {
            date1 = new SimpleDateFormat("yyyy-mm-dd HH:MM:SS").parse(val.report_time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Date timestamp = date1;
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM, yyyy");
        String date = formatter.format(timestamp);
        formatter = new SimpleDateFormat("hh:mm a");
        String time =formatter.format(timestamp);
        holder.report_time.setText(ctx.getResources().getString(R.string.reported_on)+ date + ctx.getResources().getString(R.string.at) + time);
        if(val.location_error == 1){
            holder.quarantine_item.setBackgroundColor(Color.RED);
        }else{
            holder.quarantine_item.setBackgroundColor(Color.WHITE);
        }
        holder.report_error_text.setText((val.location_error == 0?"You were in quarantine location":"You were not in quarantine location"));
    }

    @Override
    public int getItemCount() {
        if(data == null) return 0;
        return data.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder{
        TextView report_time;
        LinearLayout quarantine_item;
        TextView report_error_text;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            report_time = itemView.findViewById(R.id.report_time);
            report_error_text = itemView.findViewById(R.id.report_error_txt);
            quarantine_item = itemView.findViewById(R.id.quarantine_item);
        }
    }
}
