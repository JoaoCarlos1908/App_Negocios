package com.example.appnegocios.ui.perfil;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appnegocios.R;
import com.example.appnegocios.databinding.FragmentMapsBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

public class MapsFragment extends Fragment implements OnMapReadyCallback {

    private FragmentMapsBinding binding;
    private GoogleMap mMap;
    private FirebaseFirestore db;
    private String usuarioID;
    private FusedLocationProviderClient fusedLocationClient;
    private Marker marcadorSelecionado;
    private double latitude = 0.0;
    private double longitude = 0.0;
    private boolean edicaoHabilitada = false;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private Button bt_editar, bt_salvar, bt_cancelar;
    private TextView text_alterar_loc;
    // Dados de latitude e longitude do Firebase
    private double latitudeFirebase = 0.0;
    private double longitudeFirebase = 0.0;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMapsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        IniciarComponentes(view);

        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        ativarEdicaoDeLocalizacao(); // Permissão concedida
                    } else {
                        Toast.makeText(requireContext(), "Permissão de localização negada", Toast.LENGTH_SHORT).show();
                    }
                });

        // Inicializa o fragmento do mapa
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        bt_editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                edicaoHabilitada = true;
                bt_editar.setVisibility(View.GONE);
                bt_salvar.setVisibility(View.VISIBLE);
                bt_cancelar.setVisibility(View.VISIBLE);
                text_alterar_loc.setVisibility(View.VISIBLE);

                ativarEdicaoDeLocalizacao();
            }
        });

        bt_salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("Cliente").document(usuarioID)
                        .update(
                                "latitude", latitude,
                                "longitude", longitude
                        )
                        .addOnSuccessListener(aVoid -> {
                            Snackbar snackbar = Snackbar.make(v, "Localização atualizada!", Snackbar.LENGTH_SHORT);
                            snackbar.setBackgroundTint(Color.GREEN);
                            snackbar.setTextColor(Color.BLACK);
                            snackbar.show();

                            atualizarTela(); // só chama aqui depois do sucesso
                        })
                        .addOnFailureListener(e -> {
                            Snackbar snackbar = Snackbar.make(v, "Erro: " + e.getMessage(), Snackbar.LENGTH_SHORT);
                            snackbar.setBackgroundTint(Color.RED);
                            snackbar.setTextColor(Color.WHITE);
                            snackbar.show();
                        });
            }
        });

        bt_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                atualizarTela();
            }
        });

        return view;
    }

    private void IniciarComponentes(View view) {
        bt_editar = view.findViewById(R.id.bt_editar);
        bt_salvar = view.findViewById(R.id.bt_salvar);
        bt_cancelar = view.findViewById(R.id.bt_cancelar);
        text_alterar_loc = view.findViewById(R.id.text_selecionar_maps);
        usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (verificarPermissaoLocalizacao()) {
            try {
                mMap.setMyLocationEnabled(true); // ATIVA A BOLINHA AZUL
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
        if (edicaoHabilitada) {
            ativarEdicaoDeLocalizacao();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        carregarDadosDoFirebase();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void ativarEdicaoDeLocalizacao() {
        if (!verificarPermissaoLocalizacao()) {
            return;
        }

        try {
            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    LatLng minhaLocalizacao = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(minhaLocalizacao, 16));
                }
            });

            mMap.setOnMapClickListener(latLng -> {
                if (marcadorSelecionado != null) {
                    marcadorSelecionado.remove();
                }

                marcadorSelecionado = mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title("Localização escolhida")
                        .draggable(true));

                latitude = latLng.latitude;
                longitude = latLng.longitude;
            });
        } catch (SecurityException e) {
            Toast.makeText(requireContext(), "Permissão de localização não concedida", Toast.LENGTH_SHORT).show();
        }
    }


    private static final int REQUEST_LOCATION_PERMISSION = 1001;

    private boolean verificarPermissaoLocalizacao() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);

            return false;
        }
        return true;
    }

    private void atualizarTela() {
        NavController navController = NavHostFragment.findNavController(this);
        navController.popBackStack(); // Remove o fragment atual da pilha
        navController.navigate(R.id.mapsFragment); // Reinsere o mesmo fragment
    }

    private void carregarDadosDoFirebase() {
        // Supondo que você tenha uma coleção "locations" com um documento "userLocation"
        CollectionReference locationsRef = db.collection("Cliente");

        locationsRef.document(usuarioID).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Double lat = document.getDouble("latitude");
                            Double lon = document.getDouble("longitude");

                            if (lat != null && lon != null) {
                                latitudeFirebase = lat;
                                longitudeFirebase = lon;

                                if (latitudeFirebase != 0.0 && longitudeFirebase != 0.0) {
                                    addMarkerToMap(latitudeFirebase, longitudeFirebase);
                                }
                            } else {
                                Toast.makeText(requireContext(), "Latitude ou longitude não definidas", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(requireContext(), "Documento de localização não encontrado", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(requireContext(), "Erro ao carregar dados do Firebase", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Método para adicionar marcador no mapa
    private void addMarkerToMap(double latitude, double longitude) {
        LatLng localizacao = new LatLng(latitude, longitude);

        // Adiciona o marcador ao mapa
        if (mMap != null) {
            mMap.addMarker(new MarkerOptions()
                    .position(localizacao)
                    .title("Localização Atual")
                    .draggable(true));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(localizacao, 16));
        }
    }

}
