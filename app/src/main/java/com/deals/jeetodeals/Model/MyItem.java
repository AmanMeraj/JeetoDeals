package com.deals.jeetodeals.Model;

public class MyItem {
    private final String totalCardsText;
    private final int progress;
    private final int imageResId;
    private final String chanceText;
    private final String winText;
    private final String buttonText;

    // Constructor
    public MyItem(String totalCardsText, int progress, int imageResId, String chanceText, String winText, String buttonText) {
        this.totalCardsText = totalCardsText;
        this.progress = progress;
        this.imageResId = imageResId;
        this.chanceText = chanceText;
        this.winText = winText;
        this.buttonText = buttonText;
    }

    // Getters
    public String getTotalCardsText() {
        return totalCardsText;
    }

    public int getProgress() {
        return progress;
    }

    public int getImageResId() {
        return imageResId;
    }

    public String getChanceText() {
        return chanceText;
    }

    public String getWinText() {
        return winText;
    }

    public String getButtonText() {
        return buttonText;
    }
}
