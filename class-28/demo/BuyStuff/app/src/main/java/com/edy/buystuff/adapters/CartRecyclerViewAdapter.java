package com.edy.buystuff.adapters;

import static com.edy.buystuff.activities.MainActivity.PRODUCT_NAME_EXTRA_STRING;

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
import com.edy.buystuff.models.CartItem;

import java.util.List;

// Step 1-4: Create RecyclerViewAdapter and extend RecyclerView.Adapter
// Step 1-5: Create a new Fragment and design that fragment
// Don't forget to convert your Fragment to a different layout (ConstraintLayout)
// Step 2-3b: Change the parameterized type on the next line to use our new view holder
public class CartRecyclerViewAdapter extends RecyclerView.Adapter<CartRecyclerViewAdapter.CartItemViewHolder>
{
    AppCompatActivity associatedActivity;
    // Step 2-2: Pass data into RecyclerViewAdapter (need to create a holder here for it)
    List<CartItem> cartItemList;
    public final static String TAG =  "edy_buystuff_cartrecyclerviewadapter";

    public CartRecyclerViewAdapter(AppCompatActivity associatedActivity, List<CartItem> cartItemList)
    {
        this.associatedActivity = associatedActivity;
        this.cartItemList = cartItemList;
    }

    // Step 1-6: Instantiate the fragment that you made in step 1-6
    @NonNull
    @Override
    public CartItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View fragment = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_cart_item, parent, false);
        // Step 1-7: Instantiate the ViewHolder and give it the fragment
        CartItemViewHolder cartItemViewHolder = new CartItemViewHolder(fragment);
        return cartItemViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CartItemViewHolder holder, int position)
    {
        // Step 2-4: Give the view holder the data
        // Don't forget to have a toString() that shows something for the recycler view
        CartItem cartItem = cartItemList.get(position);
        View cartItemFragment = holder.itemView;
        TextView cartItemFragmentTextView = cartItemFragment.findViewById(R.id.cartItemFragmentTextView);
        cartItemFragmentTextView.setText(cartItem.toString());

        // Step 3: Make an onClickListener in order to go to another page with the cart item's info
        holder.itemView.setOnClickListener(v -> {
            {
                Intent orderFormIntent = new Intent(associatedActivity, OrderFormActivity.class);
                orderFormIntent.putExtra(PRODUCT_NAME_EXTRA_STRING, cartItem.itemName);
                associatedActivity.startActivity(orderFormIntent);
            }
        });
    }

    // Step 2-5: Make this count dynamic
    @Override
    public int getItemCount()
    {
        return cartItemList.size();
    }

    // Step 2-3a: Change everything in this class that uses a RecycleView.ViewHolder to use CartItemViewHolder instead
    public static class CartItemViewHolder extends RecyclerView.ViewHolder
    {
        public CartItemViewHolder(@NonNull View itemView)
        {
            super(itemView);
        }
    }
}
