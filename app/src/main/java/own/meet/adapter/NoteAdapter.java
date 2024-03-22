package own.meet.adapter;

import static own.meet.adapter.NoteAdapter.NoteViewHolder;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import own.meet.Model.Data_Model;
import own.meet.notes.Note;
import own.meet.notesListning.NotesListner;
import own.meet.ownbook.R;

public class NoteAdapter extends RecyclerView.Adapter<NoteViewHolder> {

    private List<Note> notes , searchdir;
    private NotesListner notesListner;
    Timer timer;
    public NoteAdapter(List<Note> notes, NotesListner notesListner) {
        this.notes = notes;
        this.notesListner = notesListner;
        searchdir = notes;
    }


    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NoteViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_container_notes,parent,false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {

            holder.setNote(notes.get(position) );
            holder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    notesListner.noteOnClick(notes.get(position), holder.getAdapterPosition());
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

    public static class NoteViewHolder extends RecyclerView.ViewHolder{

        TextView title , subTitle , dateTime;
        LinearLayout layout;
        RoundedImageView imageNote;
        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            
            title = itemView.findViewById(R.id.textTitel);
            subTitle = itemView.findViewById(R.id.textSubtitel);
            dateTime = itemView.findViewById(R.id.textDataTime);
            layout = itemView.findViewById(R.id.layoutNote);
            imageNote = itemView.findViewById(R.id.imageNote);
        }



        void setNote(Note note)
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

            if(note.getColor() != null)
            {
                GradientDrawable gradientDrawable = (GradientDrawable) layout.getBackground();
                gradientDrawable.setColor(Color.parseColor(note.getColor()));
            }

            if(note.getImagePath() != null)
            {
                Glide.with(layout.getContext()).load(note.getImagePath()).into(imageNote);
                imageNote.setVisibility(View.VISIBLE);
            }
            else {
                imageNote.setVisibility(View.GONE);
            }
        }
    }

    public void searchNotes(final String searchKey)
    {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(searchKey.trim().isEmpty())
                {
                    notes = searchdir;
                }
                else
                {
                    ArrayList<Note> list = new ArrayList<>();
                    for(Note note : searchdir)
                    {
                        if(note.getTitle().toLowerCase().contains(searchKey.toLowerCase())
                        || note.getSubtitle().toLowerCase().contains(searchKey.toLowerCase())
                         || note.getNoteText().toLowerCase().contains(searchKey.toLowerCase())){
                            list.add(note);
                        }
                    }
                    notes = list;
                }
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });
            }
        },0);
    }

    public void cancalTimer()
    {
        if(timer != null)
        {
            timer.cancel();
        }
    }

}











