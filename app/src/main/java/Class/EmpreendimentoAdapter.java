package Class;

import static androidx.core.os.BundleKt.bundleOf;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appnegocios.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.List;

public class EmpreendimentoAdapter extends RecyclerView.Adapter<EmpreendimentoAdapter.ViewHolder> {

    private final List<Empreendimento> lista;
    private final Fragment fragment; // para navegação

    public EmpreendimentoAdapter(List<Empreendimento> lista, Fragment fragment) {
        this.lista = lista;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_view_negocio3, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Empreendimento emp = lista.get(position);
        holder.bind(emp, fragment);
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nome, idEmp, text_avaliacoes;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nome = itemView.findViewById(R.id.text_nome);
            text_avaliacoes = itemView.findViewById(R.id.text_avaliacao);
            idEmp = itemView.findViewById(R.id.idView);
        }

        public void bind(Empreendimento emp, Fragment fragment) {
            nome.setText(emp.getNome());
            idEmp.setText(emp.getIdUser());
            carregarAvaliacoes(emp.getIdUser());

            itemView.setOnClickListener(v -> {
                // Requer: import androidx.core.os.bundleOf;
                Bundle bundle = new Bundle();
                bundle.putString("idEmpreendimento", emp.getIdUser());

                NavController navController = Navigation.findNavController(fragment.requireActivity(), R.id.nav_host_fragment_content_form_dashboard);
                navController.navigate(R.id.nav_view_perfil_empreedimento, bundle);
            });
        }

        private void carregarAvaliacoes(String idEmp) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("Avaliacoes")
                    .whereEqualTo("idEmpreendimento", idEmp)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        int totalEstrelas = 0;
                        int totalAvaliacoes = 0;

                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            Long estrelas = doc.getLong("estrelas");
                            if (estrelas != null) {
                                totalEstrelas += estrelas;
                                totalAvaliacoes++;
                            }
                        }

                        if (totalAvaliacoes > 0) {
                            float media = (float) totalEstrelas / totalAvaliacoes;
                            text_avaliacoes.setText(String.format("⭐%.1f (%d)", media, totalAvaliacoes));
                        } else {
                            text_avaliacoes.setText("Sem avaliações");
                        }
                    })
                    .addOnFailureListener(e -> {
                        text_avaliacoes.setText("Erro");
                        Log.e("AVALIAÇÕES", "Erro ao buscar avaliações", e);
                    });
        }
    }
}
