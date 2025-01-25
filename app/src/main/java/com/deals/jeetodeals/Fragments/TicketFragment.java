package com.deals.jeetodeals.Fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.deals.jeetodeals.Adapters.AdapterPastTickets;
import com.deals.jeetodeals.Adapters.TicketAdapter;
import com.deals.jeetodeals.Model.Ticket;
import com.deals.jeetodeals.R;
import com.deals.jeetodeals.databinding.FragmentTicketBinding;
import java.util.ArrayList;
import java.util.List;

public class TicketFragment extends Fragment {
    private FragmentTicketBinding binding;
    private boolean isActiveLayout = true;
    private TicketAdapter activeAdapter;
    private AdapterPastTickets pastAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTicketBinding.inflate(inflater, container, false);

        // Initialize adapters
        initializeAdapters();

        // Set initial view
        setupRecyclerView(getActiveTickets());

        // Button click listeners
        binding.activeBtn.setOnClickListener(view -> {
            binding.activeBtn.setBackgroundResource(R.drawable.orange_wallet_bg);
            binding.pastBtn.setBackgroundResource(R.drawable.grey_btn);
            isActiveLayout = true;
            setupRecyclerView(getActiveTickets());
        });

        binding.pastBtn.setOnClickListener(view -> {
            binding.pastBtn.setBackgroundResource(R.drawable.orange_wallet_bg);
            binding.activeBtn.setBackgroundResource(R.drawable.grey_btn);
            isActiveLayout = false;
            setupRecyclerView(getPastTickets());
        });

        return binding.getRoot();
    }

    private void initializeAdapters() {
        activeAdapter = new TicketAdapter(requireActivity(), new ArrayList<>());
        pastAdapter = new AdapterPastTickets(requireActivity(), new ArrayList<>());
    }

    private void setupRecyclerView(List<Ticket> tickets) {
        if (isActiveLayout) {
            activeAdapter = new TicketAdapter(requireActivity(), tickets);
            binding.rcTicket.setAdapter(activeAdapter);
        } else {
            pastAdapter = new AdapterPastTickets(requireActivity(), tickets);
            binding.rcTicket.setAdapter(pastAdapter);
        }
    }

    private List<Ticket> getActiveTickets() {
        List<Ticket> tickets = new ArrayList<>();
        tickets.add(new Ticket("iPhone 16 Pro Max", "JC-614-131", R.drawable.promotion_image));
        tickets.add(new Ticket("Samsung Galaxy Z Fold 5", "SC-723-420", R.drawable.promotion_image));
        return tickets;
    }

    private List<Ticket> getPastTickets() {
        List<Ticket> tickets = new ArrayList<>();
        tickets.add(new Ticket("OnePlus 11 Pro", "OP-503-242", R.drawable.promotion_image));
        tickets.add(new Ticket("Google Pixel 8 Pro", "GP-900-110", R.drawable.promotion_image));
        return tickets;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}