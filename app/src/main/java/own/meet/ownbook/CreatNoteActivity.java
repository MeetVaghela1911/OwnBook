package own.meet.ownbook;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.util.Linkify;
import android.util.Log;
import android.util.Pair;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.Executors;

import own.meet.Model.Data_Model;
import own.meet.database.NotesDatabase;
import own.meet.notes.Note;
import own.meet.notesListning.FirebaseDataListning;

public class CreatNoteActivity extends AppCompatActivity {

    private EditText title, subtitle, text;
    private TextView dateTime;
    private ImageView saveNote, imageNote , headerPinImg;
    private View subTitleIndicator;
    private String color, imagePath, weblinkString, fireBaseItemId , firebaseImagePath;
    private TextView textWebUrl;
    private AlertDialog voice;
    private AlertDialog urlDialog, deleteDialog , checkboxDialog;
    private Note alreadyPresentNote;
    private LinearLayout layoutOfWeb, layoutOfImage;
    private AppCompatButton deleteWebUrl, deleteImage;
    private ImageView[] setOfIv;
    private Boolean archive , pin ;
    private LinearLayout checkBoxLlList ;
    private String checkBoxDisplayList;
    private ConstraintLayout rootLayout;
    private Uri imageOfNote;
    private int id ;
    Data_Model data = null;
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Base_Theme_OwnBook);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_creat_note);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

//      back button of the activity
        ImageView imageBack = findViewById(R.id.imageBack);
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


//        findViewById
        rootLayout = findViewById(R.id.main);
        title = findViewById(R.id.inputnotetitle);
        subtitle = findViewById(R.id.inputnotesubtitle);
        text = findViewById(R.id.inputNotes);
        dateTime = findViewById(R.id.textDataTime);
        saveNote = findViewById(R.id.imageSave);
        imageNote = findViewById(R.id.imagNotes);
        textWebUrl = findViewById(R.id.textWebUrl);
        layoutOfWeb = findViewById(R.id.layoutOfWebUrl);
        layoutOfImage = findViewById(R.id.layoutOfImageNote);
        deleteWebUrl = findViewById(R.id.deleteWebUrl);
        deleteImage = findViewById(R.id.deleteImage);
        checkBoxLlList = findViewById(R.id.checkboxList);
        subTitleIndicator = findViewById(R.id.viewSubtitleIndicator);
        imagePath = null;
        weblinkString = null;
        fireBaseItemId = null;
        firebaseImagePath = null;
        headerPinImg = findViewById(R.id.imagepin);
        imageOfNote = null;
        id = -1;


//        set the date and time  in the notes
        dateTime.setText(
                new SimpleDateFormat(" dd/MM/yyyy , hh:mm a", Locale.getDefault())
                        .format(new Date())
        );


//        for which work come to the activity like create/ update/ ....
        if (getIntent().getBooleanExtra("isViewOrUpdate", false)) {
            alreadyPresentNote = (Note) getIntent().getSerializableExtra("note");
            setViewOrUpdateData();
        }

        if (getIntent().getIntExtra("forImage", 0) == 4) {
            selectImage();
        }

        if (getIntent().getIntExtra("forUrl", 0) == 5) {
            showAddUrlDialog();
        }

        if (getIntent().getIntExtra("forAudio",  0) == 6) {
            audioToText();
        }
        if(getIntent().getIntExtra("forCheckBox",0) == 7){
            checkBoxCreator();
        }
        if(getIntent().getBooleanExtra("firebaseNote", false)){
             data = getIntent().getParcelableExtra("note");
            if(data == null){
                Log.d("verify" , "null" );
            }
            else {
                setView(data);
            }
        }


        if (alreadyPresentNote == null)
        {
            color = "#00ffffff";
            archive = false;
            pin = false;
            findViewById(R.id.imagedelete).setVisibility(View.GONE);
        }
        else {
            color = alreadyPresentNote.getColor();
            archive = alreadyPresentNote.getArchive();
            pin = alreadyPresentNote.getPin();
            findViewById(R.id.imagedelete).setVisibility(View.VISIBLE);
        }

