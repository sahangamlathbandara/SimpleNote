package com.example.bnotes.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemKeyProvider;

import java.util.ArrayList;

/**
 * Created by brijesh on 27/3/18.
 */
public class MyItemKeyProvider extends ItemKeyProvider {
    private final ArrayList<String> note_id;

    public MyItemKeyProvider(int scope, ArrayList<String> note_id) {
        super(scope);
        this.note_id = note_id;
    }

    @Nullable
    @Override
    public Object getKey(int position) {
        return note_id.get(position);
    }

    @Override
    public int getPosition(@NonNull Object key) {
        return note_id.indexOf(key);
    }
}
