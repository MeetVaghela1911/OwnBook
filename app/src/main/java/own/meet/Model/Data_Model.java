package own.meet.Model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Data_Model implements Parcelable {

    public Data_Model() {

    }

    public Data_Model(String title, String subtitle, String noteText) {
        this.title = title;
        this.subtitle = subtitle;
        this.noteText = noteText;
    }

    public Data_Model(int id, boolean archive, String title, String dateTime, String subtitle, String noteText, String imagePath, String color, String webLink, boolean pin, String checkBoxListStr, String firebaseID, boolean collabrative , String otherUserList) {

        this.id = id;
        this.archive = archive;
        this.title = title;
        this.dateTime = dateTime;
        this.subtitle = subtitle;
        this.noteText = noteText;
        this.imagePath = imagePath;
        this.color = color;
        this.webLink = webLink;
        this.pin = pin;
        this.checkBoxListStr = checkBoxListStr;
        this.firebaseID = firebaseID;
        this.collabrative = collabrative;
        this.otherUserList = otherUserList;
    }

    private int id;
    private boolean archive;
    private String title;
    private String dateTime;
    private String subtitle;
    private String noteText;
    private String imagePath;
    private String color;



    private String webLink;
    private boolean pin;
    private String checkBoxListStr;
    private String firebaseID;
    private boolean collabrative;
    private String otherUserList;


    protected Data_Model(Parcel in) {
        id = in.readInt();
        archive = in.readByte() != 0;
        title = in.readString();
        dateTime = in.readString();
        subtitle = in.readString();
        noteText = in.readString();
        imagePath = in.readString();;
        color = in.readString();
        webLink = in.readString();
        pin = in.readByte() != 0;
        checkBoxListStr = in.readString();
        firebaseID = in.readString();
        collabrative = in.readByte() != 0;
        otherUserList = in.readString();
    }

    public static final Creator<Data_Model> CREATOR = new Creator<Data_Model>() {
        @Override
        public Data_Model createFromParcel(Parcel in) {
            return new Data_Model(in);
        }

        @Override
        public Data_Model[] newArray(int size) {
            return new Data_Model[size];
        }
    };

    public String getFirebaseID() {
        return firebaseID;
    }

    public void setFirebaseID(String firebaseID) {
        this.firebaseID = firebaseID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isArchive() {
        return archive;
    }

    public void setArchive(boolean archive) {
        this.archive = archive;
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

    public boolean getCollabrative() {
        return collabrative;
    }

    public void setCollabrative(boolean collabrative) {
        this.collabrative = collabrative;
    }

    public String getOtherUserList() {
        return otherUserList;
    }

    public void setOtherUserList(String otherUserList) {
        this.otherUserList = otherUserList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeByte((byte) (archive ? 1 : 0));
        dest.writeString(title);
        dest.writeString(dateTime);
        dest.writeString(subtitle);
        dest.writeString(noteText);
        dest.writeString(imagePath);
        dest.writeString(color);
        dest.writeString(webLink);
        dest.writeByte((byte) (pin ? 1 : 0));
        dest.writeString(checkBoxListStr);
        dest.writeString(firebaseID);
        dest.writeByte((byte) (collabrative ? 1 : 0));
        dest.writeString(otherUserList);
    }
}