//       work on the save button
        saveNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getIntent().getBooleanExtra("firebaseNote", false))
                {
                    if(user != null){
//                      for change in the firebase
                        String itemId = data.getFirebaseID();

                        if (imageOfNote != null) {
                            uploadToFirebase(imageOfNote);
                        }
                        else {
                            saveToDatabase(null, itemId);
                        }

//                      for change in room
                        if(data.getId() != -1)
                        {
                            final Note note = new Note();
                            note.setId(data.getId());
                            note.setTitle(title.getText().toString().trim());
                            note.setSubtitle(subtitle.getText().toString().trim());
                            note.setNoteText(text.getText().toString().trim());
                            note.setDateTime(dateTime.getText().toString().trim());
                            note.setColor(color);
                            note.setArchive(archive);
                            note.setPin(pin);
                            note.setFireBaseItemId(fireBaseItemId);


                            if (getAllCheckBoxHashMap(checkBoxLlList) != null) {
                                List<Pair<Boolean, String>> ListOfcheckbox = getAllCheckBoxHashMap(checkBoxLlList);
                                note.setCheckBoxListStr(getAllCheckBoxStringList(ListOfcheckbox));
                            }

                            if(imagePath != null){
                                note.setImagePath(imagePath);
                            }

                            if (weblinkString != null) {
                                note.setWebLink(weblinkString);
                            }

                            Executors.newSingleThreadExecutor().execute(new Runnable() {
                                @Override
                                public void run() {
                                    NotesDatabase.getDatabase(getApplicationContext()).noteDao().insertNotes(note);
                                }
                            });
                        }

                        Intent intent = new Intent(CreatNoteActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
                else
                    saveNotes();
                    Intent intent = new Intent(CreatNoteActivity.this, MainActivity.class);
                    startActivity(intent);
            }
        });

        findViewById(R.id.addCheckBox).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBoxCreator();
            }
        });

//        delete url
        deleteWebUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textWebUrl.setText(null);
                layoutOfWeb.setVisibility(View.GONE);
            }
        });


//        deleteimage
        deleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageNote.setImageBitmap(null);
                layoutOfImage.setVisibility(View.GONE);
                imagePath = null;
                imageOfNote=null;
            }
        });

//      opent the color bottomsheet
        findViewById(R.id.colorItmes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(CreatNoteActivity.this);
                bottomSheetDialog.setContentView(R.layout.layout_color_change);
                bottomSheetDialog.show();

                colorChanger(bottomSheetDialog);
            }
        });


//       open the add bottomsheet
        findViewById(R.id.addItmes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(CreatNoteActivity.this);
                bottomSheetDialog.setContentView(R.layout.layout_add_itemes);
                bottomSheetDialog.show();
                addInNotes(bottomSheetDialog);
            }
        });


//      open the other bottomsheet
        findViewById(R.id.otherItems).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(CreatNoteActivity.this);
                bottomSheetDialog.setContentView(R.layout.layout_other_option);
                bottomSheetDialog.show();
                if(alreadyPresentNote == null){
                    bottomSheetDialog.findViewById(R.id.layoutDeletNote).setVisibility(View.GONE);
                }
                else {
                    bottomSheetDialog.findViewById(R.id.layoutDeletNote).setVisibility(View.VISIBLE);
                }
                otherInNotes(bottomSheetDialog);
            }
        });


//        track the discription of the note
        text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
               Linkify.addLinks(s,Linkify.ALL);
            }
        });


//      delete note
        findViewById(R.id.imagedelete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteNoteDialog();
            }
        });

