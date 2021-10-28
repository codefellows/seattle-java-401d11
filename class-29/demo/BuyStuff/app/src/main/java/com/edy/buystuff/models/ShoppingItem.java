package com.edy.buystuff.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity
public class ShoppingItem
{
    @PrimaryKey(autoGenerate = true)
    public Long id;
    public String itemName;
    public java.util.Date timeAdded;
    public ProductCategoryEnum productCategory;

    public ShoppingItem(String itemName, Date timeAdded)
    {
        this.itemName = itemName;
        this.timeAdded = timeAdded;
        this.productCategory = ProductCategoryEnum.UNKNOWN;
    }

    @Override
    @NonNull
    public String toString()
    {
        return itemName + "\nTime Added=" + timeAdded + "\nItem Category=" + productCategory.getProductCategoryString();
    }
}
