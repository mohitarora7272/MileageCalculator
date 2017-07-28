package com.mileagecalculator.fuelefficiency;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mileagecalculator.R;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;


class RecordsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    private List<Utils> items;

    RecordsAdapter(Context context, ArrayList<Utils> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TestViewHolder(LayoutInflater.from(context).inflate(R.layout.list_content_items, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        try {
            ((TestViewHolder) holder).tvVName.setText(URLDecoder.decode(items.get(position).getVehcleName(), "UTF-8"));
            ((TestViewHolder) holder).tvVModel.setText(URLDecoder.decode(items.get(position).getVehcleModel(), "UTF-8"));
            ((TestViewHolder) holder).tvVFuelType.setText(URLDecoder.decode(items.get(position).getFuelType(), "UTF-8"));
            ((TestViewHolder) holder).tvVMileageType.setText(URLDecoder.decode(items.get(position).getMileage(), "UTF-8"));
            ((TestViewHolder) holder).tvVDate.setText(URLDecoder.decode(items.get(position).getDate(), "UTF-8"));
            ((TestViewHolder) holder).tvVNewReading.setText(URLDecoder.decode(items.get(position).getNew_reading(), "UTF-8"));
            ((TestViewHolder) holder).tvVFuel.setText(URLDecoder.decode(items.get(position).getFuel(), "UTF-8")+"L");
            ((TestViewHolder) holder).tvVPrice.setText(URLDecoder.decode(items.get(position).getPrice(), "UTF-8"));
            ((TestViewHolder) holder).tvVAvg.setText(URLDecoder.decode(items.get(position).getAverage(), "UTF-8"));
            ((TestViewHolder) holder).tvVPriceAvg.setText(URLDecoder.decode(items.get(position).getPriceAvg(), "UTF-8"));

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    void removePosition(int adapterPosition) {
        items.remove(adapterPosition);
        notifyItemRemoved(adapterPosition);
    }

    private static class TestViewHolder extends RecyclerView.ViewHolder {

        private TextView tvVName;
        private TextView tvVModel;
        private TextView tvVFuelType;
        private TextView tvVMileageType;
        private TextView tvVDate;
        private TextView tvVNewReading;
        private TextView tvVFuel;
        private TextView tvVPrice;
        private TextView tvVAvg;
        private TextView tvVPriceAvg;

        private TestViewHolder(final View view) {
            super(view);

            tvVName = (TextView) view.findViewById(R.id.tvVName);
            tvVModel = (TextView) view.findViewById(R.id.tvVModel);
            tvVFuelType = (TextView) view.findViewById(R.id.tvVFuelType);
            tvVMileageType = (TextView) view.findViewById(R.id.tvVMileageType);
            tvVDate = (TextView) view.findViewById(R.id.tvVDate);
            tvVNewReading = (TextView) view.findViewById(R.id.tvVNewReading);
            tvVFuel = (TextView) view.findViewById(R.id.tvVFuel);
            tvVPrice = (TextView) view.findViewById(R.id.tvVPrice);
            tvVAvg = (TextView) view.findViewById(R.id.tvVAvg);
            tvVPriceAvg = (TextView) view.findViewById(R.id.tvVPriceAvg);

        }
    }
}