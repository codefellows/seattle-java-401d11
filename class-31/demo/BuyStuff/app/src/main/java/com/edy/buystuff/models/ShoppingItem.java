package com.edy.buystuff.models;

import androidx.annotation.NonNull;

import java.util.Date;

// TODO: This will disappear soon
public class ShoppingItem
{
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

    public String toString()
    {
        return itemName + "\nTime Added=" + timeAdded + "\nItem Category=" + productCategory.getProductCategoryString();
    }
}
