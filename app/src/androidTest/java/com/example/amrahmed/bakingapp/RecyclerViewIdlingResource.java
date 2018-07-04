package com.example.amrahmed.bakingapp;

import android.support.test.espresso.IdlingResource;
import com.example.amrahmed.bakingapp.Activities.MainActivity;
import java.util.Observable;

public class RecyclerViewIdlingResource extends Observable implements IdlingResource {

    private ResourceCallback  resourceCallback;


    @Override
    public String getName() {
        return RecyclerViewIdlingResource.class.getName();
    }

    @Override
    public boolean isIdleNow() {

        boolean idle = MainActivity.callbackFinished;

        if (idle) { resourceCallback.onTransitionToIdle(); }

        return idle;
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback callback) {
        this.resourceCallback = callback;
    }
}
