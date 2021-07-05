package com.example.project.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project.R;
import com.example.project.database.NotesDatabase;
import com.example.project.entities.Note;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CreateNoteActivity extends AppCompatActivity {
    private EditText inputNoteTitle,inputNoteSubtitle,inputNoteText;
    private TextView textDateTime;
    private Note alreadyAvailableNote;
    private AlertDialog dialogDeleteNote;
    ImageView layoutDeleteNote;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);
        setTitle("Create New Note");
        ImageView imageback = findViewById(R.id.imageBack);
        inputNoteTitle = findViewById(R.id.inputNoteTitle);
        inputNoteSubtitle = findViewById(R.id.inputNoteSubtitle);
        inputNoteText = findViewById(R.id.inputNote);
        textDateTime = findViewById(R.id.textDateTime);

        textDateTime.setText(
                new SimpleDateFormat("EEEE, dd MMMM yyyy hh:mm a",Locale.getDefault())
                        .format(new Date()));

        imageback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ImageView imageSave = findViewById(R.id.imageSave);
        imageSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNote();
            }
        });

        if(getIntent().getBooleanExtra("isViewOrUpdate",false)){
            alreadyAvailableNote = (Note) getIntent().getSerializableExtra("note");
            setViewOrUpdateNote();
        }

        layoutDeleteNote = findViewById(R.id.layoutDeleteNote);
        layoutDeleteNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteNoteDialog();

            }
        });


    }
    private void setViewOrUpdateNote(){
        inputNoteTitle.setText(alreadyAvailableNote.getTitle());
        inputNoteText.setText(alreadyAvailableNote.getNotetText());
        inputNoteSubtitle.setText(alreadyAvailableNote.getSubtitle());
        textDateTime.setText(alreadyAvailableNote.getDateTime());



    }

    private void saveNote() {
        if(inputNoteTitle.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Title can't be empty", Toast.LENGTH_SHORT).show();
            return;

        }
        else if(inputNoteSubtitle.getText().toString().trim().isEmpty() && inputNoteText.getText().toString().trim().isEmpty()) {

            Toast.makeText(this, "Can't be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        final Note note= new Note();
        note.setTitle(inputNoteTitle.getText().toString());
        note.setSubtitle(inputNoteSubtitle.getText().toString());
        note.setNotetText(inputNoteText.getText().toString());
        note.setDateTime(textDateTime.getText().toString());

        if(alreadyAvailableNote != null){
            note.setId(alreadyAvailableNote.getId());
        }



        @SuppressLint("StaticFieldLeak")
        class SaveNoteTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                NotesDatabase.getDatabase(getApplicationContext()).noteDao().insertNote(note);

                return null;
            }

            @Override

            protected  void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Intent intent = new Intent();
                setResult(RESULT_OK,intent);
                finish();
            }



        }

        new SaveNoteTask().execute();






    }

    private void showDeleteNoteDialog(){
        if(dialogDeleteNote==null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(CreateNoteActivity.this);
            View view = LayoutInflater.from(this).inflate(
                    R.layout.layout_delete_note,
                    (ViewGroup) findViewById(R.id.layoutDeleteNoteContainer)
            );
            builder.setView(view);
            dialogDeleteNote = builder.create();
            if(dialogDeleteNote.getWindow() != null) {
                dialogDeleteNote.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            view.findViewById(R.id.textDeleteNote).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    @SuppressLint("StaticFieldLeak")
                    class DeleteNoteTask extends AsyncTask<Void, Void, Void>{

                        @Override
                        protected Void doInBackground(Void... voids) {
                            NotesDatabase.getDatabase(getApplicationContext()).noteDao()
                                    .deleteNote(alreadyAvailableNote);
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            super.onPostExecute(aVoid);
                            Intent intent = new Intent();
                            intent.putExtra("isNoteDeleted",true);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    }
                    new DeleteNoteTask().execute();
                }
            });

            view.findViewById(R.id.textCancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogDeleteNote.dismiss();
                }
            });
        }
        dialogDeleteNote.show();
    }


}