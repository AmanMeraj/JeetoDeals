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
import java.util.HashMap;
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
        binding.loader.rlLoader.setVisibility(View.VISIBLE);

        // Initialize adapters with empty lists
        initializeAdapters();

        // Fetch user data (replace with actual method to get user details)
        authToken = getAuthToken();
        customerId = getCustomerId();

        // Observe ticket data
        observeTickets();

        // Button click listeners
        binding.activeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.loader.rlLoader.setVisibility(View.VISIBLE);
                switchToActiveTickets();
            }
        });
        binding.pastBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.loader.rlLoader.setVisibility(View.VISIBLE);
                switchToPastTickets();
            }
        });

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
                    binding.loader.rlLoader.setVisibility(View.GONE);
                    List<TicketGroupedByProduct> activeTickets = new ArrayList<>();
                    List<TicketGroupedByProduct> pastTickets = new ArrayList<>();

                    // Extract active tickets
                    Map<String, ProductTickets> activeTicketsMap = response.data.getActive_tickets();
                    if (activeTicketsMap != null) {
                        for (ProductTickets productTickets : activeTicketsMap.values()) {
                            if (productTickets.getTickets() != null && !productTickets.getTickets().isEmpty()) {
                                activeTickets.add(new TicketGroupedByProduct(
                                        productTickets.getProduct_name(),
                                        productTickets.getProduct_image(),
                                        getTicketNumbersList(productTickets.getTickets()) // Pass list instead of String
                                ));

                            }
                        }
                    }

                    // Extract past tickets
                    Map<String, ProductTickets> pastTicketsMap = response.data.getPast_tickets();
                    if (pastTicketsMap != null) {
                        for (ProductTickets productTickets : pastTicketsMap.values()) {
                            if (productTickets.getTickets() != null && !productTickets.getTickets().isEmpty()) {
                                pastTickets.add(new TicketGroupedByProduct(
                                        productTickets.getProduct_name(),
                                        productTickets.getProduct_image(),
                                        getTicketNumbersList(productTickets.getTickets())
                                ));
                            }
                        }
                    }

                    // Update UI based on selected layout
                    if (isActiveLayout) {
                        binding.loader.rlLoader.setVisibility(View.GONE);
                        updateRecyclerView(activeTickets);
                    } else {
                        binding.loader.rlLoader.setVisibility(View.GONE);
                        updateRecyclerView(pastTickets);
                    }
                } else {
                    binding.loader.rlLoader.setVisibility(View.GONE);
                    showNoDataView();
                }
            });
        } else {
            binding.loader.rlLoader.setVisibility(View.GONE);
            showNoDataView();
        }
    }

    // Helper method to get comma-separated ticket numbers
    private List<Map<String, String>> getTicketNumbersList(List<TicketNumber> tickets) {
        List<Map<String, String>> ticketInfoList = new ArrayList<>();
        for (TicketNumber ticket : tickets) {
            Map<String, String> ticketInfo = new HashMap<>();
            ticketInfo.put("ticket_number", ticket.getTicket_number());
            ticketInfo.put("date_purchased", ticket.getDate_purchased());
            ticketInfoList.add(ticketInfo);
        }
        return ticketInfoList;
    }

    private void switchToActiveTickets() {
        binding.activeBtn.setBackgroundResource(R.drawable.orange_wallet_bg);
        binding.pastBtn.setBackgroundResource(R.drawable.grey_btn);
        isActiveLayout = true;

        // Check if active tickets are empty
        if (activeAdapter.getItemCount() == 0) {
            showNoDataView();
        } else {
            binding.rcTicket.setVisibility(View.VISIBLE);
            binding.noItem.setVisibility(View.GONE);
        }

        observeTickets();
    }

    private void switchToPastTickets() {
        binding.pastBtn.setBackgroundResource(R.drawable.orange_wallet_bg);
        binding.activeBtn.setBackgroundResource(R.drawable.grey_btn);
        isActiveLayout = false;

        // Check if past tickets are empty
        if (pastAdapter.getItemCount() == 0) {
            showNoDataView();
        } else {
            binding.rcTicket.setVisibility(View.VISIBLE);
            binding.noItem.setVisibility(View.GONE);
        }

        observeTickets();
    }


    private void updateRecyclerView(List<TicketGroupedByProduct> tickets) {
        if (tickets != null && !tickets.isEmpty()) {
            binding.rcTicket.setVisibility(View.VISIBLE);
            binding.cardNoData.setVisibility(View.GONE);

            if (isActiveLayout) {
                activeAdapter.updateTickets(tickets);
                binding.rcTicket.setAdapter(activeAdapter);
            } else {
                pastAdapter.updateTickets(tickets);
                binding.rcTicket.setAdapter(pastAdapter);
            }
        } else {
            showNoDataView();
        }
    }


    private void showNoDataView() {
        binding.rcTicket.setVisibility(View.GONE);
        binding.noItem.setVisibility(View.VISIBLE);
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
