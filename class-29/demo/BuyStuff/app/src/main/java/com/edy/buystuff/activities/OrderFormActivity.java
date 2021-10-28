package com.edy.buystuff.activities;

import static com.edy.buystuff.activities.MainActivity.DATABASE_INSTANCE_NAME;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.edy.buystuff.R;
import com.edy.buystuff.database.BuyStuffDatabase;
import com.edy.buystuff.models.ProductCategoryEnum;
import com.edy.buystuff.models.ShoppingItem;

public class OrderFormActivity extends AppCompatActivity
{
    BuyStuffDatabase buyStuffDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_form);

        buyStuffDatabase = Room.databaseBuilder(getApplicationContext(), BuyStuffDatabase.class, DATABASE_INSTANCE_NAME)
                .allowMainThreadQueries()  // bit hacky, but we'll ignore this problem in our small app
                .build();

        Intent intent = getIntent();
        long productId = intent.getLongExtra(MainActivity.PRODUCT_ID_EXTRA_STRING, -1);
        ShoppingItem shoppingItem = buyStuffDatabase.shoppingItemDao().findById(productId);

        TextView productNameOrderFormTextView = findViewById(R.id.productNameOrderFormTextView);
        productNameOrderFormTextView.setText(shoppingItem.itemName);

        Spinner productCategorySpinner = findViewById(R.id.productCategorySpinner);
        // TODO: values are not the nice human-readable values, fix this
        productCategorySpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, ProductCategoryEnum.values()));
        int spinnerPosition = getSpinnerIndex(productCategorySpinner, shoppingItem.productCategory.toString());
        productCategorySpinner.setSelection(spinnerPosition);

        Button saveShoppingItemButton = findViewById(R.id.saveShoppingItemButton);
        saveShoppingItemButton.setOnClickListener(button ->
            {
                shoppingItem.productCategory = ProductCategoryEnum.valueOf(productCategorySpinner.getSelectedItem().toString());
                buyStuffDatabase.shoppingItemDao().update(shoppingItem);
            }
        );
    }

    private int getSpinnerIndex(Spinner spinner, String stringValueToCheck){
        for (int i = 0;i < spinner.getCount(); i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(stringValueToCheck)){
                return i;
            }
        }

        return 0;
    }
}