package com.example.amrahmed.bakingapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.amrahmed.bakingapp.Activities.RecipeDetailsActivity;
import com.example.amrahmed.bakingapp.Modules.Recipe;
import com.example.amrahmed.bakingapp.R;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

/**
 * Created by amr ahmed on 5/20/2018.
 */

public class RecipesAdapter extends RecyclerView.Adapter<RecipesAdapter.ViewHolder> {

    private ArrayList<Recipe> Recipe_list;
    private Context context;

    public RecipesAdapter(ArrayList<Recipe> Movies_Array_list) {
        this.Recipe_list =Movies_Array_list;
    }


    public  class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final ImageView recipeImage;
        final TextView recipeName;

        private ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            recipeImage=itemView.findViewById(R.id.recipe_image);
            recipeName=itemView.findViewById(R.id.recipe_name);
        }


        //overriding onclick to send clicked item data to the detail activity
        @Override
        public void onClick(View v) {

            Intent intent = new Intent(context, RecipeDetailsActivity.class);

            String RECIPE_KEY=context.getResources().getString(R.string.RECIPE_EXTRAS_KEY);
            intent.putExtra(RECIPE_KEY,Recipe_list.get(getAdapterPosition()));


            int posttion=getAdapterPosition();
            String img=Recipe_list.get(posttion).getImage();
            String HANDLED_IMG_KEY=context.getResources().getString(R.string.HANDLED_IMG_KEY);
            intent.putExtra(HANDLED_IMG_KEY,HandleRecipeImg(img,posttion));


            context.startActivity(intent);

        }

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        context=parent.getContext();
        int item_layout=R.layout.recipe_item_view;
        View Recipe_item = LayoutInflater.from(context).inflate(item_layout, parent, false);
        return new ViewHolder(Recipe_item);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String img=Recipe_list.get(position).getImage();

        try {
            Picasso.get()
                    .load(HandleRecipeImg(img,position))
                    .error(R.drawable.splashimg)
                    .into(holder.recipeImage);
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.recipeName.setText(Recipe_list.get(position).getName()+" ");


    }
    @Override
    public int getItemCount() {
        if (Recipe_list==null) return 0;
        return Recipe_list.size();
    }

    private String HandleRecipeImg(String Img, int position){

        if(Img.equals("")) {

            String alternative = null;
            Resources r = context.getResources();

            if (position <= 3) {
                switch (position) {
                    case 0:
                        alternative = r.getString(R.string.pic_1);
                        break;
                    case 1:
                        alternative = r.getString(R.string.pic_2);
                        break;
                    case 2:
                        alternative = r.getString(R.string.pic_3);
                        break;
                    case 3:
                        alternative = r.getString(R.string.pic_4);
                        break;
                }return alternative;
            }
            else {
                alternative = r.getString(R.string.pic_N);
                return alternative; }

        } else{ return Img; }

}

}
