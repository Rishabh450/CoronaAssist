package com.suvidha.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.suvidha.Activities.ScanPassActivity;
import com.suvidha.Models.Pass;
import com.suvidha.R;

import java.util.ArrayList;
import java.util.List;


public class PassAdapter extends RecyclerView.Adapter<PassViewHolder> {

    Context context;
    List<Pass> passes = new ArrayList<>();
    boolean police;

    public PassAdapter(Context context, List<Pass> passes, boolean police) {
        this.context = context;
        this.passes = passes;
        this.police = police;
    }

    @NonNull
    @Override
    public PassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_single_pass, parent, false);
        return new PassViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PassViewHolder holder, int position) {
        Pass pass = passes.get(position);
        String typeText = "";
        switch (pass.getType()) {
            case 0:
                typeText = "in-city";
                break;
            case 1:
                typeText = "in-state";
                break;
            case 2:
                typeText = "out-state";
                break;
        }
        if (pass.isUrgency()) {
            holder.tvpassHeading.setText("Urgent Pass " + typeText);
        } else {
            holder.tvpassHeading.setText("Normal Pass " + typeText);
        }

        holder.tvPassID.setText(pass.getPassid());
        holder.tvduration.setText(pass.getDuration());
        holder.tvVehicleNum.setText(pass.getVehicle());
        holder.tvDate.setText(pass.getDate());
        holder.tvPassenger.setText(pass.getPassengerCount() + "");
        holder.tvType.setText(typeText);
        switch (pass.getStatus()) {
            case 0:
                holder.ivStatusImage.setImageResource(R.drawable.ic_time);
                holder.tvStatusText.setText("Pending");
                holder.tvStatusText.setTextColor(ContextCompat.getColor(context, R.color.yellow_two));
                holder.llRoot.setBackgroundColor(ContextCompat.getColor(context, R.color.yellow_one));
                break;
            case 1:
                holder.ivStatusImage.setImageResource(R.drawable.ic_tick);
                holder.tvStatusText.setText("Approved");
                holder.tvStatusText.setTextColor(ContextCompat.getColor(context, R.color.qr_background));
                holder.llRoot.setBackgroundColor(ContextCompat.getColor(context, R.color.qr_background));
                break;
            case -1:
                holder.ivStatusImage.setImageResource(R.drawable.ic_criss_cross);
                holder.tvStatusText.setText("Rejected");
                holder.tvStatusText.setTextColor(ContextCompat.getColor(context, R.color.rejected_red_text));
                holder.llRoot.setBackgroundColor(ContextCompat.getColor(context, R.color.rejected_red));
                break;

        }

        holder.llRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ScanPassActivity.class);
                Gson gson = new Gson();
                intent.putExtra("pass", gson.toJson(pass));
                intent.putExtra("police", police);
                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return passes.size();
    }
}

class PassViewHolder extends RecyclerView.ViewHolder {
    ImageView ivStatusImage;
    TextView tvpassHeading, tvPassID, tvduration, tvVehicleNum, tvDate, tvPassenger, tvType, tvStatusText;
    LinearLayout llRoot, llApprove, llReject, llStatus;
    RelativeLayout rlChangeStatus;

    public PassViewHolder(@NonNull View itemView) {
        super(itemView);
        ivStatusImage = itemView.findViewById(R.id.single_pass_status_image);
        tvStatusText = itemView.findViewById(R.id.single_pass_approved_status_text);
        tvpassHeading = itemView.findViewById(R.id.single_pass_heading);
        tvPassID = itemView.findViewById(R.id.single_pass_passid);
        tvduration = itemView.findViewById(R.id.single_pass_duration);
        tvVehicleNum = itemView.findViewById(R.id.single_pass_vehicle_num);
        tvDate = itemView.findViewById(R.id.single_pass_date);
        tvPassenger = itemView.findViewById(R.id.single_pass_passengers);
        tvType = itemView.findViewById(R.id.single_pass_type);
        llRoot = itemView.findViewById(R.id.single_item_root_layout);
        rlChangeStatus = itemView.findViewById(R.id.scan_police_status_change_layout);
        llApprove = itemView.findViewById(R.id.police_status_change_approve_layout);
        llReject = itemView.findViewById(R.id.police_status_change_reject_layout);
        llStatus = itemView.findViewById(R.id.scan_pass_approved_status_layout);
    }
}
