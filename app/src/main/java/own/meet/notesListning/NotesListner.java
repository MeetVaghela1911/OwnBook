package own.meet.notesListning;

import own.meet.Model.Data_Model;
import own.meet.notes.Note;

public interface NotesListner {
    void noteOnClick(Note note, int position);
}