//       pin button
        headerPinImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!title.getText().toString().trim().isEmpty() &&
                        !subtitle.getText().toString().trim().isEmpty()) {
                    if (pin) {
                        pin = false;
//                        saveNotes();
                        headerPinImg.setImageResource(R.drawable.icon_pin_not_filled);
//                        Intent intent = new Intent(CreatNoteActivity.this, MainActivity.class);
//                        startActivity(intent);
                    } else {
                        pin = true;
//                        saveNotes();
                        headerPinImg.setImageResource(R.drawable.icon_pin_filled);
//                        Intent intent = new Intent(CreatNoteActivity.this, MainActivity.class);
//                        startActivity(intent);
                    }
                }
                else {
                    Toast.makeText(CreatNoteActivity.this, "Fill the require details...", Toast.LENGTH_SHORT).show();
                }
            }
        });

//        Archive Note in header
        findViewById(R.id.imageArchive).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!title.getText().toString().trim().isEmpty() &&
                        !subtitle.getText().toString().trim().isEmpty()) {

                    if (archive) {
                        Toast.makeText(CreatNoteActivity.this, "" + archive, Toast.LENGTH_SHORT).show();
                        archive = false;
                        saveNotes();
                        Intent intent = new Intent(CreatNoteActivity.this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(CreatNoteActivity.this, "" + archive, Toast.LENGTH_SHORT).show();
                        archive = true;
                        saveNotes();
                        Intent intent = new Intent(CreatNoteActivity.this, Archive_notes.class);
                        startActivity(intent);
                    }
                }
                else {
                    Toast.makeText(CreatNoteActivity.this, "Fill the require details...", Toast.LENGTH_SHORT).show();
                }
            }
        });


        ifAlreadyPresntNote();
    }


//    save the notes method
    private void saveNotes() {

        if (title.getText().toString().trim().isEmpty()) {
            title.setText("New Note "+ getInformation());
            saveinformation(getInformation());
        }
        if (subtitle.getText().toString().trim().isEmpty() ) {

            subtitle.setText("New Subtitle");
        }


        if(user != null){
            if (alreadyPresentNote != null) {
                fireBaseItemId = alreadyPresentNote.getFireBaseItemId();
                if (alreadyPresentNote.getImagePath() != null && imageOfNote != null) {
                    uploadToFirebase(imageOfNote);
                } else {
                    if(imageOfNote!=null)
                        saveToDatabase(imageOfNote.toString(), alreadyPresentNote.getFireBaseItemId());
                    else
                        saveToDatabase(null, alreadyPresentNote.getFireBaseItemId());
                }
            } else {
                if (imageOfNote != null) {
                    uploadToFirebase(imageOfNote);
//                    Toast.makeText(this, "in else if", Toast.LENGTH_SHORT).show();
                } else {
                    if(imageOfNote!=null)
                        saveToDatabase(imageOfNote.toString(), fireBaseItemId);
                    else
                        saveToDatabase(null, fireBaseItemId);
//                    Toast.makeText(this, "in else else", Toast.LENGTH_SHORT).show();
                }
            }
        }

            final Note note = new Note();
            note.setTitle(title.getText().toString().trim());
            note.setSubtitle(subtitle.getText().toString().trim());
            note.setNoteText(text.getText().toString().trim());
            note.setDateTime(dateTime.getText().toString().trim());
            note.setColor(color);
            note.setArchive(archive);
            note.setPin(pin);
            note.setFireBaseItemId(fireBaseItemId);

//        Toast.makeText(this, "colo="+color, Toast.LENGTH_SHORT).show();

            if (getAllCheckBoxHashMap(checkBoxLlList) != null) {
                List<Pair<Boolean, String>> ListOfcheckbox = getAllCheckBoxHashMap(checkBoxLlList);
                note.setCheckBoxListStr(getAllCheckBoxStringList(ListOfcheckbox));
            }

            if(imagePath != null){
                note.setImagePath(imagePath);
            }

            if (weblinkString != null) {
                note.setWebLink(weblinkString);
            }

            if (alreadyPresentNote != null) {
                note.setId(alreadyPresentNote.getId());
                id = alreadyPresentNote.getId();
            }
            else {
                id = note.getId();
            }

            Executors.newSingleThreadExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    NotesDatabase.getDatabase(getApplicationContext()).noteDao().insertNotes(note);
                }
            });

        finish();
    }


