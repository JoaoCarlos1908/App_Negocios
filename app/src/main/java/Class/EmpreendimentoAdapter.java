package Class;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appnegocios.R;

import java.util.List;

public class EmpreendimentoAdapter extends RecyclerView.Adapter<EmpreendimentoAdapter.ViewHolder> {

    private final List<Empreendimento> lista;

    public EmpreendimentoAdapter(List<Empreendimento> lista) {
        this.lista = lista;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_view_negocio1, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Empreendimento emp = lista.get(position);
        holder.nome.setText(emp.getNome());
        holder.categoria.setText(emp.getDescricao());
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nome, categoria;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nome = itemView.findViewById(R.id.text_nome);
            categoria = itemView.findViewById(R.id.text_categoria);
        }
    }
}
