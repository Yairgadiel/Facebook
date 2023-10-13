package com.ml.quaterion.facenetdetection;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.ml.quaterion.facenetdetection.databinding.HistoryItemBinding;

public class HistoryAdapter extends ListAdapter<HistorySearch, HistoryAdapter.ViewHolder> {

    public HistoryAdapter() {
        super(new HistoryAdapter.DiffCallback());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        HistoryItemBinding binding = HistoryItemBinding.inflate(LayoutInflater.from(parent.getContext()));

        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(HistoryAdapter.ViewHolder holder, int position) {
        HistorySearch historySearch = getItem(position);

        holder.bind(historySearch);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        // region Members

        private HistoryItemBinding _binding;

        private HistorySearch _item;

        // endregion

        // region Constructors

        public ViewHolder(HistoryItemBinding binding) {
            super(binding.getRoot());
        }

        // endregion

        // region Properties

        public HistorySearch getItem() {
            return _item;
        }

        public void setItem(HistorySearch item) {
            _item = item;
        }

        // endregion

        // region Public Methods

        public void bind(HistorySearch item) {
            _item = item;

        }

        // endregion

    }

    public static class DiffCallback extends DiffUtil.ItemCallback<HistorySearch> {

        @Override
        public boolean areItemsTheSame(HistorySearch oldItem, @NonNull HistorySearch newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areContentsTheSame(HistorySearch oldItem, HistorySearch newItem) {
            return oldItem.getId() == newItem.getId();
        }

    }

}
