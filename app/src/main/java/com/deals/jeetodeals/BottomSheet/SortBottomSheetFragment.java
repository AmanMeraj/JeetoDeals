package com.deals.jeetodeals.BottomSheet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import  com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.deals.jeetodeals.Adapters.SortOption;
import com.deals.jeetodeals.Adapters.SortOptionsAdapter;
import com.deals.jeetodeals.R;
import com.deals.jeetodeals.databinding.FragmentSortBottomSheetBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

public class SortBottomSheetFragment extends BottomSheetDialogFragment {

    private FragmentSortBottomSheetBinding binding;
    private SortOptionListener listener;
    private List<SortOption> sortOptions = new ArrayList<>();
    private int selectedPosition = 0;

    public interface SortOptionListener {
        void onSortOptionSelected(String orderBy, String order, String displayName, int position);
    }

    public static SortBottomSheetFragment newInstance() {
        return new SortBottomSheetFragment();
    }

    public void setSortOptionListener(SortOptionListener listener) {
        this.listener = listener;
    }

    public void setSelectedPosition(int position) {
        this.selectedPosition = position;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, com.google.android.material.R.style.Base_ThemeOverlay_Material3_BottomSheetDialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSortBottomSheetBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Setup sort options
        initializeSortOptions();

        // Setup RecyclerView
        SortOptionsRecyclerAdapter adapter = new SortOptionsRecyclerAdapter(sortOptions, selectedPosition);
        binding.recyclerViewSortOptions.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerViewSortOptions.setAdapter(adapter);

        // Close button
        binding.closeButton.setOnClickListener(v -> dismiss());
    }

    private void initializeSortOptions() {
        sortOptions.add(new SortOption("Sort by popularity", "asc", "popularity"));
        sortOptions.add(new SortOption("Sort by latest", "asc", "date"));
        sortOptions.add(new SortOption("Sort by price: low to high", "asc", "price"));
        sortOptions.add(new SortOption("Sort by price: High to low", "desc", "price"));
    }

    private class SortOptionsRecyclerAdapter extends RecyclerView.Adapter<SortOptionsRecyclerAdapter.SortViewHolder> {

        private final List<SortOption> options;
        private int selectedPos;

        public SortOptionsRecyclerAdapter(List<SortOption> options, int selectedPosition) {
            this.options = options;
            this.selectedPos = selectedPosition;
        }

        @NonNull
        @Override
        public SortViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sort_option, parent, false);
            return new SortViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull SortViewHolder holder, int position) {
            SortOption option = options.get(position);
            holder.bind(option, position == selectedPos);

            holder.itemView.setOnClickListener(v -> {
                int previousSelected = selectedPos;
                selectedPos = holder.getAdapterPosition();

                // Update the UI
                notifyItemChanged(previousSelected);
                notifyItemChanged(selectedPos);

                // Notify listener
                if (listener != null) {
                    SortOption selectedOption = options.get(selectedPos);
                    listener.onSortOptionSelected(
                            selectedOption.getOrderBy(),
                            selectedOption.getOrder(),
                            selectedOption.getDisplayName(),
                            selectedPos
                    );
                }

                // Dismiss the bottom sheet
                dismiss();
            });
        }

        @Override
        public int getItemCount() {
            return options.size();
        }

        class SortViewHolder extends RecyclerView.ViewHolder {
            private final View radioButton;
            private final View itemContainer;
            private final androidx.appcompat.widget.AppCompatTextView optionText;

            public SortViewHolder(@NonNull View itemView) {
                super(itemView);
                radioButton = itemView.findViewById(R.id.radioButton);
                optionText = itemView.findViewById(R.id.optionText);
                itemContainer = itemView.findViewById(R.id.itemContainer);
            }

            public void bind(SortOption option, boolean isSelected) {
                optionText.setText(option.getDisplayName());

                // Update radio button state
                radioButton.setSelected(isSelected);

                // Update background for selected item (optional)
                if (isSelected) {
                    itemContainer.setBackgroundResource(R.drawable.bg_selected_sort_option);
                } else {
                    itemContainer.setBackgroundResource(android.R.color.transparent);
                }
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}