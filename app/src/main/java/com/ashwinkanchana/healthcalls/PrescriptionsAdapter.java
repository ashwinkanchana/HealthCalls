package com.ashwinkanchana.healthcalls;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;



public class PrescriptionsAdapter extends RecyclerView.Adapter<PrescriptionsAdapter.ExampleViewHolder> {
    private ArrayList<Medication> mExampleList;
    private OnItemClickListener mListener;
    private int freq;
    String once  ="Once a day";
    String twice  ="Twice a day";
    String thrice  ="Thrice a day";
    String dose =  "Dose - ";



    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }


    public static class ExampleViewHolder extends RecyclerView.ViewHolder {
        public TextView name,units,dose,inventory,frequency,reminders,id;
        public ImageView imageView;


        public ExampleViewHolder(View itemView,PrescriptionsAdapter.OnItemClickListener listener) {
            super(itemView);
            id = itemView.findViewById(R.id.thumbnail_id);
            name = itemView.findViewById(R.id.medication_name);
            units = itemView.findViewById(R.id.medication_units);
            dose = itemView.findViewById(R.id.medication_dose);
            inventory = itemView.findViewById(R.id.medication_inventory);
            frequency = itemView.findViewById(R.id.medication_frequency);
            reminders = itemView.findViewById(R.id.medication_reminders);
            imageView = itemView.findViewById(R.id.image_view);

            itemView.setOnClickListener(v -> {
                if(listener != null) {

                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION){
                        listener.onItemClick(position);
                    }
                }
            });
        }
    }

    public PrescriptionsAdapter(ArrayList<Medication> medicationArrayList) {
        mExampleList = medicationArrayList;
    }

    @Override
    public ExampleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_prescription, parent, false);
        ExampleViewHolder evh = new ExampleViewHolder(v,mListener);
        return evh;
    }

    @Override
    public void onBindViewHolder(ExampleViewHolder holder, int position) {
        Medication currentItem = mExampleList.get(position);
        holder.name.setText(currentItem.getName());
        holder.units.setText(currentItem.getUnit());
        freq = currentItem.getFrequency();
        holder.id.setText(String.valueOf(position+1));
        if(freq==1)
            holder.frequency.setText(once);
        else if(freq==2)
            holder.frequency.setText(twice);
        else holder.frequency.setText(thrice);

        holder.dose.setText(dose+String.valueOf(currentItem.getDoseage()+" "+currentItem.getUnit()));
        if(currentItem.getInventory()<5)
            holder.inventory.setTextColor(Color.rgb(200,0,0));
        holder.inventory.setText(String.valueOf(currentItem.getInventory()+" "+currentItem.getUnit()+" left"));
        holder.reminders.setText(currentItem.getReminders());
    }

    @Override
    public int getItemCount() {
        return mExampleList.size();
    }
}