package com.example.appnegocios.ui.logout;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.appnegocios.FormDashboard;
import com.example.appnegocios.FormLogin;
import com.example.appnegocios.FormTelaPrincipal;
import com.example.appnegocios.databinding.FragmentLogoutBinding;
import com.example.appnegocios.ui.dashboard.DashboardFragment;
import com.google.firebase.auth.FirebaseAuth;

public class LogoutFragment extends Fragment {

private FragmentLogoutBinding binding;

    public void onCreateView(Bundle savedInstanceState) {
        FirebaseAuth.getInstance().signOut();

// Obtém o contexto da activity para criar o Intent
        Intent intent = new Intent(getActivity(), FormLogin.class);
        startActivity(intent);

// Finaliza a activity atual
        requireActivity().finish();
    }

@Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}