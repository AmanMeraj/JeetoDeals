package com.deals.jeetodeals.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SortOptionsAdapter extends ArrayAdapter<SortOption> {

    private final List<SortOption> sortOptions;

    public SortOptionsAdapter(Context context, int resource) {
        super(context, resource);

        sortOptions = Arrays.asList(
                new SortOption("Sort by Popularity", "asc", "popularity"),
                new SortOption("Sort by Latest", "asc", "date"),
                new SortOption("Sort by Price: Low to High", "asc", "price"),
                new SortOption("Sort by Price: High to Low", "desc", "price")
        );

        // Add all options to the adapter
        addAll(sortOptions);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(getContext())
                    .inflate(android.R.layout.simple_dropdown_item_1line, parent, false);
        }

        SortOption option = getItem(position);
        TextView textView = view.findViewById(android.R.id.text1);
        if (option != null) {
            textView.setText(option.getDisplayName());
        }

        return view;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(getContext())
                    .inflate(android.R.layout.simple_dropdown_item_1line, parent, false);
        }

        SortOption option = getItem(position);
        TextView textView = view.findViewById(android.R.id.text1);
        if (option != null) {
            textView.setText(option.getDisplayName());
            textView.setPadding(24, 16, 24, 16);
        }

        return view;
    }

    // Get the currently selected sort option parameters for API request
    public Pair<String, String> getSelectedOptionParams(int position) {
        SortOption option = sortOptions.get(position);
        return new Pair<>(option.getOrder(), option.getOrderBy());
    }

    // Simple Pair class for returning two values
    public static class Pair<F, S> {
        private final F first;
        private final S second;

        public Pair(F first, S second) {
            this.first = first;
            this.second = second;
        }

        public F getFirst() {
            return first;
        }

        public S getSecond() {
            return second;
        }
    }
}

