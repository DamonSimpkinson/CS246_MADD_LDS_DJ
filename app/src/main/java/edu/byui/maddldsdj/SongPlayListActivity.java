package edu.byui.maddldsdj;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

public class SongPlayListActivity extends AppCompatActivity {

    public static final String SONG_EXTRA = "song_extra";
    private static final String USERPREF = "UserPref";

    private Song _song;
    private Button btnVoteUp, btnVoteDown, btnRemove;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_play_list);

        // Buttons
        btnVoteUp = (Button) findViewById(R.id.btnvoteup);
        btnVoteDown = (Button) findViewById(R.id.btnvotedown);
        btnRemove = (Button) findViewById(R.id.btnremoveplaylist);
        setButtonVisibility();

        // Get Song from the Intent
        Intent intent = getIntent();
        String songJson = intent.getStringExtra(SONG_EXTRA);

        // Materialize the Song
        Gson gson = new Gson();
        Song song = gson.fromJson(songJson, Song.class);

        // Populate views from the Song
        TextView songText = (TextView)findViewById(R.id.playSongTitle);
        TextView artistText = (TextView)findViewById(R.id.playSongArtist);
        TextView albumText = (TextView)findViewById(R.id.playSongAlbum);
        TextView voteText = (TextView)findViewById(R.id.votes);
        songText.setText(song.getTitle());
        artistText.setText(song.getArtist());
        albumText.setText(song.getAlbum());
        voteText.setText("2");  // change this to DB later
        //Toast.makeText(this, "User signed_in:" + firebaseUser.getEmail(), Toast.LENGTH_SHORT).show();

        // Save the Song so we can easily request it if need be
        _song = song;
    }

   private void setButtonVisibility() {
       // get admin from Shared Preferences
       SharedPreferences userPreferences = getSharedPreferences(USERPREF, Context.MODE_PRIVATE);
        boolean useradmin = userPreferences.getBoolean("userAdmin", false);
       // if admin show remove song button and hide vote buttons
        if (useradmin) {
            btnRemove.setVisibility(View.VISIBLE);
            btnVoteUp.setVisibility(View.INVISIBLE);
            btnVoteDown.setVisibility(View.INVISIBLE);
            // if NOT admin show vote buttons and hide remove song button
        } else {
            btnRemove.setVisibility(View.INVISIBLE);
            btnVoteUp.setVisibility(View.VISIBLE);
            btnVoteDown.setVisibility(View.VISIBLE);
        }
    }
}