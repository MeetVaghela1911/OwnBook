package own.meet.notes;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;


@Entity
public class Note implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "fireBaseItemId")
    private String fireBaseItemId ;

    @ColumnInfo(name = "archive")
    private boolean archive;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "date_time")
    private String dateTime;

    @ColumnInfo(name = "subtitle")
    private String subtitle;

    @ColumnInfo(name = "note_text")
    private String noteText;

    @ColumnInfo(name = "image_path")
    private String imagePath;
    @ColumnInfo(name = "color")
    private String color;

    @ColumnInfo(name = "web_link")
    private String webLink;

    @ColumnInfo(name = "pin")
    private boolean pin;

    @ColumnInfo(name = "checkBoxList")
    private String checkBoxListStr;










    public String getFireBaseItemId() {
        return fireBaseItemId;
    }

    public void setFireBaseItemId(String fireBaseItemId) {
        this.fireBaseItemId = fireBaseItemId;
    }

    public boolean getPin() {
        return pin;
    }
    public void setPin(boolean pin) {
        this.pin = pin;
    }

    public String getCheckBoxListStr() {
        return checkBoxListStr;
    }

    public void setCheckBoxListStr(String checkBoxListStr) {
        this.checkBoxListStr = checkBoxListStr;
    }
    public boolean getArchive() {
        return archive;
    }

    public void setArchive(boolean archive) {
        this.archive = archive;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getNoteText() {
        return noteText;
    }

    public void setNoteText(String noteText) {
        this.noteText = noteText;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getWebLink() {
        return webLink;
    }

    public void setWebLink(String webLink) {
        this.webLink = webLink;
    }

    @NonNull
    @Override
    public String toString(){
        return title + " : " + dateTime;
    }
}
