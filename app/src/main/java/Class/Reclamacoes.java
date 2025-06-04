package Class;

public class Reclamacoes {
    private String idReclamacao;            // ID da avaliação (gerado automaticamente ou pelo Firebase)
    private String idEmpreendimento;       // ID do empreendimento avaliado
    private String descricao;              // Comentário da avaliação
    private String idCliente;          // Nome da pessoa que avaliou
    private boolean anonimo, respondida;               // Se a avaliação é anônima
    private String resposta;   // Resposta opcional do empreendedor

    // Construtor vazio necessário para Firebase
    public Reclamacoes() {
    }

    // Construtor completo (sem idAvaliacao e resposta inicial)
    public Reclamacoes(String idEmpreendimento, String descricao, String nomeCliente, boolean respondida) {
        this.idEmpreendimento = idEmpreendimento;
        this.descricao = descricao;
        this.idCliente = nomeCliente;
        this.respondida = respondida;
        this.resposta = "";
    }

    // Getters e Setters

    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }

    public boolean getRespondida() {
        return respondida;
    }

    public void setRespondida(boolean respondida) {
        this.respondida = respondida;
    }

    public String getIdReclamacao() {
        return idReclamacao;
    }

    public void setIdReclamacao(String idAvaliacao) {
        this.idReclamacao = idAvaliacao;
    }

    public String getIdEmpreendimento() {
        return idEmpreendimento;
    }

    public void setIdEmpreendimento(String idEmpreendimento) {
        this.idEmpreendimento = idEmpreendimento;
    }

    public String getDescricao(){
        return descricao;
    }
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getNomeAvaliador() {
        return idCliente;
    }

    public void setNomeAvaliador(String nomeAvaliador) {
        this.idCliente = nomeAvaliador;
    }

    public String getResposta() {
        return resposta;
    }

    public void setResposta(String resposta) {
        this.resposta = resposta;
    }

    public boolean isAnonima() {
        return anonimo;
    }

    public void setAnonimo(boolean anonimo) {
        this.anonimo = anonimo;
    }
}
