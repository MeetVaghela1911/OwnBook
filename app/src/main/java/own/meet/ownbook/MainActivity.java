package own.meet.ownbook;


import static java.security.AccessController.getContext;
import static own.meet.ownbook.R.attr.*;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.color.DynamicColors;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;

import own.meet.Model.Data_Model;
import own.meet.adapter.FirewallAdapter;
import own.meet.adapter.NoteAdapter;
import own.meet.database.NotesDatabase;
import own.meet.notes.Note;
import own.meet.notesListning.FirebaseDataListning;
import own.meet.notesListning.NotesListner;

public class MainActivity extends AppCompatActivity implements NotesListner , FirebaseDataListning {

    private DrawerLayout drawerLayoutmain;
    private ImageView drawerButton;
    private ImageView imageNote, imageArchive, imageSetting, imageLogout, imageAddnote , imageProfile;
    private EditText searchtext;
    private NoteAdapter noteAdapter ;
    private  Boolean checklayoutOrieantation ;
    NestedScrollView scrollViewroomdatabase , scrollViewfirebasedatabase;
    RecyclerView recyclerView , firebaseRecyclerView;
    private List<Note> noteList , pinNoteList , archiveNoteList , colorNoteList , imageNoteList;
    public static final int REQUEST_CODE_ADD_NOTE = 1;
    public static final int REQUEST_CODE_UPDATE_NOTE = 2;
    public static final int REQUEST_CODE_SHOW_NOTE = 3;
    private  int noteClickPosition = -1;
    private Integer radioTheme;
    private ChipGroup headerChipGroup;
    FirewallAdapter adapter;
    ProgressBar progressBar ;
    List<Data_Model> arrayList;
    LinearLayout emptyNoteLayout ;

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setTheme(R.style.Base_Theme_OwnBook);
        DynamicColors.applyToActivitiesIfAvailable(getApplication());
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

//        Initialization
        scrollViewfirebasedatabase = findViewById(R.id.scrollViewOfFirebaseNote);
        scrollViewroomdatabase = findViewById(R.id.scrollViewOfRoomdata);
        firebaseRecyclerView = findViewById(R.id.firebaseNoteRecyclerView);
        recyclerView = findViewById(R.id.othernotesRecyclerView);
        emptyNoteLayout = findViewById(R.id.emptyNoteLayout);
        progressBar = findViewById(R.id.progress_bar);
        pinNoteList = new ArrayList<>();
        archiveNoteList = new ArrayList<>();
        colorNoteList = new ArrayList<>();
        imageNoteList = new ArrayList<>();
        headerChipGroup = findViewById(R.id.chipGroup);
        chipGroupOnClick();


//        set the theme
        radioTheme = getcheckPre();
        if(radioTheme == 2){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else if (radioTheme == 1) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }


//        check the permissions
        if(ContextCompat.checkSelfPermission(
                getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED ){

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);

        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},
                    200);
        }


//      For Navigation View
        drawerLayoutmain = findViewById(R.id.drawerlayoutmain);
        drawerButton = findViewById(R.id.drawerButton);
        NavigationView navigationView = findViewById(R.id.navigationDrawer);
        View headerRoot = navigationView.getHeaderView(0);
        navigationView.setPadding(0,WindowInsetsCompat.Type.statusBars(),0,0);
        imageNote = headerRoot.findViewById(R.id.image);
        imageArchive = headerRoot.findViewById(R.id.imageArchive);
        imageSetting = headerRoot.findViewById(R.id.imageSetting);
        imageLogout = headerRoot.findViewById(R.id.imageLogout);
        imageAddnote = headerRoot.findViewById(R.id.imageAddNote);
        imageProfile = headerRoot.findViewById(R.id.imageUserProfile);
        navigationDrawer(headerRoot);


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

//        create New Note
        ImageView imageAddNoteMain = findViewById(R.id.imageAddNoteMain);
////
        imageAddNoteMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getApplicationContext(), CreatNoteActivity.class),
                        REQUEST_CODE_ADD_NOTE);
                finish();
            }

        });


//        layout Changer
        findViewById(R.id.layoutChanger).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checklayoutOrieantation) {
                    recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL));
                    firebaseRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL));
                    checklayoutOrieantation = false;
                }
                else {
                    recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
                    firebaseRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
                    checklayoutOrieantation = true;
                }
                savechecklayout(checklayoutOrieantation);
            }
        });


