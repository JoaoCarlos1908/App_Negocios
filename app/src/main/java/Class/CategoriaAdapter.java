package Class;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appnegocios.R;

import java.util.List;

public class CategoriaAdapter extends RecyclerView.Adapter<CategoriaAdapter.ViewHolder> {

    private final List<String> categorias;
    private final OnCategoriaClickListener listener;

    public interface OnCategoriaClickListener {
        void onCategoriaClick(String categoria);
    }

    public CategoriaAdapter(List<String> categorias, OnCategoriaClickListener listener) {
        this.categorias = categorias;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_view_categoria, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String categoria = categorias.get(position);
        holder.nomeCategoria.setText(categoria);
        holder.itemView.setOnClickListener(v -> listener.onCategoriaClick(categoria));
    }

    @Override
    public int getItemCount() {
        return categorias.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nomeCategoria;

        public ViewHolder(View itemView) {
            super(itemView);
            nomeCategoria = itemView.findViewById(R.id.text_nomeCat);
        }
    }
}
