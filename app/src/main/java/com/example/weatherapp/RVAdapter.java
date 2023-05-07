package com.example.weatherapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.ViewHolder> {

    private Context context;

    public RVAdapter(Context context, ArrayList<weatherRVmodal> weatherRVmodalArrayList) {
        this.context = context;
        this.weatherRVmodalArrayList = weatherRVmodalArrayList;
    }

    private ArrayList<weatherRVmodal> weatherRVmodalArrayList;
    @NonNull
    @Override
    public RVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.weather_rv_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RVAdapter.ViewHolder holder, int position) {
        weatherRVmodal modal = weatherRVmodalArrayList.get(position);
        Picasso.get().load("http:".concat(modal.getIcon())).into(holder.conditionIV);
        holder.tempTV.setText(modal.getTemp()+"°c");
        holder.windTV.setText(modal.getWindspeed()+"km/h");
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        SimpleDateFormat output = new SimpleDateFormat("hh:mm aa");
        try{
            Date t= input.parse(modal.getTime());
            holder.timeTV.setText(output.format(t));
        }
        catch (ParseException e){
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount()
    {
        return weatherRVmodalArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView windTV, tempTV, timeTV;
        private ImageView conditionIV;
        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            windTV=itemView.findViewById(R.id.TVWindspeed);
            tempTV=itemView.findViewById(R.id.TVTemperature);
            timeTV=itemView.findViewById(R.id.TVTime);
            conditionIV=itemView.findViewById(R.id.IVCondition);
        }
    }
}
