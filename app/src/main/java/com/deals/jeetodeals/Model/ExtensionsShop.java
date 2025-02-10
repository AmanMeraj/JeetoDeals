package com.deals.jeetodeals.Model;

import java.io.Serializable;
import java.util.ArrayList;

public class ExtensionsShop implements Serializable {


    public ArrayList<CustomLotteryData> getCustom_lottery_data() {
        return custom_lottery_data;
    }

    public void setCustom_lottery_data(ArrayList<CustomLotteryData> custom_lottery_data) {
        this.custom_lottery_data = custom_lottery_data;
    }

    public ArrayList<CustomLotteryData> custom_lottery_data;

}
