package own.meet.dao;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;
import own.meet.notes.Note;

@Dao
public interface NoteDao {

    @Query("SELECT * FROM note WHERE NOT archive AND NOT pin ORDER BY id DESC")
    List<Note> getAllNotes();

    @Query("SELECT * FROM note WHERE archive ORDER BY id DESC")
    List<Note> getArchiveNotes();

    @Query("SELECT * FROM note WHERE NOT archive AND pin ORDER BY id DESC")
    List<Note> getPinNotes();

    @Query("SELECT * FROM note WHERE color IS NOT NULL ORDER BY id DESC")
    List<Note> getColorCNotes();


    @Query("SELECT * FROM note WHERE image_path IS NOT NULL ORDER BY id DESC")
    List<Note> getImageNotes();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertNotes(Note note);

    @Delete
    void deleteNote(Note note);
}