//    save new Note to data into the firebase database
    private void saveToDatabase(String uri , String itemIds)
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        Data_Model model ;
        String imageUri = null;
        if(uri != null){
            imageUri = uri;
        }
        Boolean archiv = archive;

        String titl = title.getText().toString();
        String subTitle = subtitle.getText().toString();
        String txt = text.getText().toString();
        String dateTi = dateTime.getText().toString();
        String webUrl = textWebUrl.getText().toString();
        String colo = color;
        boolean pi = pin;


        List<Pair<Boolean, String>> ListOfcheckbox = getAllCheckBoxHashMap(checkBoxLlList);
        String checklist = getAllCheckBoxStringList(ListOfcheckbox);

        if(user != null )
        {
            String userId = user.getUid();

            DatabaseReference dr = FirebaseDatabase.getInstance().getReference()
                    .child("users")
                    .child(userId)
                    .child("Notes");

            String itemId ;
            if(itemIds == null)
            {
                itemId = dr.push().getKey();
                fireBaseItemId = itemId;
            }
            else{
                itemId = itemIds;
            }

            model = new Data_Model(id,archiv,titl,dateTi,subTitle,txt,imageUri,colo,webUrl,pi,checklist,itemId);



            assert itemId != null;
            dr.child(itemId).setValue(model);
        }
    }

