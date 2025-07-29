package com.Africa.Wallpapers4Knewtest.ui.fr;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.Africa.Wallpapers4Knewtest.adapters.FavAdapter;
import com.Africa.Wallpapers4Knewtest.config.RoomDatabase.MyDataBse;
import com.Africa.Wallpapers4Knewtest.config.RoomDatabase.MyFavs;
import com.Africa.Wallpapers4Knewtest.databinding.FragmentFavoritesBinding;

import java.util.ArrayList;
import java.util.List;

public class FavoriteFragment extends Fragment {
    FragmentFavoritesBinding binding;
    List<MyFavs> myFavs = new ArrayList<>();
    FavAdapter favAdapter;
    
    // Static reference to the current instance
    private static FavoriteFragment instance;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentFavoritesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        instance = this;
        loadFavorites();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void loadFavorites() {
        if (getContext() != null) {
            MyDataBse myDataBse = MyDataBse.getInstance(getContext());
            myFavs = myDataBse.favDao().getAllFavs();
            
            if (favAdapter == null) {
                favAdapter = new FavAdapter(getActivity(), myFavs);
                binding.FavRecycler.setLayoutManager(new GridLayoutManager(getContext(), 3));
                binding.FavRecycler.setAdapter(favAdapter);
            } else {
                favAdapter.featuredModels.clear();
                favAdapter.featuredModels.addAll(myFavs);
                favAdapter.notifyDataSetChanged();
            }
            
            binding.ff.setText(myFavs.size() + " wallpapers");
            
            if (myFavs.isEmpty()) {
                binding.FavRecycler.setVisibility(View.GONE);
                binding.NoFav.setVisibility(View.VISIBLE);
            } else {
                binding.FavRecycler.setVisibility(View.VISIBLE);
                binding.NoFav.setVisibility(View.GONE);
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onResume() {
        super.onResume();
        loadFavorites();
    }
    
    @Override
    public void onStart() {
        super.onStart();
        // Refresh favorites when fragment becomes visible
        loadFavorites();
    }

    // Public method to refresh favorites from other activities
    public void refreshFavorites() {
        loadFavorites();
    }
    
    // Static method to refresh favorites from anywhere
    public static void refreshFavoritesStatic() {
        if (instance != null) {
            instance.loadFavorites();
        }
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        instance = null;
    }
}