package me.danco.spotifystreamer;

import android.os.Parcel;
import android.os.Parcelable;

public class TrackParcelable implements Parcelable {
    public String id;
    public String title;
    public String albumThumbnail;
    public String albumTitle;

    public TrackParcelable(String id, String title, String albumThumbnail, String albumTitle) {
        this.id = id;
        this.title = title;
        this.albumThumbnail = albumThumbnail;
        this.albumTitle = albumTitle;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.title);
        dest.writeString(this.albumThumbnail);
        dest.writeString(this.albumTitle);
    }

    private TrackParcelable(Parcel in) {
        this.id = in.readString();
        this.title = in.readString();
        this.albumThumbnail = in.readString();
        this.albumTitle = in.readString();
    }

    public static final Creator<TrackParcelable> CREATOR = new Creator<TrackParcelable>() {
        @Override
        public TrackParcelable createFromParcel(Parcel source) {
            return new TrackParcelable(source);
        }

        @Override
        public TrackParcelable[] newArray(int size) {
            return new TrackParcelable[size];
        }
    };
}
