package com.example.mydocumentlibrary;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

public class FileMyViewHolder extends RecyclerView.ViewHolder { //class that takes data from single_view_files.xml
    public TextView name, date, deadline;
    public EditText notes, original;
    public ImageButton delete, download;
    public Switch permissionFriends;
    public TextView category;
    public FileMyViewHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.file_title);
        date = itemView.findViewById(R.id.file_date);
        notes = itemView.findViewById(R.id.file_notes);
        original = itemView.findViewById(R.id.file_original);
        deadline = itemView.findViewById(R.id.file_deadlineDate);
        delete = itemView.findViewById(R.id.file_delete);
        download = itemView.findViewById(R.id.file_download);
        permissionFriends = itemView.findViewById(R.id.file_permissionSwitch);
        category = itemView.findViewById(R.id.file_type);
    }
}
