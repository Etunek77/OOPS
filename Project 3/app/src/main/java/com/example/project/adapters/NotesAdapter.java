package com.example.project.adapters;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project.entities.Note;
import com.example.project.listeners.NotesListener;
import com.example.project.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class NotesAdapter  extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {

    private List<Note> notes;
    private NotesListener notesListener;
    private Timer timer;
    private List<Note> notesSource;

    public NotesAdapter(List<Note> notes, NotesListener notesListener) {
        this.notes = notes;
        this.notesListener = notesListener;
        notesSource = notes;

    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NoteViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_container_note,
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        holder.setNote(notes.get(position));
        holder.layoutnote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notesListener.onNoteClicked(notes.get(position),position);

            }
        });

    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layoutnote;

        TextView textTitle, textSubtitle, textDateTime;

         NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textTitle);
            textSubtitle = itemView.findViewById(R.id.textSubtitle);
            textDateTime = itemView.findViewById(R.id.textDateTime);
            layoutnote = itemView.findViewById(R.id.layoutNote);

        }

        void setNote(Note note) {
             textTitle.setText(note.getTitle());
             if(note.getSubtitle().trim().isEmpty()) {
                 textSubtitle.setVisibility(View.GONE);
             } else {
                 textSubtitle.setText(note.getSubtitle());

             }
             textDateTime.setText(note.getDateTime());
        }
    }

    public void searchNotes(final String searchKeyboard) {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (searchKeyboard.trim().isEmpty()) {
                    notes = notesSource;
                } else {
                    ArrayList<Note> temp = new ArrayList<>();
                    for (Note note : notesSource) {
                        if (note.getTitle().toLowerCase().contains(searchKeyboard.toLowerCase())
                                || note.getSubtitle().toLowerCase().contains(searchKeyboard.toLowerCase())
                                || note.getNotetText().toLowerCase().contains(searchKeyboard.toLowerCase())) {
                            temp.add(note);

                        }

                    }
                    notes = temp;
                }
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();

                    }
                });

            }
        }, 500);
    }

    public  void cancelTimer(){
        if(timer != null) {
            timer.cancel();
        }
    }


}

