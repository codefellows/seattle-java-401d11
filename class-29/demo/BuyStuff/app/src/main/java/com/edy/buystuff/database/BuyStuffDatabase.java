package com.edy.buystuff.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.edy.buystuff.models.ShoppingItem;

@Database(entities = {ShoppingItem.class}, version = 2)
@TypeConverters({BuyStuffDatabaseConverters.class})
public abstract class BuyStuffDatabase extends RoomDatabase
{
    public abstract ShoppingItemDao shoppingItemDao();
}
