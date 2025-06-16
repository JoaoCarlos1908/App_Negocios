package Class;

import java.util.ArrayList;
import java.util.List;

public class Empreendimento extends Usuario {

    public Empreendimento() {
        this.endereco = "";
        this.descricao = "";
        this.categoria = "";
        this.subcategorias = new ArrayList<>();
    }

    private String endereco, descricao, categoria;
    private Contato contato;
    private List<String> subcategorias;

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Contato getContato() {
        return contato;
    }

    public void setContato(Contato contato) {
        this.contato = contato;
    }

    public List<String> getSubcategorias() {
        return subcategorias;
    }

    public void setSubcategorias(List<String> subcategorias) {
        this.subcategorias = subcategorias;
    }

    public void adicionarSubcategoria(String subcategoria) {
        if (!this.subcategorias.contains(subcategoria)) {
            this.subcategorias.add(subcategoria);
        }
    }
}
