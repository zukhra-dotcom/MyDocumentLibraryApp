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

public class ExpirationsAdapter extends RecyclerView.Adapter<ExpirationsAdapter.ExpirationViewHolder> {

    private List<PutPDF> documentsByExpirations;
    public ExpirationsAdapter(List<PutPDF> documentsByExpirations) {
        this.documentsByExpirations = documentsByExpirations;
    }

    @NonNull
    @Override
    public ExpirationsAdapter.ExpirationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view_files, parent, false);
        return new ExpirationsAdapter.ExpirationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpirationsAdapter.ExpirationViewHolder holder, int position) {
        PutPDF documentByExpirations = documentsByExpirations.get(position);

        holder.titleTextView.setText(documentByExpirations.getName());
        holder.notesTextView.setText(documentByExpirations.getNotes());
        holder.createdDate.setText(documentByExpirations.getCreatedDate());
        holder.expirationDate.setText(documentByExpirations.getDeadlineDate());
        holder.original.setVisibility(View.GONE);
        holder.changeExpirationDate.setVisibility(View.GONE);
        holder.deleteDoc.setVisibility(View.GONE);
        holder.permission.setVisibility(View.GONE);
        holder.updateOriginal.setVisibility(View.GONE);
        holder.updateNote.setEnabled(false);
    }

    @Override
    public int getItemCount() {
        return documentsByExpirations.size();
    }
    public class ExpirationViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView notesTextView;
        TextView createdDate;
        TextView expirationDate;
        TextView original;
        ImageButton changeExpirationDate;
        ImageButton deleteDoc;
        Switch permission;
        Button updateNote, updateOriginal;

        public ExpirationViewHolder(@NonNull View itemView) {
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
        }
    }
}
