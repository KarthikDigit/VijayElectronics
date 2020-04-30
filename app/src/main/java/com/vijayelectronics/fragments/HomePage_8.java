package com.vijayelectronics.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.vijayelectronics.R;
import com.vijayelectronics.activities.MainActivity;
import com.vijayelectronics.constant.ConstantValues;


public class HomePage_8 extends Fragment {





    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.homepage_8, container, false);

        // Enable Drawer Indicator with static variable actionBarDrawerToggle of MainActivity
        MainActivity.actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(ConstantValues.APP_HEADER);


        // Initialize new Bundle for Fragment arguments
        Bundle bundle = new Bundle();
        bundle.putBoolean("isHeaderVisible", true);
        bundle.putBoolean("isMenuItem", false);

        // Get FragmentManager
        FragmentManager fragmentManager = getFragmentManager();

        // Add Category Fragment to specified FrameLayout
        Fragment categoryFragment = new Categories_1_horizontal_small();
        Bundle categoryBundle = new Bundle();
        categoryBundle.putBoolean("isMenuItem", false);
        categoryBundle.putBoolean("isHeaderVisible", false);
        categoryFragment.setArguments(categoryBundle);
        fragmentManager.beginTransaction().replace(R.id.category_fragment, categoryFragment).commit();

        // Add ProductsNewest Fragment to specified FrameLayout
        Fragment productsNewest = new ProductsNewest();
        productsNewest.setArguments(bundle);
        fragmentManager.beginTransaction().replace(R.id.newest_fragment, productsNewest).commit();
        
        
        // Add ProductsOnSale Fragment to specified FrameLayout
        Fragment productsOnSale = new ProductsOnSale();
        productsOnSale.setArguments(bundle);
        fragmentManager.beginTransaction().replace(R.id.super_deals_fragment, productsOnSale).commit();


        // Add FeaturedProducts Fragment to specified FrameLayout
        Fragment allProducts = new All_Products();
        Bundle bundleInfo = new Bundle();
        bundleInfo.putBoolean("on_sale", false);
        bundleInfo.putBoolean("featured", true);
        bundleInfo.putBoolean("is_bottombar_dissabled", true);
        allProducts.setArguments(bundleInfo);
        fragmentManager.beginTransaction().replace(R.id.featured_fragment, allProducts).commit();


        return rootView;

    }
    

}
