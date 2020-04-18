package com.suvidha.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.suvidha.Models.NgoActivityModel;
import com.suvidha.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NgoAdapter extends RecyclerView.Adapter<NgoAdapter.MyHolder> {
    Context ctx;
    List<NgoActivityModel> list;
    public NgoAdapter(Context ctx, List<NgoActivityModel> list) {
        this.ctx = ctx;
        this.list = list;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.single_ngo_event,parent,false);
        return new MyHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {

        NgoActivityModel data = list.get(position);
       holder.ngo_address.setText(data.getAddress());
       holder.ngo_city.setText(data.getCity());
       String date=data.getDatetime().substring(data.getDatetime().indexOf(' ')+1)+" "+data.getDatetime().substring(0,data.getDatetime().indexOf(' '));
       //String date=data.getDatetime();
        //DateFormat formatter= new SimpleDateFormat("yyyy-MM-dd hh:mm");
       // formatter.setTimeZone(TimeZone.getTimeZone("Europe/London"));
       holder.ngo_address.setText(data.getAddress());
       holder.ngo_city.setText(data.getCity());
//       String date="At "+data.getDatetime().substring(data.getDatetime().indexOf('T')+1)+"\n"+data.getDatetime().substring(0,data.getDatetime().indexOf('T'));

       // Log.d("timewa",formatter.format(date));
        //System.out.println(formatter.format(date));
        holder.ngo_time.setText(date);
        holder.ngo_dec.setText(data.getDescription());
    }

    @Override
    public int getItemCount() {
        if(list == null) return 0;
        return list.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        TextView ngo_city,ngo_address,ngo_time,ngo_dec;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
           ngo_address=itemView.findViewById(R.id.ngo_address);
           ngo_city=itemView.findViewById(R.id.ngo_city);
           ngo_time=itemView.findViewById(R.id.ngo_time);
           ngo_dec=itemView.findViewById(R.id.ngo_desc);
        }
    }
}