//        recyclerview (inisilize the checklayout)
        checklayoutOrieantation = getchecklayout();
        if (checklayoutOrieantation) {
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        }
        else {
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL));
        }
        noteList = new ArrayList<>();
        noteAdapter = new NoteAdapter(noteList,MainActivity.this);
        recyclerView.setAdapter(noteAdapter);





//        load the notes others
        getNotes(REQUEST_CODE_SHOW_NOTE,false);

//        load the pinned notes
        getpinNote(REQUEST_CODE_SHOW_NOTE,false);

//        load archive notes
        getArchiveNotes(REQUEST_CODE_SHOW_NOTE,false);

//         load image notes
        getImageNotes(REQUEST_CODE_SHOW_NOTE,false);

//        findViewById(R.id.chipAll).performClick();

//      on quick actions
        onQuickActionClick();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.chipAll).performClick();
            }
        },1000);
    }


//    on click of chipes
    private void chipGroupOnClick()
    {
        Chip[] setOfChip = new Chip[]{
                headerChipGroup.findViewById(R.id.chipAll),
                headerChipGroup.findViewById(R.id.chippin),
                headerChipGroup.findViewById(R.id.chipArchive),
                headerChipGroup.findViewById(R.id.chipFirebaseData),
                headerChipGroup.findViewById(R.id.chipImg)
        };

        for (Chip chip : setOfChip)
        {
            chip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    for(Chip chip1 : setOfChip)
                    {
                        if (chip == chip1) {
                            chip1.setChecked(true);
                        } else {
                            chip1.setChecked(false);
                        }
                    }


                    if(chip == headerChipGroup.findViewById(R.id.chipAll))
                    {
                        noteAdapter = new NoteAdapter(noteList,MainActivity.this);
                        recyclerView.setAdapter(noteAdapter);
                        recyclerView.setVisibility(View.VISIBLE);

                        if(!noteList.isEmpty()){
                            emptyNoteLayout.setVisibility(View.GONE);
                        }
                        else {
                            emptyNoteLayout.setVisibility(View.VISIBLE);
                        }
                    }
                    else if ( chip == headerChipGroup.findViewById(R.id.chippin))
                    {
                        noteAdapter = new NoteAdapter(pinNoteList,MainActivity.this);
                        recyclerView.setAdapter(noteAdapter);
                        recyclerView.setVisibility(View.VISIBLE);

                        if(pinNoteList.isEmpty()){
                            emptyNoteLayout.setVisibility(View.VISIBLE);
                        }
                        else {
                            emptyNoteLayout.setVisibility(View.GONE);
                        }
                    }
                    else if (chip == headerChipGroup.findViewById(R.id.chipArchive))
                    {
                        noteAdapter = new NoteAdapter(archiveNoteList,MainActivity.this);
                        recyclerView.setAdapter(noteAdapter);
                        recyclerView.setVisibility(View.VISIBLE);

                        if(archiveNoteList.isEmpty()){
                            emptyNoteLayout.setVisibility(View.VISIBLE);
                        }
                        else {
                            emptyNoteLayout.setVisibility(View.GONE);
                        }
                    }
                    else if (chip == headerChipGroup.findViewById(R.id.chipImg))
                    {
                        noteAdapter = new NoteAdapter(imageNoteList,MainActivity.this);
                        recyclerView.setAdapter(noteAdapter);
                        recyclerView.setVisibility(View.VISIBLE);

                        if(imageNoteList.isEmpty()){
                            emptyNoteLayout.setVisibility(View.VISIBLE);
                        }
                        else {
                            emptyNoteLayout.setVisibility(View.GONE);
                        }
                    }
                    else if (chip == headerChipGroup.findViewById(R.id.chipFirebaseData))
                    {
                        emptyNoteLayout.setVisibility(View.GONE);
                        if(user != null){
                            progressBar.setVisibility(View.VISIBLE);
                            getNoteFormRealtimeDataBase();
                        }
                        else {
                            emptyNoteLayout.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        }

                    }
                }
            });
        }
    }

