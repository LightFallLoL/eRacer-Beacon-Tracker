package org.milaifontanals.projecte.Model.Response;

import org.milaifontanals.projecte.Model.Registre;

import java.util.List;

public class RegistreResponse {

    private List<Registre> registres;

    public List<Registre> getRegistres() {
        return registres;
    }

    public void setRegistres(List<Registre> inscripcions) {
        this.registres = inscripcions;
    }
}

