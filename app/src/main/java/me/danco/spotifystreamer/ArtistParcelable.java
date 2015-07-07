package me.danco.spotifystreamer;

import android.os.Parcel;
import android.os.Parcelable;

public class ArtistParcelable implements Parcelable {
    public String id;
    public String name;
    public String thumbnail;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.thumbnail);
    }

    public ArtistParcelable(String id, String name, String thumbnail) {
        this.id = id;
        this.name = name;
        this.thumbnail = thumbnail;
    }

    public static final Creator<ArtistParcelable> CREATOR = new Creator<ArtistParcelable>() {
        @Override
        public ArtistParcelable createFromParcel(Parcel source) {
            return new ArtistParcelable(source);
        }

        @Override
        public ArtistParcelable[] newArray(int size) {
            return new ArtistParcelable[size];
        }
    };

    private ArtistParcelable(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.thumbnail = in.readString();
    }
}
