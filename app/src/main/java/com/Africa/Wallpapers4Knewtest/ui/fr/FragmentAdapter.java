package com.Africa.Wallpapers4Knewtest.ui.fr;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.Africa.Wallpapers4Knewtest.MyUtils;


public class FragmentAdapter extends FragmentStateAdapter {


    public FragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (MyUtils.appControl.isShowCategories()){
            switch (position){
                case 0:
                    return new DiscoverFragment();
                case 1:
                    return new CategoriesFragment();
                case 2:
                    return new FavoriteFragment();
                default:
                    return new DiscoverFragment();
            }
        }else {
            switch (position){
                case 0:
                    return new DiscoverFragment();
                case 1:
                    return new FavoriteFragment();
                default:
                    return new DiscoverFragment();
            }
        }
       
    }

    @Override
    public int getItemCount() {
        if (MyUtils.appControl.isShowCategories()){
            return 3; // Discover, Categories, and Favorites
        }else {
            return 2; // Discover and Favorites
        }
    }
}
