package com.edy.buystuff.adapters;

import static com.edy.buystuff.activities.MainActivity.PRODUCT_ID_EXTRA_STRING;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.edy.buystuff.R;
import com.edy.buystuff.activities.MainActivity;
import com.edy.buystuff.activities.OrderFormActivity;
import com.edy.buystuff.models.ShoppingItem;

import java.util.List;

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
        shoppingItemFragmentTextView.setText(shoppingItem.toString());

        holder.itemView.setOnClickListener(v -> {
            {
                Intent orderFormIntent = new Intent(associatedActivity, OrderFormActivity.class);
                orderFormIntent.putExtra(PRODUCT_ID_EXTRA_STRING, shoppingItem.id);
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
