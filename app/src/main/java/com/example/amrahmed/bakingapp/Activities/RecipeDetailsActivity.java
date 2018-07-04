package com.example.amrahmed.bakingapp.Activities;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.example.amrahmed.bakingapp.Adapters.IngredientsAdapter;
import com.example.amrahmed.bakingapp.Modules.Ingredients;
import com.example.amrahmed.bakingapp.Modules.Recipe;
import com.example.amrahmed.bakingapp.R;
import com.example.amrahmed.bakingapp.databinding.ActivityRecipeDetailsBinding;
import com.example.amrahmed.bakingapp.widget.NewAppWidget;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class RecipeDetailsActivity extends AppCompatActivity {
    private Recipe recipe;
    private Resources r;
    private ActivityRecipeDetailsBinding binding;
    private ArrayList<Ingredients> ingredients;
    private String img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_recipe_details);

        final Intent intent=getIntent();
        if(intent.getExtras() != null){

            initializeUI(intent);

            initializeAdapter();

            FabClickListners();

        }

    }


    private void initializeAdapter(){
        IngredientsAdapter ingredientsAdapter=new IngredientsAdapter(ingredients);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this) ;
        binding.ingredients.setHasFixedSize(true);
        binding.ingredients.setLayoutManager(linearLayoutManager);
        binding.ingredients.setAdapter(ingredientsAdapter);
        }

    private void initializeUI(Intent intent){

        r=this.getResources();
        String HANDLED_IMG_KEY=r.getString(R.string.HANDLED_IMG_KEY);
        recipe=intent.getParcelableExtra(r.getString(R.string.RECIPE_EXTRAS_KEY));

        ingredients=recipe.getIngredients();
        img=intent.getStringExtra(HANDLED_IMG_KEY);

        try {
            Picasso.get()
                    .load(img)
                    .error(R.drawable.splashimg)
                    .into(binding.DetailImg);
        } catch (Exception e) {
            e.printStackTrace();
        }

        binding.DetailRecipeName.setText(recipe.getName()+" ");
        binding.ServingsNumber.setText(String.valueOf(recipe.getServings()));

    }

    //fab buttons ClickListners
    private void FabClickListners(){

        //Fab button to lunch the stepListActivity
        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(getApplicationContext(),stepListActivity.class);
                String RECIPE_KEY=getResources().getString(R.string.RECIPE_EXTRAS_KEY);
                intent.putParcelableArrayListExtra(RECIPE_KEY,recipe.getSteps());
                getApplicationContext().startActivity(intent);

            }
        });


        //favourites Fab button to add the recipe to the home widget
        binding.favouritesFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addToShared();
                triggerWidgetUpdate();

                Snackbar snackbar = Snackbar
                        .make(findViewById(R.id.coordinatorLayout), "Added to widget", Snackbar.LENGTH_LONG);
                snackbar.show();

            }
        });

    }

    //add the recipe to the shared preferences
    private void addToShared(){

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        String WIDGET_PIC_KEY= getResources().getString(R.string.WIDGET_PIC_KEY);
        String WIDGET_NAME_KEY = getResources().getString(R.string.WIDGET_NAME_KEY);
        String WIDGET_INGREDIENTS_KEY = getResources().getString(R.string.WIDGET_INGREDIENTS_KEY);

        StringBuilder ingredientsSet=new StringBuilder();

        for(int i=0;i<ingredients.size();i++){

            String s1=ingredients.get(i).getIngredient();
            String s2=String.valueOf(ingredients.get(i).getQuantity());
            String s3=ingredients.get(i).getMeasure();

            ingredientsSet.append(s1+"   ");
            ingredientsSet.append(s2);
            ingredientsSet.append(s3);
            ingredientsSet.append(" \n");
        }

        String ingredients= ingredientsSet.toString();


        editor.putString(WIDGET_PIC_KEY, img);
        editor.putString(WIDGET_NAME_KEY, recipe.getName());
        editor.putString(WIDGET_INGREDIENTS_KEY, ingredients);

        editor.apply();


    }

    //triggers the home widget Update function
    private void triggerWidgetUpdate(){

        Intent intent = new Intent(getApplicationContext(), NewAppWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

        // Use an array and EXTRA_APPWIDGET_IDS instead of AppWidgetManager.EXTRA_APPWIDGET_ID,
        // since it seems the onUpdate() is only fired on that:
        int[] ids = AppWidgetManager.getInstance(getApplication())
                .getAppWidgetIds(new ComponentName(getApplication(), NewAppWidget.class));

        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        sendBroadcast(intent);

    }

}