//  get the notes form room data base
    private void getNotes(final int reqestcode, final boolean isNoteDelete){

        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                List<Note> notes = NotesDatabase.getDatabase(getApplicationContext()).noteDao().getAllNotes();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        handleNoteResponse(reqestcode,isNoteDelete,notes);
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


//    get the pinned notes from room
    private void getpinNote(final int reqCode, final  boolean isNoteRemove)
    {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                List<Note> pinMoteList = NotesDatabase.getDatabase(getApplicationContext()).noteDao().getPinNotes();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        handlePinNoteResponse(reqCode,isNoteRemove,pinMoteList);
                    }
                });
            }
        });
    }
    private void handlePinNoteResponse(int reqestcode , boolean isNoteDelete , List<Note> notes)
    {
        if (reqestcode == REQUEST_CODE_SHOW_NOTE)
        {
            pinNoteList.addAll(notes);
            noteAdapter.notifyDataSetChanged();
        }
        else if (reqestcode == REQUEST_CODE_ADD_NOTE)
        {
            pinNoteList.add(0,notes.get(0));
            noteAdapter.notifyDataSetChanged();
        }
        else if (reqestcode == REQUEST_CODE_UPDATE_NOTE)
        {
            pinNoteList.remove(noteClickPosition);

            if(isNoteDelete)
            {
                noteAdapter.notifyItemRemoved(noteClickPosition);
            }
            else{
                pinNoteList.add(noteClickPosition,notes.get(noteClickPosition));
                noteAdapter.notifyItemChanged(noteClickPosition);
            }
        }

    }
//    get archive notes
    private void getArchiveNotes(final int reqestcode, final boolean isNoteDelete) {

        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                List<Note> notes = NotesDatabase.getDatabase(getApplicationContext()).noteDao().getArchiveNotes();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        handleArchiveNoteResponse(reqestcode, isNoteDelete, notes);
                    }
                });
            }
        });

    }
    private void handleArchiveNoteResponse(int reqestcode , boolean isNoteDelete , List<Note> notes)
    {
        if (reqestcode == REQUEST_CODE_SHOW_NOTE)
        {
            archiveNoteList.addAll(notes);
            noteAdapter.notifyDataSetChanged();
        }
        else if (reqestcode == REQUEST_CODE_ADD_NOTE)
        {
            archiveNoteList.add(0,notes.get(0));
            noteAdapter.notifyDataSetChanged();
        }
        else if (reqestcode == REQUEST_CODE_UPDATE_NOTE)
        {
            archiveNoteList.remove(noteClickPosition);

            if(isNoteDelete)
            {
                noteAdapter.notifyItemRemoved(noteClickPosition);
            }
            else{
                archiveNoteList.add(noteClickPosition,notes.get(noteClickPosition));
                noteAdapter.notifyItemChanged(noteClickPosition);
            }
        }
    }

//    get image notes
    private void getImageNotes(final int reqestcode, final boolean isNoteDelete) {

        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                List<Note> notes = NotesDatabase.getDatabase(getApplicationContext()).noteDao().getImageNotes();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        handleImageNoteResponse(reqestcode, isNoteDelete, notes);
                    }
                });
            }
        });

    }

    private void handleImageNoteResponse(int reqestcode , boolean isNoteDelete , List<Note> notes)
    {
        if (reqestcode == REQUEST_CODE_SHOW_NOTE)
        {
            imageNoteList.addAll(notes);
            noteAdapter.notifyDataSetChanged();
        }
        else if (reqestcode == REQUEST_CODE_ADD_NOTE)
        {
            imageNoteList.add(0,notes.get(0));
            noteAdapter.notifyDataSetChanged();
        }
        else if (reqestcode == REQUEST_CODE_UPDATE_NOTE)
        {
            imageNoteList.remove(noteClickPosition);

            if(isNoteDelete)
            {
                noteAdapter.notifyItemRemoved(noteClickPosition);
            }
            else{
                imageNoteList.add(noteClickPosition,notes.get(noteClickPosition));
                noteAdapter.notifyItemChanged(noteClickPosition);
            }
        }
    }

