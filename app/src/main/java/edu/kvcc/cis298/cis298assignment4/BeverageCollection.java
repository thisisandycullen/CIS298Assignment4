//ANDY CULLEN
//ASSIGNMENT 4
//DUE 12/16/15

package edu.kvcc.cis298.cis298assignment4;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by David Barnes on 11/3/2015.
 * This is a singleton that will store the data for our application
 */
public class BeverageCollection {

    //Static variable that represents this class
    private static BeverageCollection sBeverageCollection;

    //List to store all of the beverages
    private List<Beverage> mBeverages;

    //public static method to get the single instance of this class
    public static BeverageCollection get() {
        //If the collection is null
        if (sBeverageCollection == null) {
            //make a new one
            sBeverageCollection = new BeverageCollection();
        }
        //regardless of whether it was just made or not, return the instance
        return sBeverageCollection;
    }

    //Private constructor to create a new BeverageCollection
    private BeverageCollection() {
        //Make a new list to hold the beverages
        mBeverages = new ArrayList<>();
    }

    //Getters
    public List<Beverage> getBeverages() {
        return mBeverages;
    }

    public Beverage getBeverage(String Id) {
        for (Beverage beverage : mBeverages) {
            if (beverage.getId().equals(Id)) {
                return beverage;
            }
        }
        return null;
    }

    public void addBeverage(Beverage beverage) {
        mBeverages.add(beverage);
    }
}
