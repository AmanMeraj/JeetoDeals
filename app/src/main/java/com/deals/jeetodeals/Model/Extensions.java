package com.deals.jeetodeals.Model;

import java.io.Serializable;

public class Extensions implements Serializable {
    public CustomLotteryData getCustom_lottery_data() {
        return custom_lottery_data;
    }

    public void setCustom_lottery_data(CustomLotteryData custom_lottery_data) {
        this.custom_lottery_data = custom_lottery_data;
    }

    public CustomLotteryData custom_lottery_data;

}
