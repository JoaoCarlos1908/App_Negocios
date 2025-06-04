package Class;

public class Usuario {
    public Usuario() {
        this.idUser = "";
        this.nome = "";
        this.email = "";
        this.tell = "";
        this.senha = "";
        this.tipoConta = false;
    }

    private String idUser, nome, email, tell, senha;
    private Boolean tipoConta;

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTell() {
        return tell;
    }

    public void setTell(String tell) {
        this.tell = tell;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Boolean getTipoConta() {
        return tipoConta;
    }

    public void setTipoConta(Boolean tipoConta) {
        this.tipoConta = tipoConta;
    }
}

