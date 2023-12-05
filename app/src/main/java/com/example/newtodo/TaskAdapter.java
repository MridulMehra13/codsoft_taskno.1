package com.example.newtodo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> tasks = new ArrayList<>();
    private OnTaskClickListener listener;
    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
        notifyDataSetChanged();
    }

    public void setOnTaskClickListener(OnTaskClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = tasks.get(position);
        holder.bind(task);
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }
    public interface OnTaskClickListener {
        void onDeleteClick(Task task);
    }

    // TaskViewHolder class
     class TaskViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTextView;
        private TextView priorityTextView;
        private TextView dateTextView;
        private TextView timeTextView;
        private ImageButton deleteButton;


        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);

            titleTextView = itemView.findViewById(R.id.titleTextView);
            priorityTextView = itemView.findViewById(R.id.priorityTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }

        public void bind(Task task) {
            titleTextView.setText(task.getTitle());
            priorityTextView.setText(task.isUrgent() ? "Urgent" : "Not Urgent");
            dateTextView.setText("Date: " + task.getDate());
            timeTextView.setText("Time: " + task.getTime());


            deleteButton.setOnClickListener(v -> {
                // Call method to delete the task
                if(listener != null)
                {
                    listener.onDeleteClick(task);
                }
            });
        }
    }

    // Method to delete a task from Firebase
    public void deleteTask(int position) {
        Task task = tasks.get(position);
        deleteTaskInFirebase(task.getId());
    }

    // Method to delete a task from Firebase
    private void deleteTaskInFirebase(String taskId) {

    }
}
