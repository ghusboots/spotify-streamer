package me.danco.spotifystreamer;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * A placeholder fragment containing a simple view.
 */
public class SearchActivityFragment extends Fragment {
    ArrayAdapter<String> artistAdapter;

    public SearchActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        artistAdapter = new ArrayAdapter<>(getActivity(), R.layout.list_item_artist, R.id.list_item_artist_textview, new ArrayList<String>());
        artistAdapter.add("blah 1");
        artistAdapter.add("Blah 2");

        View rootView = inflater.inflate(R.layout.fragment_search, container, false);

        ListView artistList = (ListView)rootView.findViewById(R.id.list_artists);
        artistList.setAdapter(artistAdapter);

        EditText searchBox = (EditText) rootView.findViewById(R.id.edit_search);
        searchBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    //getActivity();
                    Log.v(getClass().getName(), "Enter pressed");
                } else {
                    Log.v(getClass().getName(), "nope");
                }

                return true;
            }
        });

        return rootView;
    }
}
