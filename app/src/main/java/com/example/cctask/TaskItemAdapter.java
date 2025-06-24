package com.example.cctask;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class TaskItemAdapter extends BaseAdapter {
    private final Context context;
    private final List<Task> taskList;

    public TaskItemAdapter(Context context, List<Task> taskList) {
        this.context = context;
        this.taskList = taskList;
    }

    @Override
    public int getCount() {
        return taskList.size();
    }

    @Override
    public Object getItem(int position) {
        return taskList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Task task = taskList.get(position);
        View view = LayoutInflater.from(context).inflate(R.layout.task_item, parent, false);

        ((TextView) view.findViewById(R.id.taskTitle)).setText("• " + task.getTitle());
        ((TextView) view.findViewById(R.id.taskAssigned)).setText("Atribuit: " + task.getAssignedTo());
        ((TextView) view.findViewById(R.id.taskCreated)).setText("Creat de: " + task.getCreatedBy());
        ((TextView) view.findViewById(R.id.taskDueDate)).setText("Expiră: " + Task.dateFormat.format(task.getDataExpirare()));

        return view;
    }
}
