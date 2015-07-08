package me.danco.spotifystreamer;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Executors;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.android.MainThreadExecutor;
import retrofit.client.Response;


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
                SpotifyApi api = new SpotifyApi(Executors.newSingleThreadExecutor(), new MainThreadExecutor());
                SpotifyService spotify = api.getService();

                HashMap<String, Object> queryMap = new HashMap<>();
                queryMap.put("country", "US");
                spotify.getArtistTopTrack(getActivity().getIntent().getStringExtra("artistId"), queryMap, new Callback<Tracks>() {
                    @Override
                    public void success(Tracks tracks, Response response) {
                        if (tracks.tracks.size() > 0) {
                            for (Track track : tracks.tracks) {
                                trackList.add(new TrackParcelable(track.id, track.name, track.album.images.size() > 0 ? track.album.images.get(0).url : null, track.album.name));
                            }

                            trackAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getActivity(), getActivity().getString(R.string.tracks_no_connection), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void failure(RetrofitError retrofitError) {
                        Log.e("Top tracks failure", retrofitError.toString());
                        Toast.makeText(getActivity(), getActivity().getString(R.string.tracks_no_connection), Toast.LENGTH_LONG).show();
                    }
                });
            }
        } else {
            this.trackList = savedInstanceState.getParcelableArrayList("tracks");
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
}
