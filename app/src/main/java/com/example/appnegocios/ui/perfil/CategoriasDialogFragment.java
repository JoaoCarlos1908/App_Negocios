package com.example.appnegocios.ui.perfil;

import android.app.Dialog;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.appnegocios.R;
import com.example.appnegocios.databinding.FragmentDashboardBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class CategoriasDialogFragment extends DialogFragment {

    private FragmentDashboardBinding binding;
    private Button salvar;
    private Spinner categoria;
    private TextView categoriaAtual;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_categorias, container, false);
        salvar = view.findViewById(R.id.btnSalvar);
        categoria = view.findViewById(R.id.spinnerCategoria);
        categoriaAtual = view.findViewById(R.id.text_CategoriaAtual);


        DocumentReference documentReference = db.collection("Cliente").document(usuarioID);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (documentSnapshot != null) {
                    String text = documentSnapshot.getString("categoria");
                    categoriaAtual.setText("Categoria: " + text);
                    String valorDesejado = text;
                    ArrayAdapter<String> adapter = (ArrayAdapter<String>) categoria.getAdapter();

                    for (int i = 0; i < adapter.getCount(); i++) {
                        if (adapter.getItem(i).equals(valorDesejado)) {
                            categoria.setSelection(i);
                            break;
                        }
                    }

                }
            }
        });

        salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String categoriaSelect = categoria.getSelectedItem().toString();
                db.collection("Cliente").document(usuarioID)
                        .update(
                                "categoria", categoriaSelect)
                        .addOnSuccessListener(aVoid -> {
                            Snackbar snackbar = Snackbar.make(v, "Categoria atualizado!", Snackbar.LENGTH_SHORT);
                            snackbar.setBackgroundTint(Color.GREEN);
                            snackbar.setTextColor(Color.BLACK);
                            snackbar.show();

                            categoriaAtual.setText("Categoria atual: " + categoriaSelect);
                        })
                        .addOnFailureListener(e -> {
                            Snackbar snackbar = Snackbar.make(v, "Erro: " + e.getMessage(), Snackbar.LENGTH_SHORT);
                            snackbar.setBackgroundTint(Color.RED);
                            snackbar.setTextColor(Color.WHITE);
                            snackbar.show();
                        });
            }
        });

        return  view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            dialog.getWindow().setLayout(
                    (int) (Resources.getSystem().getDisplayMetrics().widthPixels * 0.9),
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }
    }

@Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}