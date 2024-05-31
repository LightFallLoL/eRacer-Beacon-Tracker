package org.milaifontanals.projecte.Model.Response;

import org.milaifontanals.projecte.Model.Checkpoint;
import org.milaifontanals.projecte.Model.Participant;

import java.util.List;

/**
 * Les responses les he utilitzat per pillar desde la api. Ho vaig veure en una recomenacio.
 */
public class CheckpointResponse {
    private List<Checkpoint> checkpoints;

    public List<Checkpoint> getCheckpoints() {
        return checkpoints;
    }

    public void setCheckpoints(List<Checkpoint> checkpoints) {
        this.checkpoints = checkpoints;
    }
}
