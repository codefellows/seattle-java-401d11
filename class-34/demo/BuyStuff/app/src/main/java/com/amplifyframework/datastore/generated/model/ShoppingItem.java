package com.amplifyframework.datastore.generated.model;

import com.amplifyframework.core.model.temporal.Temporal;
import com.amplifyframework.core.model.annotations.BelongsTo;

import java.util.List;
import java.util.UUID;
import java.util.Objects;

import androidx.core.util.ObjectsCompat;

import com.amplifyframework.core.model.Model;
import com.amplifyframework.core.model.annotations.Index;
import com.amplifyframework.core.model.annotations.ModelConfig;
import com.amplifyframework.core.model.annotations.ModelField;
import com.amplifyframework.core.model.query.predicate.QueryField;

import static com.amplifyframework.core.model.query.predicate.QueryField.field;

/** This is an auto generated class representing the ShoppingItem type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "ShoppingItems")
@Index(name = "byOurItem", fields = {"businessUnitId"})
public final class ShoppingItem implements Model {
  public static final QueryField ID = field("ShoppingItem", "id");
  public static final QueryField ITEM_NAME = field("ShoppingItem", "itemName");
  public static final QueryField TIME_ADDED = field("ShoppingItem", "timeAdded");
  public static final QueryField PRODUCT_CATEGORY = field("ShoppingItem", "productCategory");
  public static final QueryField BUSINESS_UNIT = field("ShoppingItem", "businessUnitId");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="String") String itemName;
  private final @ModelField(targetType="AWSDateTime") Temporal.DateTime timeAdded;
  private final @ModelField(targetType="String") String productCategory;
  private final @ModelField(targetType="BusinessUnit", isRequired = true) @BelongsTo(targetName = "businessUnitId", type = BusinessUnit.class) BusinessUnit businessUnit;
  private @ModelField(targetType="AWSDateTime", isReadOnly = true) Temporal.DateTime createdAt;
  private @ModelField(targetType="AWSDateTime", isReadOnly = true) Temporal.DateTime updatedAt;
  public String getId() {
      return id;
  }
  
  public String getItemName() {
      return itemName;
  }
  
  public Temporal.DateTime getTimeAdded() {
      return timeAdded;
  }
  
  public String getProductCategory() {
      return productCategory;
  }
  
  public BusinessUnit getBusinessUnit() {
      return businessUnit;
  }
  
  public Temporal.DateTime getCreatedAt() {
      return createdAt;
  }
  
  public Temporal.DateTime getUpdatedAt() {
      return updatedAt;
  }
  
  private ShoppingItem(String id, String itemName, Temporal.DateTime timeAdded, String productCategory, BusinessUnit businessUnit) {
    this.id = id;
    this.itemName = itemName;
    this.timeAdded = timeAdded;
    this.productCategory = productCategory;
    this.businessUnit = businessUnit;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      ShoppingItem shoppingItem = (ShoppingItem) obj;
      return ObjectsCompat.equals(getId(), shoppingItem.getId()) &&
              ObjectsCompat.equals(getItemName(), shoppingItem.getItemName()) &&
              ObjectsCompat.equals(getTimeAdded(), shoppingItem.getTimeAdded()) &&
              ObjectsCompat.equals(getProductCategory(), shoppingItem.getProductCategory()) &&
              ObjectsCompat.equals(getBusinessUnit(), shoppingItem.getBusinessUnit()) &&
              ObjectsCompat.equals(getCreatedAt(), shoppingItem.getCreatedAt()) &&
              ObjectsCompat.equals(getUpdatedAt(), shoppingItem.getUpdatedAt());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getItemName())
      .append(getTimeAdded())
      .append(getProductCategory())
      .append(getBusinessUnit())
      .append(getCreatedAt())
      .append(getUpdatedAt())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("ShoppingItem {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("itemName=" + String.valueOf(getItemName()) + ", ")
      .append("timeAdded=" + String.valueOf(getTimeAdded()) + ", ")
      .append("productCategory=" + String.valueOf(getProductCategory()) + ", ")
      .append("businessUnit=" + String.valueOf(getBusinessUnit()) + ", ")
      .append("createdAt=" + String.valueOf(getCreatedAt()) + ", ")
      .append("updatedAt=" + String.valueOf(getUpdatedAt()))
      .append("}")
      .toString();
  }
  
  public static BusinessUnitStep builder() {
      return new Builder();
  }
  
  /** 
   * WARNING: This method should not be used to build an instance of this object for a CREATE mutation.
   * This is a convenience method to return an instance of the object with only its ID populated
   * to be used in the context of a parameter in a delete mutation or referencing a foreign key
   * in a relationship.
   * @param id the id of the existing item this instance will represent
   * @return an instance of this model with only ID populated
   */
  public static ShoppingItem justId(String id) {
    return new ShoppingItem(
      id,
      null,
      null,
      null,
      null
    );
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(id,
      itemName,
      timeAdded,
      productCategory,
      businessUnit);
  }
  public interface BusinessUnitStep {
    BuildStep businessUnit(BusinessUnit businessUnit);
  }
  

  public interface BuildStep {
    ShoppingItem build();
    BuildStep id(String id);
    BuildStep itemName(String itemName);
    BuildStep timeAdded(Temporal.DateTime timeAdded);
    BuildStep productCategory(String productCategory);
  }
  

  public static class Builder implements BusinessUnitStep, BuildStep {
    private String id;
    private BusinessUnit businessUnit;
    private String itemName;
    private Temporal.DateTime timeAdded;
    private String productCategory;
    @Override
     public ShoppingItem build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new ShoppingItem(
          id,
          itemName,
          timeAdded,
          productCategory,
          businessUnit);
    }
    
    @Override
     public BuildStep businessUnit(BusinessUnit businessUnit) {
        Objects.requireNonNull(businessUnit);
        this.businessUnit = businessUnit;
        return this;
    }
    
    @Override
     public BuildStep itemName(String itemName) {
        this.itemName = itemName;
        return this;
    }
    
    @Override
     public BuildStep timeAdded(Temporal.DateTime timeAdded) {
        this.timeAdded = timeAdded;
        return this;
    }
    
    @Override
     public BuildStep productCategory(String productCategory) {
        this.productCategory = productCategory;
        return this;
    }
    
    /** 
     * @param id id
     * @return Current Builder instance, for fluent method chaining
     */
    public BuildStep id(String id) {
        this.id = id;
        return this;
    }
  }
  

  public final class CopyOfBuilder extends Builder {
    private CopyOfBuilder(String id, String itemName, Temporal.DateTime timeAdded, String productCategory, BusinessUnit businessUnit) {
      super.id(id);
      super.businessUnit(businessUnit)
        .itemName(itemName)
        .timeAdded(timeAdded)
        .productCategory(productCategory);
    }
    
    @Override
     public CopyOfBuilder businessUnit(BusinessUnit businessUnit) {
      return (CopyOfBuilder) super.businessUnit(businessUnit);
    }
    
    @Override
     public CopyOfBuilder itemName(String itemName) {
      return (CopyOfBuilder) super.itemName(itemName);
    }
    
    @Override
     public CopyOfBuilder timeAdded(Temporal.DateTime timeAdded) {
      return (CopyOfBuilder) super.timeAdded(timeAdded);
    }
    
    @Override
     public CopyOfBuilder productCategory(String productCategory) {
      return (CopyOfBuilder) super.productCategory(productCategory);
    }
  }
  
}
