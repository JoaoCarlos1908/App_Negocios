package Class;

public class CepSession {
    private static CepSession instance;
    private String cep;

    private CepSession() {}

    public static CepSession getInstance() {
        if (instance == null) {
            instance = new CepSession();
        }
        return instance;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getCep() {
        return cep;
    }

    public void limpar() {
        cep = null;
    }
}
