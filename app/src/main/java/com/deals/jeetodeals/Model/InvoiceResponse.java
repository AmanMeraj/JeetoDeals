package com.deals.jeetodeals.Model;

import java.io.Serializable;

public class InvoiceResponse implements Serializable {
    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getPdf_url() {
        return pdf_url;
    }

    public void setPdf_url(String pdf_url) {
        this.pdf_url = pdf_url;
    }

    public boolean status;
    public String pdf_url;
}