//    get notes from database
    private void getNoteFormRealtimeDataBase()
    {

        recyclerView.setVisibility(View.GONE);
        arrayList = new ArrayList<>();  

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            String userId = user.getUid();

            DatabaseReference dr = FirebaseDatabase.getInstance().getReference()
                    .child("users")
                    .child(userId)
                    .child("Notes");

            dr.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    Log.d("child", String.valueOf(snapshot.getChildrenCount()));

                    for (DataSnapshot datasnap : snapshot.getChildren()) {

                        // Get the data as a HashMap
                        HashMap<String, Object> dataMap = (HashMap<String, Object>) datasnap.getValue();

                        // Extract relevant fields from the HashMap
                        boolean archive = (Boolean) dataMap.get("archive");
                        String title = (String) dataMap.get("title");
                        String dateTime = (String) dataMap.get("dateTime");
                        String  subtitle = (String) dataMap.get("subtitle");
                        String  noteText = (String) dataMap.get("noteText");
                        String imagePath = (String) dataMap.get("imagePath");
                        String  color = (String) dataMap.get("color");
                        String  webLink = (String) dataMap.get("webLink");
                        boolean  pin = (Boolean) dataMap.get("pin");
                        String  checkBoxListStr = (String) dataMap.get("checkBoxListStr");
                        String firebaseID = (String) dataMap.get("firebaseID");
                        Boolean collabrative = (Boolean) dataMap.get("collabrative");
                        String otherUserList = (String) dataMap.get("otherUserList");

                        // Create an instance of Data_Model using the extracted data
                        Data_Model model = new Data_Model(0,archive,title,dateTime,subtitle,noteText, imagePath,color,webLink,pin,checkBoxListStr,firebaseID,collabrative,otherUserList);

                        // Add the model to your arrayList
                            arrayList.add(model);
                    }

                    // Update UI with the fetched data
//                    if(arrayList.isEmpty()){
//                        emptyNoteLayout.setVisibility(View.VISIBLE);
//                    }
//                    else {
//                        emptyNoteLayout.setVisibility(View.GONE);
//                    }

//                    adapter = new FirewallAdapter(arrayList, MainActivity.this,MainActivity.this);
//                    recyclerView.setAdapter(adapter);
//                    progressBar.setVisibility(View.GONE);
//                    recyclerView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    //
                }
            });

            dr = FirebaseDatabase.getInstance().getReference()
                    .child("users")
                    .child(userId)
                    .child("Collabrative Note");

            dr.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    Log.d("child", String.valueOf(snapshot.getChildrenCount()));

                    for (DataSnapshot datasnap : snapshot.getChildren()) {

                        // Get the data as a HashMap
                        HashMap<String, Object> dataMap = (HashMap<String, Object>) datasnap.getValue();

                        // Extract relevant fields from the HashMap
                        boolean archive = (Boolean) dataMap.get("archive");
                        String title = (String) dataMap.get("title");
                        String dateTime = (String) dataMap.get("dateTime");
                        String  subtitle = (String) dataMap.get("subtitle");
                        String  noteText = (String) dataMap.get("noteText");
                        String imagePath = (String) dataMap.get("imagePath");
                        String  color = (String) dataMap.get("color");
                        String  webLink = (String) dataMap.get("webLink");
                        boolean  pin = (Boolean) dataMap.get("pin");
                        String  checkBoxListStr = (String) dataMap.get("checkBoxListStr");
                        String firebaseID = (String) dataMap.get("firebaseID");
                        Boolean collabrative = (Boolean) dataMap.get("collabrative");
                        String otherUserList = (String) dataMap.get("otherUserList");

                        // Create an instance of Data_Model using the extracted data
                        Data_Model model = new Data_Model(0,archive,title,dateTime,subtitle,noteText, imagePath,color,webLink,pin,checkBoxListStr,firebaseID,collabrative,otherUserList);

                        // Add the model to your arrayList
                        arrayList.add(model);
                    }

                    // Update UI with the fetched data
                    if(arrayList.isEmpty()){
                        emptyNoteLayout.setVisibility(View.VISIBLE);
                    }
                    else {
                        emptyNoteLayout.setVisibility(View.GONE);
                    }

                    adapter = new FirewallAdapter(arrayList, MainActivity.this,MainActivity.this);
                    recyclerView.setAdapter(adapter);
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    //
                }
            });
        }
    }

    @Override
    public void OnDataClick(Data_Model note, int position) {
        noteClickPosition = position;
        Intent intent = new Intent( this ,CreatNoteActivity.class);
        intent.putExtra("firebaseNote" , true);
        intent.putExtra("note", note);
        startActivityForResult(intent,REQUEST_CODE_UPDATE_NOTE);
        finish();
    }


    private boolean getchecklayout(){
        SharedPreferences sharedPreferences = getSharedPreferences("check" , 0);
        return sharedPreferences.getBoolean("checklayout",true);
    }
