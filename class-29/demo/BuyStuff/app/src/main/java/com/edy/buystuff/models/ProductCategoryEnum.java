package com.edy.buystuff.models;

public enum ProductCategoryEnum
{
    CLOTHES("Clothes"),
    PERSONAL_ELECTRONICS("Personal Electronics"),
    FOOD("Food"),
    UNKNOWN("Unknown");

    private final String productCategoryString;

    ProductCategoryEnum(String productCategoryString)
    {
        this.productCategoryString = productCategoryString;
    }

    public String getProductCategoryString()
    {
        return productCategoryString;
    }
}