//   save the image into the database
    private void uploadToFirebase(Uri uri)
    {
        FirebaseApp.initializeApp(this);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user != null)
        {
            String userId = user.getUid();

            StorageReference sr = FirebaseStorage.getInstance().getReference()
                    .child("Note_Image")
                    .child(userId)
                    .child(Objects.requireNonNull(uri.getLastPathSegment()));

            sr.putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            sr.getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String ss = uri.toString();
                                            Log.d("path", ss);
//                                            Toast.makeText(CreatNoteActivity.this, ""+uri.getPath(), Toast.LENGTH_SHORT).show();
                                            saveToDatabase(ss,null);
//                                            Toast.makeText(CreatNoteActivity.this, "note save in db", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(CreatNoteActivity.this, "not abel to getDownload uri", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CreatNoteActivity.this, "not abel to upload uri", Toast.LENGTH_SHORT).show();
                        }
                    });

        }
    }

    //SharedPreferences for name
    private void saveinformation(int ii)
    {
        SharedPreferences sharedPreferences = getSharedPreferences("amount",0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        ii++;
        editor.putInt("amount",ii);
        editor.apply();
    }
    private int getInformation()
    {
        SharedPreferences sharedPreferences = getSharedPreferences("amount",0);
        return sharedPreferences.getInt("amount",1);
    }

    //    save archive notes
    private void saveArchiveNote(){

        if(!title.getText().toString().trim().isEmpty() &&
                !subtitle.getText().toString().trim().isEmpty())
        {
            archive = true;
            saveNotes();
            Intent intent = new Intent(CreatNoteActivity.this,Archive_notes.class);
            startActivity(intent);
        }
        else
        {
            Toast.makeText(CreatNoteActivity.this, "Text should not be empty", Toast.LENGTH_SHORT).show();
        }
    }

//  set view or update the data in the notes
    private void setViewOrUpdateData() {
        id = alreadyPresentNote.getId();
        title.setText(alreadyPresentNote.getTitle());
        subtitle.setText(alreadyPresentNote.getSubtitle());
        text.setText(alreadyPresentNote.getNoteText());
        dateTime.setText(alreadyPresentNote.getDateTime());
        if(alreadyPresentNote.getColor() != null){
            rootLayout.setBackgroundColor(Color.parseColor(alreadyPresentNote.getColor()));
        }

        if (alreadyPresentNote.getImagePath() != null && !alreadyPresentNote.getImagePath().toString().trim().isEmpty()) {
            imageNote.setImageBitmap(BitmapFactory.decodeFile(alreadyPresentNote.getImagePath()));

            imagePath = alreadyPresentNote.getImagePath();

            layoutOfImage.setVisibility(View.VISIBLE);
        }
        if (alreadyPresentNote.getWebLink() != null) {
            textWebUrl.setText(alreadyPresentNote.getWebLink());
            layoutOfWeb.setVisibility(View.VISIBLE);
        }
        if(!Objects.equals(alreadyPresentNote.getCheckBoxListStr(), "{}")){
            checkBoxDisplayList = alreadyPresentNote.getCheckBoxListStr();
            setAllCheckBoxList(checkBoxDisplayList);
        }
        if (alreadyPresentNote.getPin() == true){
            headerPinImg.setImageResource(R.drawable.icon_pin_filled);
        }
    }


//    set view of firebase notes
    private void setView(Data_Model data)
    {
        title.setText(data.getTitle());
        subtitle.setText(data.getSubtitle());
        text.setText(data.getNoteText());
        dateTime.setText(data.getDateTime());

        if(data.getColor() != null){
            rootLayout.setBackgroundColor(Color.parseColor(data.getColor()));
        }

        if (data.getImagePath() != null) {
            String imge = data.getImagePath();
//            Log.d("img Url", imge);
            Glide.with(this).load(imge).into(imageNote);


            layoutOfImage.setVisibility(View.VISIBLE);
        }
        if (!data.getWebLink().equals("")) {
            textWebUrl.setText(data.getWebLink());
            layoutOfWeb.setVisibility(View.VISIBLE);
        }
        if(!Objects.equals(data.getCheckBoxListStr(), "{}")){
            checkBoxDisplayList = data.getCheckBoxListStr();
            setAllCheckBoxList(checkBoxDisplayList);
        }
        if (data.getPin() == true){
            headerPinImg.setImageResource(R.drawable.icon_pin_filled);
        }
    }

    private void otherInNotes(BottomSheetDialog bsd)
    {
        bsd.findViewById(R.id.layoutOfShare).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!title.getText().toString().trim().isEmpty() &&
                        !subtitle.getText().toString().trim().isEmpty() &&
                        !text.getText().toString().trim().isEmpty()) {

                    bsd.dismiss();

                    String allData = title.getText().toString() + " \n \n" +
                                    subtitle.getText().toString() + " \n \n"+
                            text.getText().toString()  + " \n \n" ;

                    if(!dateTime.getText().toString().trim().isEmpty())
                    {
                        allData = allData + dateTime.getText().toString();
                    }

                    if (textWebUrl.getText().toString().trim().isEmpty()) {
                        allData = allData + textWebUrl.getText().toString()  + " \n \n" ;
                    }

                    if(!Objects.equals(alreadyPresentNote.getCheckBoxListStr(), "{}")){
                        allData = allData + "CheckBox List" + "\n";
                        LinearLayout ll = findViewById(R.id.checkboxList);
                        for(int i=0; i<ll.getChildCount(); i++)
                        {
                            View view = ll.getChildAt(i);
                            if(view instanceof CheckBox)
                            {
                                CheckBox checkBox = (CheckBox) view;
                                String tag = checkBox.getText().toString();
                                allData = allData + tag + "\n";
                            }
                        }

                    }

                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_TEXT , allData);
                    intent.setType("text/plain");
                    startActivity(Intent.createChooser(intent, null));
                }
                else {
                    Toast.makeText(CreatNoteActivity.this, "Fill the require details...", Toast.LENGTH_SHORT).show();
                    bsd.dismiss();
                }
            }

        });

        bsd.findViewById(R.id.layoutOfMakeCopy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alreadyPresentNote = null;
                saveNotes();
                if (archive)
                {
                    Intent intent = new Intent(CreatNoteActivity.this, Archive_notes.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                }
                else
                {
                    Intent intent = new Intent(CreatNoteActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                }

            }
        });

        bsd.findViewById(R.id.layoutDeletNote).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteNoteDialog();
            }
        });
        bsd.findViewById(R.id.layoutAddToArchiv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveArchiveNote();
                bsd.dismiss();
            }
        });
    }


    private  void addInNotes(BottomSheetDialog bds){
        bds.findViewById(R.id.layoutAddImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
                bds.dismiss();
            }
        });

        bds.findViewById(R.id.layoutAddUrl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddUrlDialog();
                bds.dismiss();
            }
        });

        bds.findViewById(R.id.layoutAddVoiceNote).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                audioToText();
                bds.dismiss();
            }
        });

        bds.findViewById(R.id.layoutAddCheakBox).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBoxCreator();
                bds.dismiss();
            }
        });
        checkBoxLlList.findViewById(R.id.addCheckBox).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBoxCreator();
            }
        });

    }


    private  void checkBoxCreator(){
        if(checkboxDialog == null) {
            AlertDialog.Builder checkboxbuilder = new AlertDialog.Builder(this);
            View view = LayoutInflater.from(this).inflate(R.layout.checkbox_dialog,null);
            checkboxbuilder.setView(view);

            checkboxDialog = checkboxbuilder.create();
            if(checkboxDialog.getWindow() != null)
            {
                checkboxDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }

            EditText edt = view.findViewById(R.id.checkboxTag);
            edt.requestFocus();

            view.findViewById(R.id.addButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String str = edt.getText().toString().trim();
                    if(!str.isEmpty())
                    {
                        CheckBox checkBox = new CheckBox(CreatNoteActivity.this);
                        LinearLayout.LayoutParams parameteres = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                        );
                        checkBox.setLayoutParams(parameteres);
                        checkBox.setPadding(20,16,0,16);
                        checkBox.setTextSize(20f);
                        checkBox.setGravity(Gravity.CENTER_VERTICAL);
                        checkBox.setText(str);

                        checkBoxLlList.addView(checkBox);

                        edt.setText("");
                        checkboxDialog.dismiss();
                        checkBoxLlList.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        Toast.makeText(CreatNoteActivity.this, "Enter Tag...", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            view.findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkboxDialog.dismiss();
                }
            });
        }
        checkboxDialog.show();
    }



    private void colorChanger(BottomSheetDialog bsd){

        setOfIv = new ImageView[]{
                bsd.findViewById(R.id.color2),
                bsd.findViewById(R.id.color3),
                bsd.findViewById(R.id.color4),
                bsd.findViewById(R.id.color5),
                bsd.findViewById(R.id.color6),
                bsd.findViewById(R.id.color7),
                bsd.findViewById(R.id.color8),
                bsd.findViewById(R.id.color9),
                bsd.findViewById(R.id.color10),
                bsd.findViewById(R.id.color11),
                bsd.findViewById(R.id.color12)
        };

        for(final ImageView imageView1 : setOfIv)
        {
            ColorStateList colorStateList = imageView1.getBackgroundTintList();
            if (colorStateList != null) {
                int color1 = colorStateList.getDefaultColor();
                String color2 = String.format("#%06X", (0xFFFFFF & color1));

                if (color2.equals(color)) {
                    imageView1.setImageResource(R.drawable.icon_done);
                    subTitleIndicator.setBackgroundColor(color1);
                }
            }
        }

        for(final ImageView imageView : setOfIv)
        {
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    for(ImageView clickedIv : setOfIv)
                    {
                        if(imageView == clickedIv)
                        {
                            clickedIv.setImageResource(R.drawable.icon_done);

                            ColorStateList colorStateList = imageView.getBackgroundTintList();
                            if (colorStateList != null) {
                                int color1 = colorStateList.getDefaultColor();
                                subTitleIndicator.setBackgroundColor(color1);
                                rootLayout.setBackgroundColor(color1);
                                color = String.format("#%06X",(0xFFFFFF & color1));
                            }
                        }
                        else
                        {
                            clickedIv.setImageDrawable(null);
                        }
                    }
                }
            });
        }
    }

    private void ifAlreadyPresntNote()
    {
        BottomSheetDialog bsd = new BottomSheetDialog(CreatNoteActivity.this);
        bsd.setContentView(R.layout.layout_color_change);

        setOfIv = new ImageView[]{
                bsd.findViewById(R.id.color2),
                bsd.findViewById(R.id.color3),
                bsd.findViewById(R.id.color4),
                bsd.findViewById(R.id.color5),
                bsd.findViewById(R.id.color6),
                bsd.findViewById(R.id.color7),
                bsd.findViewById(R.id.color8),
                bsd.findViewById(R.id.color9),
                bsd.findViewById(R.id.color10),
                bsd.findViewById(R.id.color11),
                bsd.findViewById(R.id.color12)
        };

        if (alreadyPresentNote != null && alreadyPresentNote.getColor() != null && !alreadyPresentNote.getColor().toString().trim().isEmpty()) {

            for(final ImageView imageView1 : setOfIv)
            {
                ColorStateList colorStateList = imageView1.getBackgroundTintList();
                if (colorStateList != null) {
                    int color1 = colorStateList.getDefaultColor();
                    String color2 = String.format("#%06X", (0xFFFFFF & color1));

                }
            }
        }
    }


