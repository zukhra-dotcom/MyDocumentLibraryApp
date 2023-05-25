package com.example.mydocumentlibrary;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {

    private List<PutPDF> documentsByNotes;
    String fileType;
    public NotesAdapter(List<PutPDF> documentList) {
        this.documentsByNotes = documentList;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view_files, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotesAdapter.NoteViewHolder holder, int position) {
        PutPDF documentByNotes = documentsByNotes.get(position);

        holder.titleTextView.setText(documentByNotes.getName());
        holder.notesTextView.setText(documentByNotes.getNotes());
        holder.createdDate.setText(documentByNotes.getCreatedDate());
        holder.expirationDate.setText(documentByNotes.getDeadlineDate());
        holder.original.setVisibility(View.GONE);
        holder.changeExpirationDate.setVisibility(View.GONE);
        holder.deleteDoc.setVisibility(View.GONE);
        holder.permission.setVisibility(View.GONE);
        holder.updateOriginal.setVisibility(View.GONE);
        holder.updateNote.setEnabled(false);
        holder.fileTypeTextView.setText(fileType);
    }

    @Override
    public int getItemCount() {
        return documentsByNotes.size();
    }
    public class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView notesTextView;
        TextView createdDate;
        TextView expirationDate;
        TextView original;
        ImageButton changeExpirationDate;
        ImageButton deleteDoc;
        Switch permission;
        Button updateNote, updateOriginal;
        TextView fileTypeTextView;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.file_title);
            notesTextView = itemView.findViewById(R.id.file_notes);
            createdDate = itemView.findViewById(R.id.file_date);
            expirationDate = itemView.findViewById(R.id.file_deadlineDate);
            original = itemView.findViewById(R.id.file_original);
            changeExpirationDate = itemView.findViewById(R.id.file_deadline);
            deleteDoc = itemView.findViewById(R.id.file_delete);
            permission = itemView.findViewById(R.id.file_permissionSwitch);
            updateNote = itemView.findViewById(R.id.updateNoteBtn);
            updateOriginal = itemView.findViewById(R.id.updateOriginBtn);
            fileTypeTextView = itemView.findViewById(R.id.file_type);
        }
    }
}
