package org.milaifontanals.projecte.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.milaifontanals.projecte.API.ApiService;
import org.milaifontanals.projecte.API.RetrofitClient;
import org.milaifontanals.projecte.Adapters.CircuitAdapter;
import org.milaifontanals.projecte.Model.Checkpoint;
import org.milaifontanals.projecte.Model.Circuit;
import org.milaifontanals.projecte.Model.Cursa;
import org.milaifontanals.projecte.Model.Response.CheckpointResponse;
import org.milaifontanals.projecte.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CursaInFragment extends Fragment  implements CircuitAdapter.OnCircuitClickListener{
    private TextView txvTitol;
    private TextView txvDate;
    private TextView txvLocation;
    private EditText edtPK;
    private RecyclerView rcyCircuits;

    private Button btnInscripcio;
    private List<Checkpoint> allCheckpoints = new ArrayList<>();
    private CircuitAdapter circuitAdapter;
    private Spinner spnCheckpoint;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");;

    private Circuit selectedCircuit;

    private Cursa cursa;
    int indexSpinner;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            cursa = (Cursa) getArguments().getSerializable("cursa");
            selectedCircuit = (Circuit) getArguments().getSerializable("selectedCircuit");
        }
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_in_cursa, container, false);

        txvTitol = view.findViewById(R.id.txvTitol);
        txvDate = view.findViewById(R.id.txvDate);
        txvLocation = view.findViewById(R.id.txvLocation);
        rcyCircuits = view.findViewById(R.id.rcyCircuits);
        btnInscripcio = view.findViewById(R.id.btnRegistrar);
        spnCheckpoint = view.findViewById(R.id.spnCheckpoint);
        edtPK = view.findViewById(R.id.edtPK);
        txvTitol.setText(cursa.getNom());
        txvDate.setText(dateFormat.format(cursa.getDataInici())); // Formatear fecha si es necesario
        txvLocation.setText(cursa.getLloc());
        // Configurar RecyclerView de circuits
        List<Circuit> circuits = cursa.getCircuits();
        if (circuits != null && !circuits.isEmpty()) {
            circuitAdapter = new CircuitAdapter(circuits,this);
            rcyCircuits.setLayoutManager(new LinearLayoutManager(getContext()));
            rcyCircuits.setAdapter(circuitAdapter);
        }


        if ("En Curs".equals(cursa.getEstatCursa().getNom())) {
            btnInscripcio.setText("Escanejar");
            btnInscripcio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = spnCheckpoint.getSelectedItemPosition();
                    double pk = (double) spnCheckpoint.getSelectedItem();
                    Checkpoint selectedCheckpoint = filterCheckpoints(selectedCircuit.getId()).get(pos);
                    int idCheckpoint = selectedCheckpoint.getChkId();

                    Bundle bundle = new Bundle();
                    bundle.putSerializable("cursa", cursa);
                    bundle.putSerializable("circuit", selectedCircuit);


                    NavController navController = Navigation.findNavController(view);
                    navController.navigate(R.id.action_cursaDetailFragment_to_scannerFragment, bundle);
                }
            });
        }
        loadCheckpoints();
        spnCheckpoint.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                double checkpoint = (double) parent.getItemAtPosition(position);
                updatePuntoKilometrico(checkpoint);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No hacer nada si no hay selección
            }
        });
        return view;
    }

    private void loadCheckpoints() {


        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<CheckpointResponse> call = apiService.getAllCheckpoints();
        call.enqueue(new Callback<CheckpointResponse>() {
            @Override
            public void onResponse(Call<CheckpointResponse> call, Response<CheckpointResponse> response) {
                if (response.isSuccessful()) {
                    allCheckpoints = response.body().getCheckpoints();
                } else {
                    Toast.makeText(getContext(), "Error loading checkpoints", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CheckpointResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Failed to load checkpoints: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onCircuitClick(Circuit circuit) {
        selectedCircuit = circuit;
        spnCheckpoint.setVisibility(View.VISIBLE);
        edtPK.setVisibility(View.VISIBLE);

        List<Checkpoint> filteredCheckpoints = filterCheckpoints(circuit.getId());
        fillCheckpointsSpinner(filteredCheckpoints);
    }

    private List<Checkpoint> filterCheckpoints(int circuitId) {
        List<Checkpoint> filteredCheckpoints = new ArrayList<>();
        for (Checkpoint checkpoint : allCheckpoints) {
            if (checkpoint.getChkCirId() == circuitId) {
                filteredCheckpoints.add(checkpoint);
            }
        }
        return filteredCheckpoints;
    }

    private void fillCheckpointsSpinner(List<Checkpoint> checkpoints) {
        List<Double> checkpointNumbers = new ArrayList<>();
        for (Checkpoint checkpoint : checkpoints) {
            checkpointNumbers.add(checkpoint.getChkPk());
        }

        ArrayAdapter<Double> adapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item, checkpointNumbers);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCheckpoint.setAdapter(adapter);
    }

    private void updatePuntoKilometrico(double checkpoint) {
        String puntoKilometricoText = "" + checkpoint +"Km";
        edtPK.setText(puntoKilometricoText);
    }
}