//  delete dialog
    private void showDeleteNoteDialog() {
        if (deleteDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View view = LayoutInflater.from(this).inflate(
                    R.layout.delete_diloge, null
            );
            builder.setView(view);

            deleteDialog = builder.create();
            if (deleteDialog.getWindow() != null) {
                deleteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }

            view.findViewById(R.id.deleteButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Executors.newSingleThreadExecutor().execute(new Runnable() {
                        @Override
                        public void run() {
                            NotesDatabase.getDatabase(getApplicationContext()).noteDao()
                                    .deleteNote(alreadyPresentNote);

//                            delete from firebase
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if(user != null)
                            {
                                String userId = user.getUid();
                                DatabaseReference dr = FirebaseDatabase.getInstance().getReference()
                                        .child("users")
                                        .child(userId)
                                        .child("Notes");
                                String noteId = alreadyPresentNote.getFireBaseItemId();
                                Log.d("noteID", noteId);
                                dr.child(noteId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(CreatNoteActivity.this, "deleted ", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });

                    Intent intent = new Intent(CreatNoteActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                 }
            });

            view.findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteDialog.dismiss();
                }
            });
        }
        deleteDialog.show();
    }

//    select imgae
    @SuppressLint("QueryPermissionsNeeded")
    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 2);
    }

    private String imagePathFormUri(Uri contentUri) {
        String path = "";
        Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            path = contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex("_data");
            path = cursor.getString(index);
            cursor.close();
        }
        Toast.makeText(this, ""+path, Toast.LENGTH_SHORT).show();
        return path;
    }

    private void showAddUrlDialog() {
        if (urlDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View view = LayoutInflater.from(this).inflate(
                    R.layout.url_diloge, null
            );
            builder.setView(view);

            urlDialog = builder.create();
            if (urlDialog.getWindow() != null) {
                urlDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }

            EditText enterdurl = view.findViewById(R.id.alrtWebUrl);
            enterdurl.requestFocus();

            view.findViewById(R.id.addButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(CreatNoteActivity.this, ""+enterdurl, Toast.LENGTH_SHORT).show();
                    if (enterdurl.getText().toString().trim().isEmpty()) {
                        Toast.makeText(CreatNoteActivity.this, "Enter the Url", Toast.LENGTH_SHORT).show();
                    } else if (!Patterns.WEB_URL.matcher(enterdurl.getText().toString()).matches()) {
                        Toast.makeText(CreatNoteActivity.this, "Enter the Valid Url", Toast.LENGTH_SHORT).show();
                    } else {
                        textWebUrl.setText(enterdurl.getText().toString());
                        layoutOfWeb.setVisibility(View.VISIBLE);
                        weblinkString = enterdurl.getText().toString();
                        enterdurl.setText("");
                        urlDialog.dismiss();

                    }
                }
            });
            view.findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    enterdurl.setText("");
                    urlDialog.dismiss();
                }
            });
        }
        urlDialog.show();
    }

    public void audioToText() {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        startActivityForResult(intent,0);

    }

    private  List<Pair<Boolean,String>> getAllCheckBoxHashMap(LinearLayout ll)
    {
        List<Pair<Boolean,String>> list = new ArrayList<>();

        for(int i=0; i<ll.getChildCount(); i++)
        {
            View view = ll.getChildAt(i);
            if(view instanceof CheckBox)
            {
                CheckBox checkBox = (CheckBox) view;
                boolean check = checkBox.isChecked();
                String tag = checkBox.getText().toString();
                list.add(new Pair<>(check,tag));
            }
        }
        return list;
    }

    private String getAllCheckBoxStringList(List<Pair<Boolean,String>> list){
        if(list != null){
            StringBuilder sb = new StringBuilder();
            sb.append("{");
            for (Pair<Boolean, String> entry : list) {
                sb.append(entry.first).append("=").append(entry.second).append(", ");
            }
            // Remove the extra ", " at the end
            if (sb.length() > 2) {
                sb.setLength(sb.length() - 2);
            }
            sb.append("}");
            return sb.toString();
        }
        else {
            return null;
        }
    }
    private void setAllCheckBoxList(String checkBoxList){

        if (checkBoxList == null || checkBoxList.isEmpty() || checkBoxList.length() < 3) { // Check if the input string is valid
                return;
        }

        // Remove curly braces from the input string
        checkBoxList = checkBoxList.substring(1, checkBoxList.length() - 1);

        // Split the string by commas to get key-value pairs
        String[] pairs = checkBoxList.split(", ");

        for (String pair : pairs) {
            // Split each pair by "=" to get the boolean value and string
            CheckBox checkBox = new CheckBox(CreatNoteActivity.this);
            LinearLayout.LayoutParams parameteres = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            checkBox.setLayoutParams(parameteres);
            checkBox.setPadding(20,16,0,16);
            checkBox.setTextSize(20f);
            checkBox.setGravity(Gravity.CENTER_VERTICAL);
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                boolean key = Boolean.parseBoolean(keyValue[0]);
                String value = keyValue[1];
                checkBox.setChecked(key);
                checkBox.setText(value);
                checkBoxLlList.addView(checkBox);
            }
        }
        checkBoxLlList.setVisibility(View.VISIBLE);
    }





