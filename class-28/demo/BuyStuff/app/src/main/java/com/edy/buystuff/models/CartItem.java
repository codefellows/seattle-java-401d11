package com.edy.buystuff.models;

import androidx.annotation.NonNull;

import java.util.Date;

// Step 1-4: Create a data model class, and create that data by hand
public class CartItem
{
    public String itemName;
    public java.util.Date timeAdded;

    public CartItem(String itemName, Date timeAdded)
    {
        this.itemName = itemName;
        this.timeAdded = timeAdded;
    }

    @Override
    @NonNull
    public String toString()
    {
        return itemName + "\nTime Added=" + timeAdded;
    }
}
