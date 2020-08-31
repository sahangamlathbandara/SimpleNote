package com.example.bnotes.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import androidx.recyclerview.selection.ItemDetailsLookup;
/**
 * Created by brijesh on 27/3/18.
 */
public class MyItemLookup extends ItemDetailsLookup {

    private final RecyclerView recyclerView;

    public MyItemLookup(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    @Nullable
    @Override
    public ItemDetails getItemDetails(@NonNull MotionEvent e) {
        View view = recyclerView.findChildViewUnder(e.getX(), e.getY());
        if (view != null) {
            RecyclerView.ViewHolder viewHolder = recyclerView.getChildViewHolder(view);
            if (viewHolder instanceof CustomAdapter.ItemListViewHolder) {
                return ((CustomAdapter.ItemListViewHolder) viewHolder).getItemDetails();
            }
        }
        return null;
    }
}
