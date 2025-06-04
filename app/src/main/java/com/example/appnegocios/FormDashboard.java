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
import androidx.navigation.fragment.NavHostFragment;
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

        boolean isEmpreendimento = getIntent().getBooleanExtra("tipoConta", false);

        // Obtém o NavHostFragment
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_content_form_dashboard);
        NavController navController = navHostFragment.getNavController();

        // Define o grafo de navegação dinamicamente
        if (isEmpreendimento) {
            navController.setGraph(R.navigation.nav_empreendimento);
        } else {
            navController.setGraph(R.navigation.nav_cliente);
        }

        // Define o menu lateral correto
        if (isEmpreendimento) {
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.menu_empresarial);
            mAppBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.nav_dashboard, R.id.nav_perfil, R.id.nav_produtos, R.id.nav_avaliacoes, R.id.nav_reclamacoes,
                    R.id.nav_config, R.id.nav_mudar_conta)
                    .setOpenableLayout(drawer)
                    .build();
        } else {
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.menu_cliente);
            mAppBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.nav_inicial, R.id.nav_perfilCliente, R.id.nav_categorias, R.id.nav_avaliacoesCliente, R.id.nav_reclamacoesCliente,
                    R.id.nav_configCliente, R.id.nav_mudar_conta)
                    .setOpenableLayout(drawer)
                    .build();
        }

        // Conecta Navigation UI
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // Menu lateral personalizado
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_mudar_conta) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(FormDashboard.this, FormLogin.class));
                finish();
                return true;
            }

            boolean handled = NavigationUI.onNavDestinationSelected(item, navController);
            if (handled) {
                drawer.closeDrawers();
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