package me.danco.spotifystreamer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
    private ArrayList<TrackParcelable> trackList;

    public TopTracksActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ConnectivityManager connectivityManager = (ConnectivityManager) this.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (savedInstanceState == null || !savedInstanceState.containsKey("tracks")) {
            this.trackList = new ArrayList<>();
            if (networkInfo == null || !networkInfo.isConnected()) {
                Toast.makeText(this.getActivity(), this.getActivity().getString(R.string.no_connection), Toast.LENGTH_LONG).show();
            } else {
                new TopTracksTask().execute(getActivity().getIntent().getStringExtra("artistId"));
            }

            Log.v(TopTracksActivityFragment.class.getName(), "Request from network");
        } else {
            this.trackList = savedInstanceState.getParcelableArrayList("tracks");
            Log.v(TopTracksActivityFragment.class.getName(), "pull from saved instance state");
        }

        this.trackAdapter = new TrackAdapter(getActivity(), R.layout.list_item_track, this.trackList);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("tracks", trackList);
        super.onSaveInstanceState(outState);
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

    private class TopTracksTask extends AsyncTask<String, Void, ArrayList<TrackParcelable>> {
        @Override
        protected ArrayList<TrackParcelable> doInBackground(String... params) {
            SpotifyApi api = new SpotifyApi();
            SpotifyService spotify = api.getService();
            ArrayList<TrackParcelable> tracks = new ArrayList<>();


            HashMap<String, Object> queryMap = new HashMap<>();
            queryMap.put("country", "US");
            try {
                Tracks topTracks = spotify.getArtistTopTrack(params[0], queryMap);

                for (Track track : topTracks.tracks) {
                    tracks.add(new TrackParcelable(track.id, track.name, track.album.images.size() > 0 ? track.album.images.get(0).url : null, track.album.name));
                }
            } catch (Exception ex) {
                Log.d(TopTracksTask.class.getName(), ex.toString());
            }

            return tracks;
        }

        @Override
        protected void onPostExecute(ArrayList<TrackParcelable> tracks) {
            super.onPostExecute(tracks);

            trackAdapter.clear();
            if (tracks.size() == 0) {
                Toast.makeText(getActivity(), getActivity().getString(R.string.tracks_no_connection), Toast.LENGTH_LONG).show();
            } else {
                trackAdapter.addAll(tracks);
            }
        }
    }
}
