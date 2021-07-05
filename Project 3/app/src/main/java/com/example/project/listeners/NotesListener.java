package com.example.project.listeners;

import com.example.project.entities.Note;

public interface NotesListener {
    void onNoteClicked(Note note, int position);
}
