package com.example.android.bakingapp.objects;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by adamzarn on 8/16/17.
 */

public class Ingredient implements Parcelable {

    private String quantity;
    private String measure;
    private String item;

    public Ingredient(String quantity, String measure, String item) {
        this.quantity = quantity;
        this.measure = measure;
        this.item = item;
    }

    public Ingredient(Parcel parcel) {
        this.quantity = parcel.readString();
        this.measure = parcel.readString();
        this.item = parcel.readString();
    }

    public String getQuantity() { return quantity; }

    public String getMeasure() {
        return measure;
    }

    public String getItem() {
        return item;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(quantity);
        dest.writeString(measure);
        dest.writeString(item);
    }

    public static final Parcelable.Creator<Ingredient> CREATOR
            = new Parcelable.Creator<Ingredient>() {

        public Ingredient createFromParcel(Parcel in) {
            return new Ingredient(in);
        }

        public Ingredient[] newArray(int size) {
            return new   Ingredient[size];
        }
    };

}
