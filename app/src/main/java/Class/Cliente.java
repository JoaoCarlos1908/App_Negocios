package Class;

public class Cliente extends Usuario{
    public Cliente() {
        this.cep = "";
    }

    private String cep;

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }
}
