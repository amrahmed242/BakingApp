package com.example.amrahmed.bakingapp.Activities;

import android.databinding.DataBindingUtil;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import com.example.amrahmed.bakingapp.Adapters.RecipesAdapter;
import com.example.amrahmed.bakingapp.Modules.Recipe;
import com.example.amrahmed.bakingapp.R;
import com.example.amrahmed.bakingapp.Utilities.API;
import com.example.amrahmed.bakingapp.databinding.ActivityMainBinding;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private Call<ArrayList<Recipe>> recipeCall = null;
    private Callback<ArrayList<Recipe>> callback = null;
    private static ArrayList<Recipe> recipesList=null;
    public static Boolean callbackFinished=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        //check if data is loaded, if not we call the api
        if (recipesList == null) {
            Loading_Animation();
            callRetrofit();
        } else {
            Initiate_UI();
            binding.splashscreen.setVisibility(View.GONE);
            binding.loadingIcon.setVisibility(View.GONE);
            binding.appName.setVisibility(View.GONE);
        }

    }


    //splash screen animation
    private void Loading_Animation(){

        Animation blinkanimation= new AlphaAnimation(.1f, .9f); // Change alpha from fully visible to invisible
        blinkanimation.setDuration(1500); // duration
        blinkanimation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
        blinkanimation.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
        blinkanimation.setRepeatMode(Animation.REVERSE);
        binding.loadingIcon.setAnimation(blinkanimation);

    }

    //splash fade animation
    private void OnResponse_Animation(){

        binding.splashscreen.animate().alpha(0).setDuration(2000);
        binding.appName.animate().alpha(0).setDuration(2000);
        binding.loadingIcon.animate().alpha(0).setDuration(2000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.splashscreen.setVisibility(View.GONE);
                binding.appName.setVisibility(View.GONE);
                binding.loadingIcon.setVisibility(View.GONE);

            }
        }, 2500);
    }

    //api call
    private void callRetrofit(){

        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(API.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        final API api=retrofit.create(API.class);


        recipeCall=api.getRecipes();
        callback=new Callback<ArrayList<Recipe>>(){
            @Override
            public void onResponse(@NonNull Call<ArrayList<Recipe>> call, @NonNull Response<ArrayList<Recipe>> response) {

                recipesList = response.body();
                Initiate_UI();
                OnResponse_Animation();
                callbackFinished=true;


            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<Recipe>> call, @NonNull Throwable t) {

                Toast.makeText(getApplicationContext(),"check connection",Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        recipeCall=api.getRecipes();
                        recipeCall.enqueue(callback);

                    }
                }, 5000);

            }
        };

        recipeCall.enqueue(callback);

    }

    //load data in the UI
    private void Initiate_UI(){

        RecipesAdapter recipesAdapter=new RecipesAdapter(recipesList);
        GridLayoutManager gridLayoutManager=new GridLayoutManager(this,calculateBestSpanCount()) ;

        binding.Recylcerview.setHasFixedSize(true);
        binding.Recylcerview.setLayoutManager(gridLayoutManager);
        binding.Recylcerview.setAdapter(recipesAdapter);


    }

    //responsive column count
    private int calculateBestSpanCount() {
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        float screenWidth = outMetrics.widthPixels;
        return Math.round(screenWidth / 900);
    }


}


