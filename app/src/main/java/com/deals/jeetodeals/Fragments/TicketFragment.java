package com.deals.jeetodeals.Fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.deals.jeetodeals.Adapters.AdapterPastTickets;
import com.deals.jeetodeals.Adapters.TicketAdapter;
import com.deals.jeetodeals.Model.ProductTickets;
import com.deals.jeetodeals.Model.TicketGroupedByProduct;
import com.deals.jeetodeals.Model.TicketNumber;
import com.deals.jeetodeals.R;
import com.deals.jeetodeals.Utils.SharedPref;
import com.deals.jeetodeals.databinding.FragmentTicketBinding;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TicketFragment extends Fragment {
    private FragmentTicketBinding binding;
    SharedPref pref= new SharedPref();
    private boolean isActiveLayout = true;
    private TicketAdapter activeAdapter;
    private AdapterPastTickets pastAdapter;
    private FragmentsViewModel ticketViewModel;
    private String authToken, customerId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTicketBinding.inflate(inflater, container, false);
        ticketViewModel = new ViewModelProvider(this).get(FragmentsViewModel.class);

        // Initialize adapters with empty lists
        initializeAdapters();

        // Fetch user data (replace with actual method to get user details)
        authToken = getAuthToken();
        customerId = getCustomerId();

        // Observe ticket data
        observeTickets();

        // Button click listeners
        binding.activeBtn.setOnClickListener(view -> switchToActiveTickets());
        binding.pastBtn.setOnClickListener(view -> switchToPastTickets());

        return binding.getRoot();
    }

    private void initializeAdapters() {
        activeAdapter = new TicketAdapter(requireActivity(), new ArrayList<>());
        pastAdapter = new AdapterPastTickets(requireActivity(), new ArrayList<>());
        binding.rcTicket.setAdapter(activeAdapter);
    }

    private void observeTickets() {
        if (authToken != null && customerId != null) {
            ticketViewModel.GetCustomerTickets(authToken, customerId).observe(getViewLifecycleOwner(), response -> {
                if (response != null && response.isSuccess && response.data != null) {
                    List<TicketGroupedByProduct> activeTickets = new ArrayList<>();
                    List<TicketGroupedByProduct> pastTickets = new ArrayList<>();

                    // Extract active tickets
                    Map<String, ProductTickets> activeTicketsMap = response.data.getActive_tickets();
                    if (activeTicketsMap != null) {
                        for (ProductTickets productTickets : activeTicketsMap.values()) {
                            if (productTickets.getTickets() != null && !productTickets.getTickets().isEmpty()) {
                                String ticketNumbers = getCommaSeparatedTickets(productTickets.getTickets());
                                activeTickets.add(new TicketGroupedByProduct(
                                        productTickets.getProduct_name(),
                                        productTickets.getProduct_image(),
                                        ticketNumbers
                                ));
                            }
                        }
                    }

                    // Extract past tickets
                    Map<String, ProductTickets> pastTicketsMap = response.data.getPast_tickets();
                    if (pastTicketsMap != null) {
                        for (ProductTickets productTickets : pastTicketsMap.values()) {
                            if (productTickets.getTickets() != null && !productTickets.getTickets().isEmpty()) {
                                String ticketNumbers = getCommaSeparatedTickets(productTickets.getTickets());
                                pastTickets.add(new TicketGroupedByProduct(
                                        productTickets.getProduct_name(),
                                        productTickets.getProduct_image(),
                                        ticketNumbers
                                ));
                            }
                        }
                    }

                    // Update UI based on selected layout
                    if (isActiveLayout) {
                        updateRecyclerView(activeTickets);
                    } else {
                        updateRecyclerView(pastTickets);
                    }
                } else {
                    showNoDataView();
                }
            });
        } else {
            showNoDataView();
        }
    }

    // Helper method to get comma-separated ticket numbers
    private String getCommaSeparatedTickets(List<TicketNumber> tickets) {
        StringBuilder ticketNumbers = new StringBuilder();
        for (int i = 0; i < tickets.size(); i++) {
            ticketNumbers.append(tickets.get(i).getTicket_number());
            if (i < tickets.size() - 1) {
                ticketNumbers.append(", ");
            }
        }
        return ticketNumbers.toString();
    }

    private void switchToActiveTickets() {
        binding.activeBtn.setBackgroundResource(R.drawable.orange_wallet_bg);
        binding.pastBtn.setBackgroundResource(R.drawable.grey_btn);
        isActiveLayout = true;
        observeTickets();
    }

    private void switchToPastTickets() {
        binding.pastBtn.setBackgroundResource(R.drawable.orange_wallet_bg);
        binding.activeBtn.setBackgroundResource(R.drawable.grey_btn);
        isActiveLayout = false;
        observeTickets();
    }

    private void updateRecyclerView(List<TicketGroupedByProduct> tickets) {
        if (tickets != null && !tickets.isEmpty()) {
            binding.rcTicket.setVisibility(View.VISIBLE);
            binding.cardNoData.setVisibility(View.GONE);

            if (isActiveLayout) {
                activeAdapter.updateTickets(tickets);
                binding.rcTicket.setAdapter(activeAdapter);  // Set active adapter
            } else {
                pastAdapter.updateTickets(tickets);
                binding.rcTicket.setAdapter(pastAdapter);  // Set past adapter
            }
        } else {
            showNoDataView();
        }
    }


    private void showNoDataView() {
        binding.rcTicket.setVisibility(View.GONE);
        binding.cardNoData.setVisibility(View.VISIBLE);
    }

    private String getAuthToken() {
        // Replace this with actual method to get auth token
        return "Bearer " + pref.getPrefString(requireActivity(), pref.user_token);
    }

    private String getCustomerId() {
        // Replace this with actual method to get customer ID
        return "your_customer_id";
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
