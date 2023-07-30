package com.example.bt.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.bt.Adapter.KhoaAdapter;
import com.example.bt.R;
import com.example.bt.ViewModel.Khoa;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Map;

public class DashboardFragment extends Fragment {

    private TextView textViewKhoaInfo;
    private KhoaAdapter khoaAdapter;
    private RecyclerView recyclerViewKhoa;
    private ArrayList<Khoa> khoaList = new ArrayList<>();

    // Remove the setSharedViewModel method and the sharedViewModel variable

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);

        // Initialize the views
        textViewKhoaInfo = rootView.findViewById(R.id.textViewKhoaItem);
        recyclerViewKhoa = rootView.findViewById(R.id.recyclerViewKhoa);

        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        recyclerViewKhoa.setLayoutManager(layoutManager);
        khoaAdapter = new KhoaAdapter(khoaList);
        recyclerViewKhoa.setAdapter(khoaAdapter);

        // Fetch Khoa information from Firestore
        fetchKhoaFromFirestore();


        khoaAdapter.setOnItemClickListener(new KhoaAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Khoa khoa) {
                // Show the dialog to edit "Khoa" information
                showEditKhoaDialog(khoa);
            }
        });

        // Set up the click listener for the floating action button
        FloatingActionButton fabAction = rootView.findViewById(R.id.fabAction);
        fabAction.setOnClickListener(view -> {
            // Show the dialog to input "Khoa" information
            showKhoaInputDialog();
        });



        // Set up swipe-to-delete
        ItemTouchHelper.SimpleCallback swipeToDeleteCallback = new ItemTouchHelper.SimpleCallback(
                0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT
        ) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // When an item is swiped, delete it from the list
                int position = viewHolder.getAdapterPosition();
                khoaAdapter.deleteKhoa(position);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchHelper.attachToRecyclerView(recyclerViewKhoa);
        return rootView;
    }

    private void showEditKhoaDialog(Khoa khoa) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Edit Khoa Information");

        View view = View.inflate(getContext(), R.layout.dialog_khoa_input, null);
        final TextInputEditText editTextKhoaId = view.findViewById(R.id.editTextKhoaId);
        final TextInputEditText editTextKhoaName = view.findViewById(R.id.editTextKhoaName);

        // Set the current values for editing
        editTextKhoaId.setText(khoa.getId());
        editTextKhoaName.setText(khoa.getName());

        builder.setView(view);

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String khoaId = editTextKhoaId.getText().toString().trim();
                String khoaName = editTextKhoaName.getText().toString().trim();
                if (!khoaId.isEmpty() && !khoaName.isEmpty()) {
                    // Save the updated Khoa ID and name to Firestore
                    updateKhoaToFirestore(khoa, khoaId, khoaName);
                }
            }
        });

        builder.setNegativeButton("Cancel", null);

        builder.create().show();
    }

    private void updateKhoaToFirestore(Khoa originalKhoa, String khoaId, String khoaName) {
        // Get a Firestore instance
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        // Update the Khoa object with the entered ID and name
        originalKhoa.setId(khoaId);
        originalKhoa.setName(khoaName);

        // Reference to the "KhoaCollection" and the document with the specified ID
        firestore.collection("KhoaCollection")
                .document(khoaId) // Use the specified ID as the document ID
                .set(originalKhoa)
                .addOnSuccessListener(aVoid -> {
                    fetchKhoaFromFirestore();
                })
                .addOnFailureListener(e -> {
                    // Handle update failure
                });
    }
    private void saveKhoaToFirestore(String khoaId, String khoaName) {
        // Get a Firestore instance
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        // Create a new Khoa object with the entered ID and name
        Khoa khoa = new Khoa(khoaId, khoaName);

        // Reference to the "KhoaCollection" and a new document with the specified ID
        firestore.collection("KhoaCollection")
                .document(khoaId) // Use the specified ID as the document ID
                .set(khoa)
                .addOnSuccessListener(aVoid -> {
                    fetchKhoaFromFirestore();
                })
                .addOnFailureListener(e -> {
                    // Handle save failure
                });
    }
    private void fetchKhoaFromFirestore() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("KhoaCollection")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<Khoa> khoaList = new ArrayList<>();
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        Khoa khoa = documentSnapshot.toObject(Khoa.class);
                        khoa.setId(documentSnapshot.getId()); // Store the document ID in the Khoa object
                        khoaList.add(khoa);
                    }
                    this.khoaList.clear();
                    this.khoaList.addAll(khoaList);
                    khoaAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    // Handle fetch failure if needed
                });
    }

    private void showKhoaInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Enter Khoa Information");

        View view = View.inflate(getContext(), R.layout.dialog_khoa_input, null);
        final TextInputEditText editTextKhoaId = view.findViewById(R.id.editTextKhoaId);
        final TextInputEditText editTextKhoaName = view.findViewById(R.id.editTextKhoaName);
        builder.setView(view);

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String khoaId = editTextKhoaId.getText().toString().trim();
                String khoaName = editTextKhoaName.getText().toString().trim();
                if (!khoaId.isEmpty() && !khoaName.isEmpty()) {
                    // Save the Khoa ID and name to Firestore
                    saveKhoaToFirestore(khoaId, khoaName);
                }
            }
        });

        builder.setNegativeButton("Cancel", null);

        builder.create().show();
    }



}