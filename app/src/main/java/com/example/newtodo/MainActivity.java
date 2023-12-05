package com.example.newtodo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.android.material.navigation.NavigationView;

import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.newtodo.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private FloatingActionButton fabAddTask;
    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recyclerView);
        fabAddTask = findViewById(R.id.fabAddTask);

        taskAdapter = new TaskAdapter();
        taskAdapter.setOnTaskClickListener(task -> {
            // Handle delete button click here
            deleteTaskInFirebase(task.getId());
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));



        databaseReference = FirebaseDatabase.getInstance().getReference("tasks");

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.OpenDrawer, R.string.CloseDrawer
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();

                if (id == R.id.nav_all) {
                    loadAllTasks();
                } else if (id == R.id.nav_urgent) {
                    loadUniqueTasks();
                } else {
                    // Handle other menu items
                }

                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });


        fabAddTask.setOnClickListener(view -> showAddTaskDialog());


        loadAllTasks();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    private void showAddTaskDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_task, null);
        builder.setView(dialogView);

        EditText titleEditText = dialogView.findViewById(R.id.titleEditText);
        CheckBox urgentCheckBox = dialogView.findViewById(R.id.urgentCheckBox);
        DatePicker datePicker = dialogView.findViewById(R.id.datePicker);
        TimePicker timePicker = dialogView.findViewById(R.id.timePicker);
        Button doneButton = dialogView.findViewById(R.id.doneButton);

        AlertDialog dialog = builder.create();

        doneButton.setOnClickListener(view -> {
            String title = titleEditText.getText().toString().trim();
            boolean isUrgent = urgentCheckBox.isChecked();
            String date = formatDate(datePicker.getYear(), datePicker.getMonth() + 1, datePicker.getDayOfMonth());
            String time = formatTime(timePicker.getHour(), timePicker.getMinute());

            if (!title.isEmpty()) {
                Task newTask = new Task(null, title, isUrgent, date, time);
                addTaskToFirebase(newTask);
                dialog.dismiss();
            } else {
                // Handle empty title case
                Toast.makeText(MainActivity.this, "Task title cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private String formatDate(int year, int month, int day) {
        return String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month, day);
    }

    private String formatTime(int hourOfDay, int minute) {
        return String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
    }



    private void addTaskToFirebase(Task task) {
        String taskId = databaseReference.push().getKey();
        task.setId(taskId);

        databaseReference.child(taskId).setValue(task).addOnCompleteListener(taskSnapshot -> {
            if (taskSnapshot.isSuccessful()) {
                // Task added successfully
                Toast.makeText(MainActivity.this, "Task added successfully", Toast.LENGTH_SHORT).show();
            } else {
                // Handle the error case
                Exception exception = taskSnapshot.getException();
                if (exception != null) {
                    Log.e("CheckError", "Error adding task", exception);
                    Toast.makeText(MainActivity.this, "Error adding task: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Error adding task", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



    private void updateTaskInFirebase(Task task) {
        // Update the task in Firebase based on its ID
        databaseReference.child(task.getId()).setValue(task);
    }

    private void deleteTaskInFirebase(String taskId) {

        databaseReference.child(taskId).removeValue();
    }


    private void loadAllTasks() {
        // Load all tasks from Firebase
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Task> tasks = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Task task = snapshot.getValue(Task.class);
                    if (task != null) {
                        tasks.add(task);
                    }
                }
                taskAdapter.setTasks(tasks);
                recyclerView.setAdapter(taskAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    private void loadUniqueTasks() {
        // Load tasks with priority or any other condition
        Query query = databaseReference.orderByChild("priority").equalTo(true);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Task> tasks = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Task task = snapshot.getValue(Task.class);
                    if (task != null) {
                        tasks.add(task);
                    }
                }
                taskAdapter.setTasks(tasks);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }
}
