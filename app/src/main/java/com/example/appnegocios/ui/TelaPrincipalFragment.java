package com.example.appnegocios.ui;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.appnegocios.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Class.Empreendimento;
import Class.CepSession;
import Class.Endereco;
import Class.CepUtils;

public class TelaPrincipalFragment extends Fragment {

    private View view;
    private EditText editTextPesquisa, editCep;
    private ImageView btnBuscar;
    private Map<String, List<Empreendimento>> mapaEmpreendimentos = new HashMap<>();
    private  TextView btSaude, btComercios, btRestaurantes, btBeleza, btEsportes, btModa, btPets, btAutomotivo,
                      btManutencao, btOutros, textLocalizacao;
    private String usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private final boolean[] isLigado = {false};
    private Endereco cepPesquisa;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tela_principal, container, false);
        iniciarComponentes();

        carregarEmpreendimentos();
        carregarEmpreendimentos(new FirebaseCallback() {
            @Override
            public void onCallback(List<Empreendimento> lista) {
                exibirEmpreendimentosAleatorios(lista, 10, view); // aqui você limita e exibe como quiser
            }
        });

        editTextPesquisa.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtrarEmpreendimentos(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        btnBuscar.setOnClickListener(v -> {
            String texto = editTextPesquisa.getText().toString().trim();
            filtrarEmpreendimentos(texto);
        });

        //Categorias principais
        btSaude.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("categoria", "Saúde");

                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_form_dashboard);
                navController.popBackStack(R.id.nav_inicial, true);
                navController.navigate(R.id.nav_categorias, bundle);
            }
        });
        btComercios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("categoria", "Comércio Local");

                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_form_dashboard);
                navController.popBackStack(R.id.nav_inicial, true);
                navController.navigate(R.id.nav_categorias, bundle);
            }
        });
        btRestaurantes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("categoria", "Alimentação");

                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_form_dashboard);
                navController.popBackStack(R.id.nav_inicial, true);
                navController.navigate(R.id.nav_categorias, bundle);
            }
        });
        btBeleza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("categoria", "Beleza e Estética");

                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_form_dashboard);
                navController.popBackStack(R.id.nav_inicial, true);
                navController.navigate(R.id.nav_categorias, bundle);
            }
        });
        btEsportes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("categoria", "Esportes e Lazer");

                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_form_dashboard);
                navController.popBackStack(R.id.nav_inicial, true);
                navController.navigate(R.id.nav_categorias, bundle);
            }
        });
        btModa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("categoria", "Moda e Acessórios");

                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_form_dashboard);
                navController.popBackStack(R.id.nav_inicial, true);
                navController.navigate(R.id.nav_categorias, bundle);
            }
        });
        btPets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("categoria", "Pets");

                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_form_dashboard);
                navController.popBackStack(R.id.nav_inicial, true);
                navController.navigate(R.id.nav_categorias, bundle);
            }
        });
        btAutomotivo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("categoria", "Automotivo");

                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_form_dashboard);
                navController.popBackStack(R.id.nav_inicial, true);
                navController.navigate(R.id.nav_categorias, bundle);
            }
        });
        btManutencao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("categoria", "Manutenção e Reparos");

                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_form_dashboard);
                navController.popBackStack(R.id.nav_inicial, true);
                navController.navigate(R.id.nav_categorias, bundle);
            }
        });
        btOutros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_form_dashboard);
                navController.popBackStack(R.id.nav_inicial, true);
                navController.navigate(R.id.nav_categorias);
            }
        });

        textLocalizacao.setOnClickListener(v -> {
            isLigado[0] = !isLigado[0]; // Alterna estado

            if (isLigado[0]) {
                editCep.setVisibility(View.VISIBLE);
            } else {
                editCep.setVisibility(View.GONE);
            }
        });

        editCep.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {

                // 1. Esconde o teclado
                InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(editCep.getWindowToken(), 0);
                }

                // 2. Continua com sua lógica atual
                String cepDigitado = editCep.getText().toString().trim();
                CepSession.getInstance().setCep(cepDigitado);

                carregarCidadeUf(cepDigitado, () -> {
                    if (cepPesquisa != null && cepPesquisa.getLocalidade() != null && cepPesquisa.getUf() != null) {
                        textLocalizacao.setText("📍 " + cepPesquisa.getLocalidade() + " - " + cepPesquisa.getUf());
                    }

                    carregarEmpreendimentos(new FirebaseCallback() {
                        @Override
                        public void onCallback(List<Empreendimento> lista) {
                            exibirEmpreendimentosAleatorios(lista, 10, view);
                        }
                    });

                    editCep.setVisibility(View.GONE);
                });

                return true;
            }
            return false;
        });

        return view;
    }

    private void iniciarComponentes(){
        editTextPesquisa = view.findViewById(R.id.editTextPesquisa);
        editCep = view.findViewById(R.id.editTextCepLocalizacao);
        btnBuscar = view.findViewById(R.id.btnBuscar);

        btSaude = view.findViewById(R.id.btSaude);
        btComercios = view.findViewById(R.id.btComercios);
        btRestaurantes = view.findViewById(R.id.btRestaurantes);
        btBeleza = view.findViewById(R.id.btBeleza);
        btEsportes = view.findViewById(R.id.btEsportes);
        btModa = view.findViewById(R.id.btModa);
        btPets = view.findViewById(R.id.btPets);
        btAutomotivo = view.findViewById(R.id.btAutomotivo);
        btManutencao = view.findViewById(R.id.btManutencao);
        btOutros = view.findViewById(R.id.btOutros);

        textLocalizacao = view.findViewById(R.id.textLocalizacao);
        buscarCidadeEstadoDoCliente(usuarioID, textLocalizacao);
    }

    private void filtrarEmpreendimentos(String texto) {
        String termo = texto.toLowerCase().trim();
        List<Empreendimento> filtrados = new ArrayList<>();
        Set<String> idsAdicionados = new HashSet<>(); // <- evita duplicatas

        for (List<Empreendimento> lista : mapaEmpreendimentos.values()) {
            for (Empreendimento emp : lista) {
                if (idsAdicionados.contains(emp.getIdUser())) continue;

                String nome = emp.getNome() != null ? emp.getNome().toLowerCase() : "";
                String categoria = emp.getCategoria() != null ? emp.getCategoria().toLowerCase() : "";
                List<String> subcategorias = emp.getSubcategorias() != null ? emp.getSubcategorias() : new ArrayList<>();

                boolean nomeMatch = nome.contains(termo);
                boolean categoriaMatch = categoria.contains(termo);
                boolean subMatch = false;
                for (String sub : subcategorias) {
                    if (sub.toLowerCase().contains(termo)) {
                        subMatch = true;
                        break;
                    }
                }

                if (nomeMatch || categoriaMatch || subMatch) {
                    filtrados.add(emp);
                    idsAdicionados.add(emp.getIdUser()); // <- marca como já adicionado
                }
            }
        }
        // Atualiza o RecyclerView com os itens filtrados
        exibirEmpreendimentosAleatorios(filtrados, 10, view);
    }

    public interface FirebaseCallback {
        void onCallback(List<Empreendimento> listaEmpreendimentos);
    }

    private void carregarEmpreendimentos() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Cliente")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    mapaEmpreendimentos.clear();

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Empreendimento emp = new Empreendimento();

                        Boolean tipoConta = doc.getBoolean("TipoConta");
                        if (tipoConta != null && tipoConta) {
                            emp.setIdUser(doc.getId());
                            emp.setNome(doc.getString("nome")); // herdado de Usuario
                            emp.setCategoria(doc.getString("categoria"));

                            // Carregando subcategorias do Firestore para a classe Empreendimento
                            List<String> subcategorias = (List<String>) doc.get("subcategorias");
                            if (subcategorias != null) {
                                emp.setSubcategorias(subcategorias);
                            }

                            // Mapeando por categoria principal
                            String categoria = emp.getCategoria();
                            if (!mapaEmpreendimentos.containsKey(categoria)) {
                                mapaEmpreendimentos.put(categoria, new ArrayList<>());
                            }
                            mapaEmpreendimentos.get(categoria).add(emp);

                            // Mapeando por subcategorias também
                            if (subcategorias != null) {
                                for (String sub : subcategorias) {
                                    if (!mapaEmpreendimentos.containsKey(sub)) {
                                        mapaEmpreendimentos.put(sub, new ArrayList<>());
                                    }
                                    mapaEmpreendimentos.get(sub).add(emp);
                                }
                            }
                        } else {
                            Log.d("Empreendimentos", "Erro ao carregar empreendimentos ou conta não cadastrada como cliente");
                        }
                    }

                    Log.d("Empreendimentos", "Empreendimentos carregados com sucesso.");
                })
                .addOnFailureListener(e -> Log.e("Empreendimentos", "Erro ao carregar empreendimentos", e));
    }

    private void carregarEmpreendimentos(FirebaseCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        List<Empreendimento> listaEmpreendimentos = new ArrayList<>();

        db.collection("Cliente")
                .whereEqualTo("TipoConta", true) // Apenas empreendimentos
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Empreendimento empreendimento = doc.toObject(Empreendimento.class);
                        String idDocumento = doc.getId(); //pega id do documento
                        empreendimento.setIdUser(idDocumento);
                        listaEmpreendimentos.add(empreendimento);
                    }
                    callback.onCallback(listaEmpreendimentos);
                })
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "Erro ao buscar empreendimentos", e);
                    callback.onCallback(new ArrayList<>()); // Retorna lista vazia em caso de erro
                });
    }

    private void exibirEmpreendimentosAleatorios(List<Empreendimento> listaOriginal, int limite, View view) {
        String cepPrioritario = CepSession.getInstance().getCep(); // Obtém o CEP atual da sessão

        List<Empreendimento> comCep = new ArrayList<>();
        List<Empreendimento> outros = new ArrayList<>();

        // Separa os empreendimentos com base no CEP
        for (Empreendimento emp : listaOriginal) {
            if (cepPrioritario != null && cepPrioritario.equals(emp.getCep())) {
                comCep.add(emp);
            } else {
                outros.add(emp);
            }
        }

        // Embaralha os dois grupos separadamente
        Collections.shuffle(comCep);
        Collections.shuffle(outros);

        // Junta os grupos, com os prioritários primeiro
        List<Empreendimento> listaOrdenada = new ArrayList<>();
        listaOrdenada.addAll(comCep);
        listaOrdenada.addAll(outros);

        // Aplica o limite
        List<Empreendimento> listaLimitada = listaOrdenada.subList(0, Math.min(limite, listaOrdenada.size()));

        // Atualiza a interface
        LinearLayout horizontalContainer = view.findViewById(R.id.linearHorizontalContainer);
        LinearLayout verticalContainer = view.findViewById(R.id.linearVerticalContainer);

        horizontalContainer.removeAllViews();
        verticalContainer.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(view.getContext());

        for (Empreendimento emp : listaLimitada) {
            // Item horizontal
            View itemHorizontal = inflater.inflate(R.layout.layout_view_negocio1, horizontalContainer, false);
            ((TextView) itemHorizontal.findViewById(R.id.text_nome)).setText(emp.getNome());
            ((TextView) itemHorizontal.findViewById(R.id.text_categoria)).setText(emp.getCategoria());

            itemHorizontal.setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                bundle.putString("idEmpreendimento", emp.getIdUser());

                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_form_dashboard);
                navController.navigate(R.id.nav_view_perfil_empreedimento, bundle);
            });

            horizontalContainer.addView(itemHorizontal);

            // Item vertical
            View itemVertical = inflater.inflate(R.layout.layout_view_negocio2, verticalContainer, false);
            ((TextView) itemVertical.findViewById(R.id.text_nome)).setText(emp.getNome());
            ((TextView) itemVertical.findViewById(R.id.text_endereco)).setText(emp.getEndereco());
            ((RatingBar) itemVertical.findViewById(R.id.estrelas)).setRating(4.0f); // Exemplo fixo

            itemVertical.setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                bundle.putString("idEmpreendimento", emp.getIdUser());

                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_form_dashboard);
                navController.navigate(R.id.nav_view_perfil_empreedimento, bundle);
            });

            verticalContainer.addView(itemVertical);
        }
    }

    private void buscarCidadeEstadoDoCliente(String clienteId, TextView textEndereco) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Cliente")
                .document(clienteId)
                .collection("Endereco")
                .limit(1) // Pega o único documento existente
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot document = querySnapshot.getDocuments().get(0);

                        String cidade = document.getString("localidade"); // ou "cidade", dependendo do nome usado
                        String estado = document.getString("uf"); // ou "estado", dependendo do nome usado
                        CepSession.getInstance().setCep(document.getString("cep"));

                        if (cidade != null && estado != null) {
                            textEndereco.setText("\uD83D\uDCCD " + cidade + " - " + estado);
                        } else {
                            Toast.makeText(getContext(), "Campos cidade ou estado não encontrados.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Endereço não encontrado.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Erro ao buscar endereço: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void carregarCidadeUf(String cep, Runnable onFinished) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                cepPesquisa = CepUtils.buscarCep(cep);

                requireActivity().runOnUiThread(() -> {
                    onFinished.run(); // <-- executa callback no fim
                });

            } catch (IllegalArgumentException e) {
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                });

            } catch (Exception e) {
                Log.e("CEP_ERRO", "Erro ao buscar CEP", e);
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), "Erro ao buscar CEP", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }
}