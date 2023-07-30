package com.example.bt.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bt.R;
import com.example.bt.ViewModel.Khoa;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class KhoaAdapter  extends RecyclerView.Adapter<KhoaAdapter.ViewHolder> {

    private List<Khoa> khoaList;

    public KhoaAdapter(List<Khoa> khoaList) {
        this.khoaList = khoaList;
    }


    public void setKhoaList(List<Khoa> khoaList) {
        this.khoaList = khoaList;
        notifyDataSetChanged();
    }
    private OnItemClickListener itemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(Khoa khoa);
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.khoa, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        // Set the click listener for each item view
        viewHolder.itemView.setOnClickListener(v -> {
            int position = viewHolder.getAdapterPosition();
            if (position != RecyclerView.NO_POSITION && itemClickListener != null) {
                itemClickListener.onItemClick(khoaList.get(position));
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Khoa khoa = khoaList.get(position);
        holder.textViewKhoaName.setText(khoa.getName());
    }

    @Override
    public int getItemCount() {
        return khoaList.size();
    }

    public void deleteKhoa(int position) {
        if (position >= 0 && position < khoaList.size()) {
            Khoa deletedKhoa = khoaList.get(position);
            String deletedKhoaId = deletedKhoa.getId(); // Assuming the Khoa model has an ID field

            // Remove the item from Firestore
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            firestore.collection("KhoaCollection").document(deletedKhoaId)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        // On successful deletion from Firestore, remove it from the local list
                        khoaList.remove(position);
                        notifyItemRemoved(position);
                    })
                    .addOnFailureListener(e -> {
                        // Handle deletion failure if needed
                    });
        }
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewKhoaName;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewKhoaName = itemView.findViewById(R.id.textViewKhoaName);

        }

    }

}