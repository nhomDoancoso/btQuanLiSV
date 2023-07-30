package com.example.bt.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bt.R;
import com.example.bt.ViewModel.Student;

import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.ViewHolder> {

    private List<Student> studentList;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(Student student);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public StudentAdapter(List<Student> studentList) {
        this.studentList = studentList;
    }

    public void setStudentList(List<Student> studentList) {
        this.studentList = studentList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sinh_vien, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Student student = studentList.get(position);
        holder.textViewMSSV.setText("MSSV: " + student.getMssv());
        holder.textViewName.setText("Name: " + student.getName());
        holder.textViewDTB.setText("DTB: " + student.getDTB());
        holder.textViewKhoa.setText("Khoa: " + student.getKhoa());
        holder.itemView.setOnClickListener(view -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(student);
            }
        });
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewMSSV;
        private TextView textViewName;
        private TextView textViewDTB;
        private TextView textViewKhoa;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewMSSV = itemView.findViewById(R.id.textViewMSSV);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewDTB = itemView.findViewById(R.id.textViewDTB);
            textViewKhoa = itemView.findViewById(R.id.textViewKhoa);
        }
    }
}