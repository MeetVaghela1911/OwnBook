package own.meet.adapter;


import static androidx.core.app.ActivityCompat.startActivityForResult;

import static own.meet.ownbook.MainActivity.REQUEST_CODE_UPDATE_NOTE;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialDialogs;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

import own.meet.Model.Data_Model;
import own.meet.notes.Note;
import own.meet.notesListning.FirebaseDataListning;
import own.meet.notesListning.NotesListner;
import own.meet.ownbook.CreatNoteActivity;
import own.meet.ownbook.R;

public class FirewallAdapter extends RecyclerView.Adapter<FirewallAdapter.NoteViewHolder2>{

    List<Data_Model> FireWallnotes;
    private FirebaseDataListning firebaseDataListning;
    Context context;

    public FirewallAdapter(List<Data_Model> fireWallnotes,FirebaseDataListning firebaseDataListning, Context context) {
        FireWallnotes = fireWallnotes;
        this.context = context;
        this.firebaseDataListning = firebaseDataListning;
    }

    @NonNull
    @Override
    public NoteViewHolder2 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FirewallAdapter.NoteViewHolder2(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_container_notes,parent,false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder2 holder, int position) {
            holder.setNote(FireWallnotes.get(position));

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Data_Model data = FireWallnotes.get(position);

                            firebaseDataListning.OnDataClick(data, position);

                }

            });

    }

    @Override
    public int getItemCount() {
        return FireWallnotes.size();
    }

    public static class NoteViewHolder2 extends RecyclerView.ViewHolder{

        TextView title , subTitle , dateTime;
        LinearLayout layout, collabrativeNotelayout;
        RoundedImageView imageNote;
        public NoteViewHolder2(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.textTitel);
            subTitle = itemView.findViewById(R.id.textSubtitel);
            dateTime = itemView.findViewById(R.id.textDataTime);
            layout = itemView.findViewById(R.id.layoutNote);
            imageNote = itemView.findViewById(R.id.imageNote);
            collabrativeNotelayout = itemView.findViewById(R.id.collabrative_note_layout);
        }

        void setNote(Data_Model note)
        {
            title.setText(note.getTitle());
            if(note.getSubtitle().trim().isEmpty())
            {
                subTitle.setVisibility(View.GONE);
            }
            else {
                subTitle.setText(note.getSubtitle());
            }
            dateTime.setText(note.getDateTime());

            GradientDrawable gradientDrawable = (GradientDrawable) layout.getBackground();
            if(note.getColor() != null)
            {
                gradientDrawable.setColor(Color.parseColor(note.getColor()));
            }
            if(note.getImagePath() != null)
            {
                Uri image = Uri.parse(note.getImagePath());
                Glide.with(layout.getContext()).load(image).into(imageNote);
                imageNote.setVisibility(View.VISIBLE);
            }
            else {
                imageNote.setVisibility(View.GONE);
            }
            if(note.getCollabrative())
            {
                collabrativeNotelayout.setVisibility(View.VISIBLE);
            }
        }
    }


}
