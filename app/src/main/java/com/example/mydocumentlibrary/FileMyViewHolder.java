package com.example.mydocumentlibrary;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FileMyViewHolder extends RecyclerView.ViewHolder { //class that takes data from single_view_files.xml
    public TextView name, date, deadline;
    public EditText notes;
    public ImageButton delete;
    public FileMyViewHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.file_title);
        date = itemView.findViewById(R.id.file_date);
        notes = itemView.findViewById(R.id.file_notes);
        deadline = itemView.findViewById(R.id.file_deadlineDate);
        delete = itemView.findViewById(R.id.file_delete);
    }
}
