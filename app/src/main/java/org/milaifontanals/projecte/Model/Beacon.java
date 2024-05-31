package org.milaifontanals.projecte.Model;

import com.google.gson.annotations.SerializedName;

/**
 * Classe beacon (No utilitzada perque em petava)
 */
public class Beacon {

    @SerializedName("bea_id")
    private int beaId;
    @SerializedName("bea_code")
    private String beaCode;

    public int getBeaId() {
        return beaId;
    }

    public void setBeaId(int beaId) {
        this.beaId = beaId;
    }

    public String getBeaCode() {
        return beaCode;
    }

    public void setBeaCode(String beaCode) {
        this.beaCode = beaCode;
    }



}
