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
                stopPulse();
                beaconManager.stopRangingBeacons(allBeaconsRegion);
                beaconManager.stopMonitoring(wildcardRegion);
                button.setText("Comença a escanejar");
            } else {
                startPulse();
                if (!joinDone)
                    //joinParticipantsBeacons();
                beaconManager.startRangingBeacons(allBeaconsRegion);
                beaconManager.startMonitoring(wildcardRegion);
                button.setText("Escanejant");

            }
            statusAnimation = !statusAnimation;
        });

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

        // per poder saber els participants i beacons d'aquesta cursa, primer els hem de carregar tots
        //loadParticipants();
        //loadBeacons();

        // configurem els checkpoints
        //view.findViewById(R.id.btnAcceptar).setOnClickListener(v -> {

            // Comprovem que la hora del servidor sigui correcta. Si no ho és, no deixem continuar
          //  checkServer(pk);


        return view;
    }

    private void startPulse() {
        handlerAnimation.postDelayed(runnable, 1500);
    }

    private void stopPulse() {
        handlerAnimation.removeCallbacks(runnable);
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

    /*
    private void loadParticipants() {
        OkHttpClient.Builder client = new OkHttpClient.Builder();
        Gson gson = new GsonBuilder().create();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(WebService.url).addConverterFactory(GsonConverterFactory.create(gson)).client(client.build()).build();

        WebService webService = retrofit.create(WebService.class);
        Call<ParticipantsResponse> call = webService.getParticipants();

        call.enqueue(new Callback<ParticipantsResponse>() {
            @Override
            public void onResponse(Call<ParticipantsResponse> call, Response<ParticipantsResponse> response) {
                participants = response.body().getParticipants();
            }

            @Override
            public void onFailure(Call<ParticipantsResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void loadBeacons() {
        OkHttpClient.Builder client = new OkHttpClient.Builder();
        Gson gson = new GsonBuilder().create();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(WebService.url).addConverterFactory(GsonConverterFactory.create(gson)).client(client.build()).build();

        WebService webService = retrofit.create(WebService.class);
        Call<BeaconsResponse> call = webService.getBeacons();

        call.enqueue(new Callback<BeaconsResponse>() {
            @Override
            public void onResponse(Call<BeaconsResponse> call, Response<BeaconsResponse> response) {
                allBeacons = response.body().getBeacons();
            }

            @Override
            public void onFailure(Call<BeaconsResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void checkServer(double pk) {
        WebService webService = new Retrofit.Builder().baseUrl(WebService.url).build().create(WebService.class);
        Call<String> call = webService.getTime();
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Date serverTime;
                try {
                    serverTime = sdf.parse(response.body());
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }

                // Si hay más de 1 minuto de diferencia, no dejamos continuar
                if (Math.abs(serverTime.getTime() - new Date().getTime()) > 60000) {
                    // mostrar diálogo de error
                    new AlertDialog.Builder(requireContext())
                            .setTitle("Error")
                            .setMessage("L'hora del dispositiu no està sincronitzada amb el servidor. Siusplau, sincronitza-la i torna a intentar-ho.\n\nHora del servidor: " + sdf.format(serverTime) + "\nHora del dispositiu: " + sdf.format(new Date()))
                            .setPositiveButton("Aceptar", null)
                            .show();
                } else {
                    logTextView.setText("Checkpoint del km " + String.format("%.2f", pk) + " configurat a les " + sdf.format(serverTime));
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private Participant getParticipantFromInscripcion(Inscripcio inscripcio) {
        for (Participant participant : participants) {
            if (participant.getId() == inscripcio.getParticipantId()) {
                return participant;
            }
        }
        return null;
    }

    private Inscripcio getInscripcioFromParticipant(Participant participant) {
        for (Inscripcio inscripcio : circuit.getInscripcions()) {
            if (inscripcio.getParticipantId() == participant.getId()) {
                return inscripcio;
            }
        }
        return null;
    }

    private BeaconModel getBeaconFromId(int id) {
        for (BeaconModel beacon : allBeacons) {
            if (beacon.getId() == id) {
                return beacon;
            }
        }
        return null;
    }

    private void joinParticipantsBeacons() {
        for (Inscripcio i : circuit.getInscripcions()) {
            Participant participant = getParticipantFromInscripcion(i);
            BeaconModel beacon = getBeaconFromId(i.getBeaId());
            if (participant != null && beacon != null) {
                Log.d("JOIN", "Participant: " + participant.getNom() + " " + participant.getCognoms() + " Beacon: " + beacon.getCode());
                participantsMap.put(beacon.getCode(), participant);
            }
        }
        joinDone = true;
    }*/

}
