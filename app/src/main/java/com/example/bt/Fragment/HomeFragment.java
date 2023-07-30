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
import android.widget.Toast;

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

        studentAdapter.setOnItemClickListener(student -> {
            showEditStudentInputDialog(student);
        });

// Gắn ItemTouchHelper vào RecyclerView để bắt sự kiện swipe
        // Gắn ItemTouchHelper vào RecyclerView để bắt sự kiện swipe
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // Xoá sinh viên khi người dùng swipe trên item
                int position = viewHolder.getAdapterPosition();
                Student student = studentList.get(position);
                int mssv = student.getMssv();
                deleteStudentFromFirestore(mssv);
            }
        });

        itemTouchHelper.attachToRecyclerView(recyclerViewStudent);

        return rootView;
    }
    private void showEditStudentInputDialog(Student student) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Edit Student Information");

        View view = View.inflate(getContext(), R.layout.dialog_student_input, null);
        final TextInputEditText editTextMSSV = view.findViewById(R.id.editTextMSSV);
        final TextInputEditText editTextName = view.findViewById(R.id.editTextName);
        final TextInputEditText editTextDTB = view.findViewById(R.id.editTextDTB);
        Spinner spinnerKhoa = view.findViewById(R.id.spinnerKhoa);

        // Set the existing data in the input fields
        editTextMSSV.setText(String.valueOf(student.getMssv()));
        editTextName.setText(student.getName());
        editTextDTB.setText(String.valueOf(student.getDTB()));

        // Fetch Khoa information from Firestore and populate the Spinner
        fetchKhoaFromFirestore(spinnerKhoa);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item);
        spinnerKhoa.setAdapter(adapter);

        // Set the existing value as the selected item in the Spinner
        spinnerKhoa.setSelection(adapter.getPosition(student.getKhoa()));

        builder.setView(view);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String mssv = editTextMSSV.getText().toString().trim();
            String name = editTextName.getText().toString().trim();
            String dtb = editTextDTB.getText().toString().trim();
            String selectedKhoa = spinnerKhoa.getSelectedItem().toString();

            if (!mssv.isEmpty() && !name.isEmpty() && !dtb.isEmpty()) {
                // Convert DTB to float
                float dtbFloat = Float.parseFloat(dtb);

                // Save the updated student data to Firestore
                updateStudentInFirestore(student.getMssv(), mssv, name, dtbFloat, selectedKhoa);
            } else {
                // Display an error message if any of the required fields are empty
                Toast.makeText(requireContext(), "Please fill in all the required fields.", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", null);

        builder.create().show();
    }


    private void updateStudentInFirestore(int originalMssv, String mssv, String name, float dtb, String khoa) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("StudentCollection")
                .document(String.valueOf(originalMssv))
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Document exists, perform update
                        firestore.collection("StudentCollection")
                                .document(String.valueOf(originalMssv))
                                .update("mssv", Integer.parseInt(mssv), "Name", name, "DTB", dtb, "khoa", khoa)
                                .addOnSuccessListener(aVoid -> {
                                    fetchStudentFromFirestore(); // Update the student list after saving
                                })
                                .addOnFailureListener(e -> {
                                    // Handle update failure
                                });
                    } else {
                        // Document does not exist, handle error
                        Toast.makeText(requireContext(), "Student not found!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle get document failure
                });
    }

    private void deleteStudentFromFirestore(int mssv) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("StudentCollection")
                .document(String.valueOf(mssv))
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // Delete successful, update student list and notify RecyclerView
                    fetchStudentFromFirestore();
                })
                .addOnFailureListener(e -> {
                    // Handle delete failure if needed
                });
    }
    /////////
    private void fetchStudentFromFirestore() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("StudentCollection")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    studentList.clear(); // Clear the existing list before adding new data
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        Student student = documentSnapshot.toObject(Student.class);
                        studentList.add(student);
                    }
                    studentAdapter.notifyDataSetChanged(); // Notify the adapter about the data change
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

        // Fetch Khoa information from Firestore and populate the Spinner
        fetchKhoaFromFirestore(spinnerKhoa);

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
                } else {
                    // Display an error message if any of the required fields are empty
                    Toast.makeText(requireContext(), "Please fill in all the required fields.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", null);

        builder.create().show();
    }

    private void fetchKhoaFromFirestore(Spinner spinnerKhoa) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("KhoaCollection")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<String> khoaList = new ArrayList<>();
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        Khoa khoa = documentSnapshot.toObject(Khoa.class);
                        khoaList.add(khoa.getName());
                    }
                    ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinnerKhoa.getAdapter();
                    adapter.clear();
                    adapter.addAll(khoaList);
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    // Handle fetch failure if needed
                });
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