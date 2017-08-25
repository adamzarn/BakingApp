package com.example.android.bakingapp.objects;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by adamzarn on 8/16/17.
 */

public class Recipe implements Parcelable {

    private String id;
    private String name;
    private Ingredient[] ingredients;
    private Step[] steps;
    private String servings;
    private String image;

    public Recipe(String id, String name, Ingredient[] ingredients, Step[] steps, String servings, String image) {
        this.id = id;
        this.name = name;
        this.ingredients = ingredients;
        this.steps = steps;
        this.servings = servings;
        this.image = image;
    }

    public Recipe(Parcel parcel) {
        this.id = parcel.readString();
        this.name = parcel.readString();
        this.ingredients = parcel.createTypedArray(Ingredient.CREATOR);
        this.steps = parcel.createTypedArray(Step.CREATOR);
        this.servings = parcel.readString();
        this.image = parcel.readString();
    }

    public String getID() { return id; }

    public String getName() {
        return name;
    }

    public Ingredient[] getIngredients() {
        return ingredients;
    }

    public Step[] getSteps() {
        return steps;
    }

    public String getServings() {
        return servings;
    }

    public String getImage() {
        return image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeTypedArray(ingredients, 0);
        dest.writeTypedArray(steps, 0);
        dest.writeString(servings);
        dest.writeString(image);
    }

    public static final Parcelable.Creator<Recipe> CREATOR
            = new Parcelable.Creator<Recipe>() {

        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };

}
