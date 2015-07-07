package me.danco.spotifystreamer;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public final class ArtistAdapter extends ArrayAdapter<ArtistParcelable> {
    private final Context context;
    private final int layoutResourceId;
    private ArrayList<ArtistParcelable> artists = null;

    public ArtistAdapter(Context context, @SuppressWarnings("SameParameterValue") int layoutResourceId, ArrayList<ArtistParcelable> artists) {
        super(context, layoutResourceId, artists);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.artists = artists;
    }

    @Override
    public View getView(int position, View row, ViewGroup parent) {
        if (row == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(this.layoutResourceId, parent, false);
        }

        ArtistParcelable artist = artists.get(position);
        row.setTag(artist);

        ((TextView)row.findViewById(R.id.list_item_artist_name)).setText(artist.name);
        if (artist.thumbnail != null) {
            Picasso.with(this.context).load(artist.thumbnail).into((ImageView)row.findViewById(R.id.list_item_artist_thumbnail));
        } else {
            ((ImageView)row.findViewById(R.id.list_item_artist_thumbnail)).setImageResource(R.drawable.ic_broken_image_black_48dp);
        }

        return row;
    }
}
