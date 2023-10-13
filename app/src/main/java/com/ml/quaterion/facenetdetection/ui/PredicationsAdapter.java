package com.ml.quaterion.facenetdetection.ui;

import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.ml.quaterion.facenetdetection.R;

import java.util.List;

public class PredicationsAdapter extends ListAdapter<UiPrediction, PredicationsAdapter.ViewHolder> {
    private OnItemClickListener onItemClickListener;

    public PredicationsAdapter() {
        super(new DiffCallback());
    }

    public void setData(List<UiPrediction> data) {
        if (getCurrentList() != data) {
            submitList(data);
        }
    }


    // Method to set the item click listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a new ViewHolder.
        ViewHolder viewHolder = new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.predication_item, parent, false));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        UiPrediction prediction = getItem(position);

        // Bind the ViewHolder to a data item.
        holder.mTextView.setText(prediction.getLabel());

        // todo check if ok in UI thread
        holder.mImageView.setImageBitmap(BitmapFactory.decodeFile(prediction.getImage()));

        holder.itemView.setOnClickListener(view -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(prediction);
            }
        });
    }

    @Override
    public int getItemCount() {
        // Return the number of items in the data set.
        return getCurrentList().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mTextView;
        private ImageView mImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.name);
            mImageView = itemView.findViewById(R.id.picture);
        }
    }

    public static class DiffCallback extends DiffUtil.ItemCallback<UiPrediction> {

        @Override
        public boolean areItemsTheSame(UiPrediction oldItem, UiPrediction newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areContentsTheSame(UiPrediction oldItem, UiPrediction newItem) {
            return oldItem.getLabel().equals(newItem.getLabel());
        }
    }
}
