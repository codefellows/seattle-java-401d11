package com.edy.buystuff.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.edy.buystuff.models.ShoppingItem;

import java.util.List;

@Dao
public interface ShoppingItemDao
{
    @Insert
    long insert(ShoppingItem shoppingItem); // return type can be void or long or Long

    @Query("SELECT * FROM ShoppingItem")
    List<ShoppingItem> findAll();

    @Query("SELECT * FROM ShoppingItem WHERE id = :id")
    ShoppingItem findById(long id);

    @Update
    void update(ShoppingItem shoppingItem);
}
