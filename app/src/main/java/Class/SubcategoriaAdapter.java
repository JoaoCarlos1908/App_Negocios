package Class;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appnegocios.R;

import java.util.List;

public class SubcategoriaAdapter extends RecyclerView.Adapter<SubcategoriaAdapter.ViewHolder> {

    private final List<String> subcategorias;
    private final OnSubcategoriaClickListener listener;

    public interface OnSubcategoriaClickListener {
        void onSubcategoriaClick(String subcategoria);
    }

    public SubcategoriaAdapter(List<String> subcategorias, OnSubcategoriaClickListener listener) {
        this.subcategorias = subcategorias;
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
        String sub = subcategorias.get(position);
        holder.nomeSubcategoria.setText(sub);
        holder.itemView.setOnClickListener(v -> listener.onSubcategoriaClick(sub));
    }

    @Override
    public int getItemCount() {
        return subcategorias.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nomeSubcategoria;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nomeSubcategoria = itemView.findViewById(R.id.text_nomeCat);
        }
    }
}
