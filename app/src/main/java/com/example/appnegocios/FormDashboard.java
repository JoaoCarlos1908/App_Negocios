package com.example.appnegocios;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.credentials.webauthn.Cbor;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.example.appnegocios.databinding.ActivityFormDashboardBinding;
import com.google.firebase.auth.FirebaseAuth;

public class FormDashboard extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    @SuppressLint("RestrictedApi")
private ActivityFormDashboardBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

     binding = ActivityFormDashboardBinding.inflate(getLayoutInflater());
     setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarFormDashboard.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_dashboard, R.id.nav_perfil, R.id.nav_produtos, R.id.nav_avaliacoes, R.id.nav_reclamacoes,
                R.id.nav_config, R.id.nav_mudar_conta)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_form_dashboard);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_mudar_conta) {
                // Desloga do Firebase
                FirebaseAuth.getInstance().signOut();

                // Redireciona para a tela de login
                Intent intent = new Intent(FormDashboard.this, FormLogin.class);
                startActivity(intent);
                finish(); // Fecha a dashboard para impedir voltar com botão "voltar"
                return true;
            }

            // Para os outros itens, deixa o NavigationUI lidar normalmente
            boolean handled = NavigationUI.onNavDestinationSelected(item, navController);
            if (handled) {
                DrawerLayout drawerLayout = binding.drawerLayout;
                drawerLayout.closeDrawers(); // Fecha o menu lateral
            }
            return handled;
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.form_dashboard, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_form_dashboard);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}