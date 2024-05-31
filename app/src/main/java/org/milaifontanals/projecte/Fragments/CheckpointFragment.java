package org.milaifontanals.projecte.Fragments;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.Toolbar;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.milaifontanals.projecte.Model.Circuit;
import org.milaifontanals.projecte.Model.Cursa;
import org.milaifontanals.projecte.Model.Inscripcio;
import org.milaifontanals.projecte.Model.Participant;
import org.milaifontanals.projecte.R;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CheckpointFragment extends Fragment implements MonitorNotifier {
    //Fragment semicomplet ja que no m'ha donat temps d'acabar el escaneig de Beacons.
    private Circuit circuit;
    private Cursa cursa;
    private List<Participant> participants;
    private List<Beacon> allBeacons;
    private HashMap<String, Participant> participantsMap = new HashMap<>();
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private Handler handlerAnimation = new Handler();
    private boolean statusAnimation = false;
    private Button button;
    TextView logText;


    private boolean joinDone = false;

    Double idCheckpoint;
    int pk;
    // beacons
    public static final Region wildcardRegion = new Region("wildcardRegion", null, null, null);
    Region allBeaconsRegion;
    BeaconManager beaconManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scanner, container, false);
            // Barra d'estat de color vermell

        if (getArguments() != null) {
            cursa = (Cursa) getArguments().getSerializable("cursa");
            circuit = (Circuit) getArguments().getSerializable("circuit");

        }
        logText = view.findViewById(R.id.tvBeaconLog);
        button = view.findViewById(R.id.btnEscaner);

        // Mecànica de l'escaneig
        button.setOnClickListener(v -> {
            if (statusAnimation) {
                beaconManager.stopRangingBeacons(allBeaconsRegion);
                beaconManager.stopMonitoring(wildcardRegion);
                button.setText("Comença a escanejar");
            } else {

                if (!joinDone) {
                    //joinParticipantsBeacons();
                    beaconManager.startRangingBeacons(allBeaconsRegion);
                    beaconManager.startMonitoring(wildcardRegion);
                    button.setText("Escanejant");

                }
                statusAnimation = !statusAnimation;
            }

            // configurar la API de beacons
            beaconManager = BeaconManager.getInstanceForApplication(requireContext());
            beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
            allBeaconsRegion = new Region("all-beacons-region", null, null, null);
            RangeNotifier rn = (beacons, region) -> {

                if (beacons.size() > 0) {
                    for (Beacon beacon : beacons) {
                        String id = beacon.getId1().toString();
                        double distance = beacon.getDistance();

                        Log.d("BEACON", "Beacon: " + id + " Distancia: " + distance);

                        if (participantsMap.containsKey(id) && !participantsMap.get(id).haPassat) {
                            participantsMap.get(id).haPassat = true;
                            //logText.setText("[" + getInscripcioFromParticipant(participantsMap.get(id)).getDorsal() + "] " + participantsMap.get(id).getNom().toUpperCase() + " " + participantsMap.get(id).getCognoms().toUpperCase() + " ha passat pel checkpoint a les " + sdf.format(new Date()).split(" ")[1] + "\n" + logTextView.getText());
                        }
                    }
                }
            };
            beaconManager.addRangeNotifier(rn);


    });
        return view;
    }


    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            // Aquí puedes agregar alguna acción que se deba repetir periódicamente si es necesario
            handlerAnimation.postDelayed(this, 1500);
        }
    };

    @Override
    public void didEnterRegion(Region region) {
        // Implementación del método
    }

    @Override
    public void didExitRegion(Region region) {
        // Implementación del método
    }

    @Override
    public void didDetermineStateForRegion(int i, Region region) {
        // Implementación del método
    }



}
