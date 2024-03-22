package own.meet.ownbook;

import static own.meet.ownbook.MainActivity.REQUEST_CODE_ADD_NOTE;
import static own.meet.ownbook.MainActivity.REQUEST_CODE_SHOW_NOTE;
import static own.meet.ownbook.MainActivity.REQUEST_CODE_UPDATE_NOTE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import own.meet.Model.Data_Model;
import own.meet.adapter.NoteAdapter;
import own.meet.database.NotesDatabase;
import own.meet.notes.Note;
import own.meet.notesListning.NotesListner;

public class Archive_notes extends AppCompatActivity implements NotesListner {

    private Boolean checklayout;
    private RecyclerView recyclerView;
    private List<Note> noteList;
    private NoteAdapter noteAdapter;
    private  int noteClickPosition = -1;
    private EditText searchtext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Base_Theme_OwnBook);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_archive_notes);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }


//        layout changere
        findViewById(R.id.layoutChanger).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checklayout) {
                    recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL));
                    checklayout = false;
                }
                else {
                    recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
                    checklayout = true;
                }
                savechecklayout(checklayout);
            }
        });



//        recyclerview (inisilize the checklayout)
        recyclerView = findViewById(R.id.ArchiveNotesRecyclerView);
        checklayout = getchecklayout();
        if (checklayout) {
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
            checklayout = false;
        }
        else {
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL));
            checklayout = true;
        }        noteList = new ArrayList<>();
        noteAdapter = new NoteAdapter(noteList,Archive_notes.this);
        recyclerView.setAdapter(noteAdapter);



        //        For Search the notes
        searchtext = findViewById(R.id.inputSearch);
        searchtext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                noteAdapter.cancalTimer();
            }

            @Override
            public void afterTextChanged(Editable s) {
                noteAdapter.searchNotes(String.valueOf(s));
            }
        });

//        load the notes
        getNotes(REQUEST_CODE_SHOW_NOTE,false);

    }


    private void getNotes(final int reqestcode, final boolean isNoteDelete) {

        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                List<Note> notes = NotesDatabase.getDatabase(getApplicationContext()).noteDao().getArchiveNotes();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        handleNoteResponse(reqestcode, isNoteDelete, notes);
                    }
                });
            }
        });

    }

    private void handleNoteResponse(int reqestcode , boolean isNoteDelete , List<Note> notes)
    {
        if (reqestcode == REQUEST_CODE_SHOW_NOTE)
        {
            noteList.addAll(notes);
            noteAdapter.notifyDataSetChanged();

            if(noteList.isEmpty()){
                LinearLayout emptyNoteLayout = findViewById(R.id.emptyNoteLayout);
                emptyNoteLayout.setVisibility(View.VISIBLE);
            }
            else {
                LinearLayout emptyNoteLayout = findViewById(R.id.emptyNoteLayout);
                emptyNoteLayout.setVisibility(View.GONE);
            }
        }
        else if (reqestcode == REQUEST_CODE_ADD_NOTE)
        {
            noteList.add(0,notes.get(0));
            noteAdapter.notifyDataSetChanged();
        }
        else if (reqestcode == REQUEST_CODE_UPDATE_NOTE)
        {
            noteList.remove(noteClickPosition);

            if(isNoteDelete)
            {
                noteAdapter.notifyItemRemoved(noteClickPosition);
            }
            else{
                noteList.add(noteClickPosition,notes.get(noteClickPosition));
                noteAdapter.notifyItemChanged(noteClickPosition);
            }
        }
    }


    @Override
    public void noteOnClick(Note note, int position) {
        noteClickPosition = position;
        Intent intent = new Intent( this ,CreatNoteActivity.class);
        intent.putExtra("isViewOrUpdate" , true);
        intent.putExtra("note",note);
        startActivityForResult(intent,REQUEST_CODE_UPDATE_NOTE);
        finish();
    }


    private boolean getchecklayout(){
        SharedPreferences sharedPreferences = getSharedPreferences("check" , 0);
        return sharedPreferences.getBoolean("checklayout",true);
    }

    private void savechecklayout(boolean checklayout)
    {
        SharedPreferences sharedPreferences = getSharedPreferences("check", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("checklayout", checklayout);
        editor.apply();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Archive_notes.this, MainActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }
}