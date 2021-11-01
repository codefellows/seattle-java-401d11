package com.edy.buystuff.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.edy.buystuff.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ShoppingItemFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShoppingItemFragment extends Fragment
{

    public ShoppingItemFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ShoppingItemFragment.
     */

    public static ShoppingItemFragment newInstance()//String param1, String param2)
    {
        ShoppingItemFragment fragment = new ShoppingItemFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_shopping_item, container, false);
    }
}