package com.deals.jeetodeals.Fragments;

import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.deals.jeetodeals.Adapters.AdapterPromotion1;
import com.deals.jeetodeals.Adapters.AdapterPromotion2;
import com.deals.jeetodeals.Adapters.ImagePagerAdapter;
import com.deals.jeetodeals.Model.MyItem;
import com.deals.jeetodeals.Model.Promotion;
import com.deals.jeetodeals.R;
import com.deals.jeetodeals.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeFragment extends Fragment {
    FragmentHomeBinding binding;
    private boolean isPlaying = false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding=FragmentHomeBinding.inflate(getLayoutInflater());

        Uri videoUri = Uri.parse("android.resource://" + getActivity().getPackageName() + "/" + R.raw.sample_video); // Replace sample_video with your video name
        binding.videoView.setVideoURI(videoUri);

        // Handle play/pause button click
        binding.playPauseButton.setOnClickListener(v -> {
            if (isPlaying) {
                binding.videoView.pause();
                binding.playPauseButton.setImageResource(R.drawable.play_jd); // Set play icon
            } else {
                binding.videoView.start();
                binding.playPauseButton.setImageResource(R.drawable.play_jd); // Set pause icon
            }
            isPlaying = !isPlaying;
        });

        binding.videoView.setOnPreparedListener(mp -> {
            int videoWidth = mp.getVideoWidth();
            int videoHeight = mp.getVideoHeight();
            float videoProportion = (float) videoWidth / (float) videoHeight;

            int screenWidth = getResources().getDisplayMetrics().widthPixels;
            int newHeight = (int) (screenWidth / videoProportion);

            ViewGroup.LayoutParams layoutParams = binding.videoView.getLayoutParams();
            layoutParams.height = newHeight;
            binding.videoView.setLayoutParams(layoutParams);
        });


        // Handle video completion
        binding.videoView.setOnCompletionListener(mp -> {
            isPlaying = false;
            binding.playPauseButton.setImageResource(R.drawable.play_jd); // Reset to play icon
        });


        List<Integer> imageList = Arrays.asList(
                R.drawable.bannner1,
                R.drawable.bannner1
               );

        ImagePagerAdapter adapter = new ImagePagerAdapter(requireContext(), imageList);
        binding.imageViewpager.setAdapter(adapter);

        List<MyItem> itemList = new ArrayList<>();

// Add items to the list
        itemList.add(new MyItem("54 Sold out of 1000", 50, R.drawable.promotion_image, "Get a Chance to", "WIN", "Add to Cart"));
        itemList.add(new MyItem("200 Sold out of 1000", 20, R.drawable.promotion_image, "Get a Chance to", "WIN", "Add to Cart"));

        AdapterPromotion1 adapter2 = new AdapterPromotion1(requireActivity(), itemList);
        binding.promotionsRecycler.setAdapter(adapter2);


        List<Promotion> promotions = new ArrayList<>();
        promotions.add(new Promotion(
                "Closing at 02:30:45",
                R.drawable.promotion_image,
                "1250",
                "WIN",
                "I Phone 16 Pro Max",
                "Draw date 31 December 2024",
                "Get a chance to",
                "WIN I Phone 16 Pro Max",
                "60 Vouchers"
        ));

        promotions.add(new Promotion(
                "Closing at 05:30:45",
                R.drawable.promotion_image,
                "1050",
                "WIN",
                "Play Station 5",
                "Draw date 22 December 2024",
                "Get a chance to",
                "WIN Play Station 5",
                "160 Vouchers"
        ));
        AdapterPromotion2 adapter3 = new AdapterPromotion2(requireActivity(), promotions);
        binding.rcPromotion2.setAdapter(adapter3);


        return binding.getRoot();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null; // Prevent memory leaks
    }


}