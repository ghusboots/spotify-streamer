package me.danco.spotifystreamer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;


/**
 * A placeholder fragment containing a simple view.
 */
public class TopTracksActivityFragment extends Fragment {
    private TrackAdapter trackAdapter;

    public TopTracksActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRetainInstance(true);
        this.trackAdapter = new TrackAdapter(getActivity(), R.layout.list_item_track, new ArrayList<Track>());
        new TopTracksTask().execute(getActivity().getIntent().getStringExtra("artistId"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_top_tracks, container, false);
        Intent intent = getActivity().getIntent();

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setSubtitle(intent.getStringExtra("artistName"));
        }

        ListView trackList = (ListView)rootView.findViewById(R.id.list_tracks);
        trackList.setAdapter(trackAdapter);

        return rootView;
    }

    private class TopTracksTask extends AsyncTask<String, Void, ArrayList<Track>> {
        @Override
        protected ArrayList<Track> doInBackground(String... params) {
            SpotifyApi api = new SpotifyApi();
            SpotifyService spotify = api.getService();
            ArrayList<Track> tracks = new ArrayList<>();


            HashMap<String, Object> queryMap = new HashMap<>();
            queryMap.put("country", "US");
            try {
                Tracks topTracks = spotify.getArtistTopTrack(params[0], queryMap);

                for (Track track : topTracks.tracks) {
                    tracks.add(track);
                }
            } catch (Exception ex) {
                Log.d(TopTracksTask.class.getName(), ex.toString());
            }

            return tracks;
        }

        @Override
        protected void onPostExecute(ArrayList<Track> tracks) {
            super.onPostExecute(tracks);

            trackAdapter.clear();
            if (tracks.size() == 0) {
                Toast.makeText(getActivity(), getActivity().getString(R.string.tracks_no_connection), Toast.LENGTH_LONG).show();
            } else {
                trackAdapter.addAll(tracks);
            }
        }
    }

    @SuppressWarnings("SameParameterValue")
    public final class TrackAdapter extends ArrayAdapter<Track> {
        private final Context context;
        private final int layoutResourceId;
        private ArrayList<Track> tracks = null;

        public TrackAdapter(Context context, int layoutResourceId, ArrayList<Track> tracks) {
            super(context, layoutResourceId, tracks);
            this.context = context;
            this.layoutResourceId = layoutResourceId;
            this.tracks = tracks;
        }

        @Override
        public View getView(int position, View row, ViewGroup parent) {
            if (row == null) {
                LayoutInflater inflater = ((Activity)context).getLayoutInflater();
                row = inflater.inflate(this.layoutResourceId, parent, false);
            }

            Track track = tracks.get(position);
            row.setTag(track);

            ((TextView)row.findViewById(R.id.list_item_track_title)).setText(track.name);
            ((TextView)row.findViewById(R.id.list_item_track_album)).setText(track.album.name);

            if (track.album.images.size() > 0) {
                Picasso.with(this.context).load(track.album.images.get(0).url).into((ImageView)row.findViewById(R.id.list_item_track_thumbnail));
            } else {
                ((ImageView)row.findViewById(R.id.list_item_track_thumbnail)).setImageResource(R.drawable.ic_broken_image_black_48dp);
            }

            return row;
        }
    }

}