//  Override the built in methodes
    @Override
    public void onBackPressed() {
        if(archive)
        {
            Intent intent = new Intent(CreatNoteActivity.this, Archive_notes.class);
            startActivity(intent);
        }
        if(getIntent().getBooleanExtra("firebaseNote",false))
        {
            Intent intent = new Intent(CreatNoteActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        else {
            Intent intent = new Intent(CreatNoteActivity.this, MainActivity.class);
            startActivity(intent);
        }

        super.onBackPressed();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectImage();
            }
        } else {
            Toast.makeText(this, "Permission Deny", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && resultCode == RESULT_OK) {
            if (data != null) {

                imageOfNote = data.getData(); /////////////////////////////////////////////////////////////////

                Uri selectedImagaeUri = data.getData();
                if (imageOfNote != null) {

                    Glide.with(this).load(imageOfNote).into(imageNote);

                    imageNote.setVisibility(View.VISIBLE);
                    layoutOfImage.setVisibility(View.VISIBLE);
                    firebaseImagePath = data.getData().getPath();
//                    Toast.makeText(this, ""+selectedImagaeUri, Toast.LENGTH_SHORT).show();
                        imagePath = imagePathFormUri(selectedImagaeUri);
                }
            }
        }
        if(requestCode == 0 && resultCode == RESULT_OK)
        {
            List<String>  res = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            String spokenText = res.get(0);
            text.setText(spokenText);

        }
    }

}














