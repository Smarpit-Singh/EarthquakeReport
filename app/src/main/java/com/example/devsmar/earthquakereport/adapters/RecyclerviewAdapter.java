package com.example.devsmar.earthquakereport.adapters;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.devsmar.earthquakereport.activity.Earthquake;
import com.example.devsmar.earthquakereport.R;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


/**
 * Created by Dev Smar on 2/10/2018.
 */

public class RecyclerviewAdapter extends RecyclerView.Adapter<RecyclerviewAdapter.CustomViewHolder> {

    private ArrayList<Earthquake> earthquakeList;
    private final ListItemClickListener mOnClickListener;   //--------------------

    String offSet = "";
    String primaryLocation = "";


    public interface ListItemClickListener{                      //-----------------
        void onCustomClickListener(int position);            //------------------
        void onCustomLongClickListener(int position);            //------------------
    }

    public Earthquake getItem(int position) {
        return earthquakeList.get(position);
    }


    public RecyclerviewAdapter(ArrayList<Earthquake> earthquakeList, ListItemClickListener mOnClickListener) {
        this.earthquakeList = earthquakeList;
        this.mOnClickListener=mOnClickListener; //-------------------------------
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_item_layout, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        Earthquake obj = earthquakeList.get(position);

        double magnitude = obj.getMagnitude();
        holder.magnitude.setText(getFormattedMagnitude(magnitude));

        if (obj.getLocation().contains("of")) {
            String[] splittedLocation = getSplittedLocation(obj.getLocation());
            offSet = splittedLocation[0];
            primaryLocation = splittedLocation[1];
        } else {
            offSet = String.valueOf(R.string.near_the);
            primaryLocation = obj.getLocation();
        }


        holder.locationOffset.setText(offSet);
        holder.locationPrimary.setText(primaryLocation);

        Date dateObj = new Date(obj.getTime());

        String date = formatDate(dateObj);
        String time = formatTime(dateObj);

        holder.date.setText(date);
        holder.time.setText(time);

        int num = Integer.valueOf(Double.valueOf(obj.getMagnitude()).intValue());
        GradientDrawable magnitudeCircle = (GradientDrawable) holder.magnitude.getBackground();

        setMagnitudeColor(num, magnitudeCircle);

    }

    @Override
    public int getItemCount() {
        return earthquakeList.size();
    }

    private String formatDate(Date dateObject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("LLL dd, yyyy");
        return dateFormat.format(dateObject);
    }


    private String formatTime(Date dateObject) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        return timeFormat.format(dateObject);
    }

    public String getFormattedMagnitude(double mag){
        DecimalFormat magnitudeFormat = new DecimalFormat("0.0");
        return magnitudeFormat.format(mag);
    }

    public String[] getSplittedLocation(String location) {
        String[] splittedString = location.split("of");
        return splittedString;
    }


    private void setMagnitudeColor(int num, GradientDrawable magnitudeCircle) {
        switch (num) {
            case 0:
            case 1:
                magnitudeCircle.setColor(Color.parseColor("#4A7BA7"));
                break;
            case 2:
                magnitudeCircle.setColor(Color.parseColor("#04B4B3"));
                break;
            case 3:
                magnitudeCircle.setColor(Color.parseColor("#10CAC9"));
                break;
            case 4:
                magnitudeCircle.setColor(Color.parseColor("#F5A623"));
                break;
            case 5:
                magnitudeCircle.setColor(Color.parseColor("#FF7D50"));
                break;
            case 6:
                magnitudeCircle.setColor(Color.parseColor("#FC6644"));
                break;
            case 7:
                magnitudeCircle.setColor(Color.parseColor("#E75F40"));
                break;
            case 8:
                magnitudeCircle.setColor(Color.parseColor("#E13A20"));
                break;
            case 9:
                magnitudeCircle.setColor(Color.parseColor("#D93218"));
                break;
            default:
                magnitudeCircle.setColor(Color.parseColor("#C03823"));
                break;
        }


    }

    public class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public TextView magnitude, locationOffset, locationPrimary, date, time;

        public CustomViewHolder(View itemView) {
            super(itemView);

            magnitude = itemView.findViewById(R.id.magnitude);
            locationOffset = itemView.findViewById(R.id.location_offset);
            locationPrimary = itemView.findViewById(R.id.primary_location);
            date = itemView.findViewById(R.id.date);
            time = itemView.findViewById(R.id.time);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            mOnClickListener.onCustomClickListener(position);
        }

        @Override
        public boolean onLongClick(View v) {
            int position = getAdapterPosition();
            mOnClickListener.onCustomLongClickListener(position);
            return true;
        }
    }
}