//
    private void savechecklayout(boolean checklayout)
    {
        SharedPreferences sharedPreferences = getSharedPreferences("check", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("checklayout", checklayout);
        editor.apply();
    }
//
    private int getcheckPre()
    {
        SharedPreferences sharedPreferences = getSharedPreferences("themes",0);
        return sharedPreferences.getInt("theme",0);
    }
//
//
    @Override
    public void noteOnClick(Note note, int position) {
        noteClickPosition = position;
        Intent intent = new Intent( this ,CreatNoteActivity.class);
        intent.putExtra("isViewOrUpdate" , true);
        intent.putExtra("note", note);
        startActivityForResult(intent,REQUEST_CODE_UPDATE_NOTE);
        finish();
    }


    private void navigationDrawer(View headerRoot){

        drawerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayoutmain.openDrawer(GravityCompat.START);
                hideAllImagesExcept(imageNote);
            }
        });

        headerRoot.findViewById(R.id.headerNote).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideAllImagesExcept(imageNote);
                drawerLayoutmain.close();
            }
        });

        headerRoot.findViewById(R.id.headerAddNote).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideAllImagesExcept(imageAddnote);
                Intent intent = new Intent(MainActivity.this , CreatNoteActivity.class);
                startActivity(intent);
                drawerLayoutmain.close();
                finish();
            }
        });

        headerRoot.findViewById(R.id.headerUserProfile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideAllImagesExcept(imageAddnote);
                Intent intent = new Intent(MainActivity.this , User_profile.class);
                startActivity(intent);
                drawerLayoutmain.close();
                finish();
            }
        });


        headerRoot.findViewById(R.id.headerArchive).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideAllImagesExcept(imageArchive);
                Intent intent = new Intent(MainActivity.this , Archive_notes.class);
                startActivity(intent);
                drawerLayoutmain.close();
                finish();
            }
        });

        headerRoot.findViewById(R.id.headerSetting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideAllImagesExcept(imageSetting);
                drawerLayoutmain.close();
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                finish();
            }
        });

        headerRoot.findViewById(R.id.headerLogout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideAllImagesExcept(imageLogout);
                drawerLayoutmain.close();

                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.signOut();

                Intent intent = new Intent(getApplicationContext(), Welcome.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private void hideAllImagesExcept(ImageView imageViewToShow) {

        // Hide all ImageViews
        imageNote.setVisibility(View.GONE);
        imageArchive.setVisibility(View.GONE);
        imageSetting.setVisibility(View.GONE);
        imageLogout.setVisibility(View.GONE);
        imageAddnote.setVisibility(View.GONE);

        // Show the provided ImageView
        imageViewToShow.setVisibility(View.VISIBLE);
    }

    private void onQuickActionClick(){

        findViewById(R.id.imageCheackBox).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CreatNoteActivity.class);
                intent.putExtra("forCheckBox",7);
                startActivityForResult(intent,REQUEST_CODE_ADD_NOTE);
                finish();
            }
        });

        findViewById(R.id.imageImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(ContextCompat.checkSelfPermission(
                        getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED ){

                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);

                }
                else {
                    Intent intent1 = new Intent(getApplicationContext() , CreatNoteActivity.class);
                    intent1.putExtra("forImage", 4);
                    startActivityForResult(intent1,REQUEST_CODE_ADD_NOTE);
                    finish();
//                        Toast.makeText(MainActivity.this, "in find methode", Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.imageUrl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getApplicationContext() , CreatNoteActivity.class);
                intent1.putExtra("forUrl", 5);
                startActivityForResult(intent1,REQUEST_CODE_ADD_NOTE);
                finish();
            }
        });

        findViewById(R.id.imageMic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),CreatNoteActivity.class);
                intent.putExtra("forAudio", 6);
                startActivityForResult(intent,REQUEST_CODE_ADD_NOTE);
                finish();
            }
        });

        findViewById(R.id.imgCollabrative).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CreatNoteActivity.class);
                intent.putExtra("forCollabrativeNote",8);
                startActivityForResult(intent,REQUEST_CODE_ADD_NOTE);
                finish();
            }
        });

    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 200) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_ADD_NOTE && resultCode == RESULT_OK)
        {
            getNotes(REQUEST_CODE_ADD_NOTE,true);
        }
        else if (requestCode == REQUEST_CODE_UPDATE_NOTE  && resultCode == RESULT_OK) {
            if (data != null) {
                getNotes(REQUEST_CODE_UPDATE_NOTE,data.getBooleanExtra("isNoteDelete",false));
            }
        }

    }

}








