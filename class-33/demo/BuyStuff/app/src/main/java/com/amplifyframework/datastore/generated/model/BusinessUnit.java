package com.amplifyframework.datastore.generated.model;

import com.amplifyframework.core.model.annotations.HasMany;
import com.amplifyframework.core.model.temporal.Temporal;

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

/** This is an auto generated class representing the BusinessUnit type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "BusinessUnits")
public final class BusinessUnit implements Model {
  public static final QueryField ID = field("BusinessUnit", "id");
  public static final QueryField BUSINESS_UNIT_NAME = field("BusinessUnit", "businessUnitName");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="String") String businessUnitName;
  private final @ModelField(targetType="ShoppingItem") @HasMany(associatedWith = "businessUnit", type = ShoppingItem.class) List<ShoppingItem> shoppingItems = null;
  private @ModelField(targetType="AWSDateTime", isReadOnly = true) Temporal.DateTime createdAt;
  private @ModelField(targetType="AWSDateTime", isReadOnly = true) Temporal.DateTime updatedAt;
  public String getId() {
      return id;
  }
  
  public String getBusinessUnitName() {
      return businessUnitName;
  }
  
  public List<ShoppingItem> getShoppingItems() {
      return shoppingItems;
  }
  
  public Temporal.DateTime getCreatedAt() {
      return createdAt;
  }
  
  public Temporal.DateTime getUpdatedAt() {
      return updatedAt;
  }
  
  private BusinessUnit(String id, String businessUnitName) {
    this.id = id;
    this.businessUnitName = businessUnitName;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      BusinessUnit businessUnit = (BusinessUnit) obj;
      return ObjectsCompat.equals(getId(), businessUnit.getId()) &&
              ObjectsCompat.equals(getBusinessUnitName(), businessUnit.getBusinessUnitName()) &&
              ObjectsCompat.equals(getCreatedAt(), businessUnit.getCreatedAt()) &&
              ObjectsCompat.equals(getUpdatedAt(), businessUnit.getUpdatedAt());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getBusinessUnitName())
      .append(getCreatedAt())
      .append(getUpdatedAt())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("BusinessUnit {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("businessUnitName=" + String.valueOf(getBusinessUnitName()) + ", ")
      .append("createdAt=" + String.valueOf(getCreatedAt()) + ", ")
      .append("updatedAt=" + String.valueOf(getUpdatedAt()))
      .append("}")
      .toString();
  }
  
  public static BuildStep builder() {
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
  public static BusinessUnit justId(String id) {
    return new BusinessUnit(
      id,
      null
    );
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(id,
      businessUnitName);
  }
  public interface BuildStep {
    BusinessUnit build();
    BuildStep id(String id);
    BuildStep businessUnitName(String businessUnitName);
  }
  

  public static class Builder implements BuildStep {
    private String id;
    private String businessUnitName;
    @Override
     public BusinessUnit build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new BusinessUnit(
          id,
          businessUnitName);
    }
    
    @Override
     public BuildStep businessUnitName(String businessUnitName) {
        this.businessUnitName = businessUnitName;
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
    private CopyOfBuilder(String id, String businessUnitName) {
      super.id(id);
      super.businessUnitName(businessUnitName);
    }
    
    @Override
     public CopyOfBuilder businessUnitName(String businessUnitName) {
      return (CopyOfBuilder) super.businessUnitName(businessUnitName);
    }
  }
  
}
