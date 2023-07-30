package com.example.bt.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.bt.Adapter.KhoaAdapter;
import com.example.bt.Adapter.StudentAdapter;
import com.example.bt.R;
import com.example.bt.ViewModel.Khoa;
import com.example.bt.ViewModel.Student;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class HomeFragment  extends Fragment {
    private StudentAdapter studentAdapter;
    private ArrayList<Student> studentList = new ArrayList<>();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize the RecyclerView for students
        RecyclerView recyclerViewStudent = rootView.findViewById(R.id.recyclerViewStudent);
        studentAdapter = new StudentAdapter(studentList);
        recyclerViewStudent.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewStudent.setAdapter(studentAdapter);

        // Fetch Student information from Firestore
        fetchStudentFromFirestore();

        // Set up the click listener for the floating action button
        FloatingActionButton fabAction = rootView.findViewById(R.id.fabAction);
        fabAction.setOnClickListener(view -> {
            // Show the dialog to input "Student" information
            showStudentInputDialog();
        });

        return rootView;
    }

    private void fetchStudentFromFirestore() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("StudentCollection")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<Student> studentList = new ArrayList<>();
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        Student student = documentSnapshot.toObject(Student.class);
                        studentList.add(student);
                    }
                    studentAdapter.setStudentList(studentList);
                })
                .addOnFailureListener(e -> {
                    // Handle fetch failure if needed
                });
    }



    private void showStudentInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Enter Student Information");

        View view = View.inflate(getContext(), R.layout.dialog_student_input, null);
        final TextInputEditText editTextMSSV = view.findViewById(R.id.editTextMSSV);
        final TextInputEditText editTextName = view.findViewById(R.id.editTextName);
        final TextInputEditText editTextDTB = view.findViewById(R.id.editTextDTB);

        // Spinner setup (Replace "spinnerKhoa" with the actual ID of your spinner in dialog_student_input.xml)
        Spinner spinnerKhoa = view.findViewById(R.id.spinnerKhoa);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item);
        spinnerKhoa.setAdapter(adapter);

        builder.setView(view);

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String mssv = editTextMSSV.getText().toString().trim();
                String name = editTextName.getText().toString().trim();
                String dtb = editTextDTB.getText().toString().trim();
                String selectedKhoa = spinnerKhoa.getSelectedItem().toString();

                if (!mssv.isEmpty() && !name.isEmpty() && !dtb.isEmpty()) {
                    // Convert DTB to float
                    float dtbFloat = Float.parseFloat(dtb);

                    // Save student data to Firestore
                    saveStudentToFirestore(mssv, name, dtbFloat, selectedKhoa);
                }
            }
        });

        builder.setNegativeButton("Cancel", null);

        builder.create().show();
    }

    private void saveStudentToFirestore(String mssv, String name, float dtb, String khoa) {
        // Get a Firestore instance
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        // Create a new Student object with the entered data
        Student student = new Student(Integer.parseInt(mssv), name, dtb, khoa);

        // Reference to the "StudentCollection" and a new document with the specified MSSV as the ID
        firestore.collection("StudentCollection")
                .document(mssv) // Use the MSSV as the document ID
                .set(student)
                .addOnSuccessListener(aVoid -> {
                    fetchStudentFromFirestore(); // Update the student list after saving
                })
                .addOnFailureListener(e -> {
                    // Handle save failure
                });
    }
}