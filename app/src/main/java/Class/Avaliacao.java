package Class;

public class Avaliacao {
    private String idAvaliacao;            // ID da avaliação (gerado automaticamente ou pelo Firebase)
    private String idEmpreendimento;       // ID do empreendimento avaliado
    private int estrelas;                  // Número de estrelas (1 a 5)
    private String descricao;              // Comentário da avaliação
    private String idAvaliador;          // Nome da pessoa que avaliou
    private boolean anonima;               // Se a avaliação é anônima
    private String resposta;   // Resposta opcional do empreendedor

    // Construtor vazio necessário para Firebase
    public Avaliacao() {
    }

    // Construtor completo (sem idAvaliacao e resposta inicial)
    public Avaliacao(String idEmpreendimento, int estrelas, String descricao, String nomeAvaliador, boolean anonima) {
        this.idEmpreendimento = idEmpreendimento;
        this.estrelas = estrelas;
        this.descricao = descricao;
        this.idAvaliacao = nomeAvaliador;
        this.anonima = anonima;
        this.resposta = "";
    }

    // Getters e Setters

    public String getIdAvaliacao() {
        return idAvaliacao;
    }

    public void setIdAvaliacao(String idAvaliacao) {
        this.idAvaliacao = idAvaliacao;
    }

    public String getIdEmpreendimento() {
        return idEmpreendimento;
    }

    public void setIdEmpreendimento(String idEmpreendimento) {
        this.idEmpreendimento = idEmpreendimento;
    }

    public String getIdAvaliador() {
        return idAvaliador;
    }

    public void setIdAvaliador(String idAvaliador) {
        this.idAvaliador = idAvaliador;
    }

    public int getEstrelas() {
        return estrelas;
    }

    public void setEstrelas(int estrelas) {
        this.estrelas = estrelas;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getNomeAvaliador() {
        return anonima ? "Anônimo" : idAvaliador;
    }

    public void setNomeAvaliador(String nomeAvaliador) {
        this.idAvaliador = nomeAvaliador;
    }

    public boolean isAnonima() {
        return anonima;
    }

    public void setAnonima(boolean anonima) {
        this.anonima = anonima;
    }

    public String getResposta() {
        return resposta;
    }

    public void setResposta(String resposta) {
        this.resposta = resposta;
    }
}
