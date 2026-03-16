package com.finpay.payments.model;

import java.util.Map;

public class TransactionMetadata {

    private String version;
    private Map<String, String> optionalFields;
    private String corporateId;
    private String billingZip;

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    public Map<String, String> getOptionalFields() { return optionalFields; }
    public void setOptionalFields(Map<String, String> optionalFields) { this.optionalFields = optionalFields; }

    public String getCorporateId() { return corporateId; }
    public void setCorporateId(String corporateId) { this.corporateId = corporateId; }

    public String getBillingZip() { return billingZip; }
    public void setBillingZip(String billingZip) { this.billingZip = billingZip; }
}
