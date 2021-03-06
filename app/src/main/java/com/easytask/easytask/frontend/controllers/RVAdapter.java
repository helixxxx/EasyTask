package com.easytask.easytask.frontend.controllers;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.easytask.easytask.R;
import com.easytask.easytask.frontend.views.DetailedTaskActivity;
import com.easytask.easytask.frontend.views.MainActivity;

import java.util.List;

/**
 * Created by Silas on 12-10-2017.
 */

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.TaskViewHolder>{

    private List<Task> tasks;
    private String description;
    private String subject;
    private String pay;
    private String creatorID;

    public RVAdapter(List<Task> tasks){
        this.tasks = tasks;
    }



    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_item_layout, viewGroup, false);
        TaskViewHolder tvh = new TaskViewHolder(v);
        return tvh;
    }

    @Override
    public void onBindViewHolder(TaskViewHolder holder, int i) {

        pay = tasks.get(i).getTaskReward();
        description = tasks.get(i).getCard_description();
        subject = tasks.get(i).getCard_subject();
        creatorID = tasks.get(i).getCreatorID();


        holder.card_subject.setText(subject);
        holder.card_description.setText(description);
        holder.card_payment.setText(pay + " kr.");
        holder.creatorID = creatorID;


    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView card_subject;
        TextView card_description;
        TextView card_payment;
        String creatorID;

        TaskViewHolder(final View itemView) {
            super(itemView);
            card_subject = (TextView)itemView.findViewById(R.id.card_subject);
            card_description = (TextView)itemView.findViewById(R.id.card_description);
            card_payment = (TextView)itemView.findViewById(R.id.cardPay);

            /* Handles card clicks*/
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    Intent intent = new Intent();
                    intent.setClass(itemView.getContext(), DetailedTaskActivity.class);
                    intent.putExtra("task_subject", card_subject.getText());
                    intent.putExtra("task_description", card_description.getText());
                    intent.putExtra("task_payment", card_payment.getText());
                    intent.putExtra("task_creator", creatorID);
//                    intent.putExtra("task_image", R.id.detailed_task_image);
                    itemView.getContext().startActivity(intent);
//                    Toast.makeText(v.getContext(),card_subject.getText(), Toast.LENGTH_SHORT).show();
                }
            });

        }
    }


}