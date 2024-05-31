package org.milaifontanals.projecte.Model;

import com.google.gson.annotations.SerializedName;
/**
 * Classe Checkpoint amb les anotacions pertintents
 */
public class Checkpoint {
    @SerializedName("chk_id")
    private int chkId;
    @SerializedName("chk_pk")
    private double chkPk;
    @SerializedName("chk_cir_id")
    private int chkCirId;

    public int getChkId() {
        return chkId;
    }

    public void setChkId(int chkId) {
        this.chkId = chkId;
    }

    public double getChkPk() {
        return chkPk;
    }

    public void setChkPk(double chkPk) {
        this.chkPk = chkPk;
    }

    public int getChkCirId() {
        return chkCirId;
    }

    public void setChkCirId(int chkCirId) {
        this.chkCirId = chkCirId;
    }
}
