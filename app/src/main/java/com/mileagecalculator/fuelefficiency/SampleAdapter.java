package com.mileagecalculator.fuelefficiency;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mileagecalculator.R;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

import atownsend.swipeopenhelper.BaseSwipeOpenViewHolder;


class SampleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    private final ButtonCallbacks callbacks;
    private List<Utils> items;

    interface ButtonCallbacks {
        void removePosition(int position);

        void showRecords(int position);
    }

    SampleAdapter(Context context, ButtonCallbacks callbacks, List<Utils> items) {
        this.context = context;
        this.callbacks = callbacks;
        this.items = items;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TestViewHolder(
                LayoutInflater.from(context).inflate(R.layout.view_holder_view, parent, false), callbacks);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        try {
            ((TestViewHolder) holder).textView.setText(URLDecoder.decode(items.get(position)
                    .getVehcleName()
                    + " "
                    + "("
                    + items.get(position).getVehcleModel() + ")", "UTF-8"));
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

    private static class TestViewHolder extends BaseSwipeOpenViewHolder {

        private LinearLayout contentView;
        private TextView textView;
        private TextView deleteButton;
        private TextView editButton;

        private TestViewHolder(final View view, final ButtonCallbacks callbacks) {
            super(view);
            contentView = (LinearLayout) view.findViewById(R.id.content_view);
            textView = (TextView) view.findViewById(R.id.display_text);
            deleteButton = (TextView) view.findViewById(R.id.delete_button);
            editButton = (TextView) view.findViewById(R.id.edit_button);

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callbacks.removePosition(getAdapterPosition());
                }
            });

            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   callbacks.showRecords(getAdapterPosition());
                }
            });
        }

        @NonNull
        @Override
        public View getSwipeView() {
            return contentView;
        }

        @Override
        public float getEndHiddenViewSize() {
            return deleteButton.getMeasuredWidth();
        }

        @Override
        public float getStartHiddenViewSize() {
            return editButton.getMeasuredWidth();
        }

        @Override
        public void notifyStartOpen() {
            itemView.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.gc_blue));
        }

        @Override
        public void notifyEndOpen() {
            itemView.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.colorPrimary));
        }
    }
}