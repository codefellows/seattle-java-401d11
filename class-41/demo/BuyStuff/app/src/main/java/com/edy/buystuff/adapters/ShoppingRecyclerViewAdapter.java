package com.edy.buystuff.adapters;

import static com.edy.buystuff.activities.MainActivity.PRODUCT_ID_EXTRA_STRING;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.amplifyframework.datastore.generated.model.ShoppingItem;
import com.edy.buystuff.R;
import com.edy.buystuff.activities.OrderFormActivity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class ShoppingRecyclerViewAdapter extends RecyclerView.Adapter<ShoppingRecyclerViewAdapter.ShoppingItemViewHolder>
{
    AppCompatActivity associatedActivity;
    List<ShoppingItem> shoppingItemList;
    public final static String TAG =  "edy_buystuff_shoppingrecyclerviewadapter";

    public ShoppingRecyclerViewAdapter(AppCompatActivity associatedActivity, List<ShoppingItem> shoppingItemList)
    {
        this.associatedActivity = associatedActivity;
        this.shoppingItemList = shoppingItemList;
    }

    @NonNull
    @Override
    public ShoppingItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View fragment = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_shopping_item, parent, false);
        // Step 1-7: Instantiate the ViewHolder and give it the fragment
        ShoppingItemViewHolder shoppingItemViewHolder = new ShoppingItemViewHolder(fragment);
        return shoppingItemViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ShoppingItemViewHolder holder, int position)
    {
        ShoppingItem shoppingItem = shoppingItemList.get(position);
        View shoppingItemFragment = holder.itemView;
        TextView shoppingItemFragmentTextView = shoppingItemFragment.findViewById(R.id.shoppingItemFragmentTextView);

        DateFormat localIso8601InputDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        localIso8601InputDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        DateFormat dateOutputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        dateOutputFormat.setTimeZone(TimeZone.getDefault());
        String timeAddedString = "";
        try
        {
            Date timeAddedDate = localIso8601InputDateFormat.parse(shoppingItem.getTimeAdded().format());
            if (timeAddedDate != null)
            {
                timeAddedString = dateOutputFormat.format(timeAddedDate);
            }
        }
        catch (ParseException pe)
        {
            Log.i(TAG, "Error converting shopping item date to string: " + pe.getMessage(), pe);
        }

        String shoppingItemListString = "Name: " + shoppingItem.getItemName()
                + "\nDate Created: " + timeAddedString
                + "\nBusiness Unit: " + shoppingItem.getBusinessUnit().getBusinessUnitName();
        shoppingItemFragmentTextView.setText(shoppingItemListString);

        holder.itemView.setOnClickListener(v -> {
            {
                Intent orderFormIntent = new Intent(associatedActivity, OrderFormActivity.class);
                orderFormIntent.putExtra(PRODUCT_ID_EXTRA_STRING, shoppingItem.getId());
                associatedActivity.startActivity(orderFormIntent);
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return shoppingItemList.size();
    }

    public List<ShoppingItem> getShoppingItemList()
    {
        return shoppingItemList;
    }

    public void setShoppingItemList(List<ShoppingItem> shoppingItemList)
    {
        this.shoppingItemList = shoppingItemList;
    }

    public static class ShoppingItemViewHolder extends RecyclerView.ViewHolder
    {
        public ShoppingItemViewHolder(@NonNull View itemView)
        {
            super(itemView);
        }
    }
}
