package Class;

public class Empreendimento extends Usuario{
    public Empreendimento() {
        this.endereco = "";
        this.descricao = "";
        this.categoria = "";
    }

    private String endereco, descricao, categoria;

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
}
