package com.example.amrahmed.bakingapp.Activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.amrahmed.bakingapp.Fragments.stepDetailFragment;
import com.example.amrahmed.bakingapp.Modules.Steps;
import com.example.amrahmed.bakingapp.R;
import com.example.amrahmed.bakingapp.databinding.ActivityStepDetailBinding;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import java.util.ArrayList;

/**
 * An activity representing a single step detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link stepListActivity}.
 */
public class stepDetailActivity extends AppCompatActivity {
    private ActivityStepDetailBinding B;
    private SimpleExoPlayer exoPlayer;
    private ArrayList<Steps> steps;
    private int position;
    private static long playerPosition= C.TIME_UNSET;
    private String videoURL;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        B= DataBindingUtil.setContentView(this,R.layout.activity_step_detail);

        Intent intent=getIntent();
        if(intent.getExtras() != null){
            steps=intent.getParcelableArrayListExtra(getResources().getString(R.string.Step_Detail_Activity_KEY));
            position=intent.getIntExtra("position",0);
        }

        if(savedInstanceState != null){
            position = savedInstanceState.getInt("position");
            }

        //check if app in landscape mode
        if(B.exoPlayerViewFull != null){

            landscapeMode();

        }else{

            // savedInstanceState is non-null when there is fragment state
            // saved from previous configurations of this activity
            // (e.g. when rotating the screen from portrait to landscape).
            // In this case, the fragment will automatically be re-added
            // to its container so we don't need to manually add it.
            // For more information, see the Fragments API guide at:
            // http://developer.android.com/guide/components/fragments.html
            portraitMode(savedInstanceState);

        }

    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt("position",position);
        super.onSaveInstanceState(savedInstanceState);
    }


    @Override
    protected void onPause() {
        if(exoPlayer !=null){
            playerPosition=exoPlayer.getCurrentPosition();
        }
        super.onPause();
    }


    @Override
    protected void onDestroy() {
        if(exoPlayer !=null){
            exoPlayer.release();
            exoPlayer.stop();
        }
        super.onDestroy();
    }

    private void landscapeMode(){

        //go full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if(getSupportFragmentManager().findFragmentById(R.id.step_detail_container) != null) {
            getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().
                    findFragmentById(R.id.step_detail_container)).commit();
            Log.i("xxx","f removed"+String.valueOf(position));

        }

        Log.i("xxx","L pos "+String.valueOf(position));


        if(steps.get(position).getVideoURL().isEmpty()){
            B.placeholder.setVisibility(View.VISIBLE);
            B.placeholder.setAlpha(1f);

        }else {

            B.placeholder.setAlpha(0f);
            B.placeholder.setVisibility(View.GONE);

            try {

                BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
                TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
                exoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector);

                videoURL = steps.get(position).getVideoURL();
                Uri videoURI = Uri.parse(videoURL);

                DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("exo_player_video");
                ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
                MediaSource mediaSource = new ExtractorMediaSource(videoURI, dataSourceFactory, extractorsFactory, null, null);


                B.exoPlayerViewFull.setPlayer(exoPlayer);
                exoPlayer.prepare(mediaSource);
                exoPlayer.setPlayWhenReady(true);
                if(playerPosition != C.TIME_UNSET){ exoPlayer.seekTo(playerPosition); }

            } catch (Exception e) {
                Log.e("DetailActivity", "exoplayer error" + e.toString());
            }

        }



    }

    private void portraitMode(Bundle savedInstanceState){


        B.Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(position < steps.size()-1){
                    position=position+1;

                    Bundle arguments = new Bundle();
                    arguments.putParcelable(getResources().getString(R.string.Step_Detail_Fragment_KEY), steps.get(position));
                    stepDetailFragment fragment = new stepDetailFragment();
                    fragment.setArguments(arguments);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.step_detail_container, fragment)
                            .commit();
                }
            }
        });

        B.Prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(position > 0) {
                    position = position - 1;

                    Bundle arguments = new Bundle();
                    arguments.putParcelable(getResources().getString(R.string.Step_Detail_Fragment_KEY), steps.get(position));
                    stepDetailFragment fragment = new stepDetailFragment();
                    fragment.setArguments(arguments);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.step_detail_container, fragment)
                            .commit();
                }
            }
        });

        if (savedInstanceState == null || B.exoPlayerViewFull == null) {

            // Create the detail fragment and add it to the activity
            Bundle arguments = new Bundle();
            Log.i("xxx","f pos "+String.valueOf(position));
            arguments.putParcelable(getResources().getString(R.string.Step_Detail_Fragment_KEY), steps.get(position));
            arguments.putLong(getResources().getString(R.string.Player_Position_KEY),playerPosition);
            stepDetailFragment fragment = new stepDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.step_detail_container, fragment)
                    .commit();

        }

    }


    public void setPlayerPosition(Long playerPosition){
        this.playerPosition=playerPosition;

    }


}
