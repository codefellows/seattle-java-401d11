package com.edy.buystuff.models;

public enum ProductCategoryEnum
{
    CLOTHES("Clothes"),
    PERSONAL_ELECTRONICS("Personal Electronics"),
    FOOD("Food"),
    UNKNOWN("Unknown");

    private final String productCategoryString;

    @Override
    public String toString()
    {
        return productCategoryString;
    }

    ProductCategoryEnum(String productCategoryString)
    {
        this.productCategoryString = productCategoryString;
    }

    public static ProductCategoryEnum fromString(String inputProductCategoryText) {
        for (ProductCategoryEnum productCategory : ProductCategoryEnum.values()) {
            if (productCategory.productCategoryString.equalsIgnoreCase(inputProductCategoryText)) {
                return productCategory;
            }
        }
        return null;
    }
}
