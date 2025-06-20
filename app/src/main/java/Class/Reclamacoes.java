package Class;

public class Reclamacoes {
    private String idReclamacao;            // ID da avaliação (gerado automaticamente ou pelo Firebase)
    private String idEmpreendimento;       // ID do empreendimento avaliado
    private String reclamacao;              // Comentário da avaliação
    private String idCliente;          // Nome da pessoa que avaliou
    private boolean anonima, respondida;               // Se a avaliação é anônima
    private String resposta;   // Resposta opcional do empreendedor
    private String nomeClinete, nomeEmpre;

    // Construtor vazio necessário para Firebase
    public Reclamacoes() {
    }

    // Construtor completo (sem idAvaliacao e resposta inicial)
    public Reclamacoes(String idEmpreendimento, String descricao, String nomeCliente, boolean respondida) {
        this.idEmpreendimento = idEmpreendimento;
        this.reclamacao = descricao;
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
    
    public String getNomeClinete() {
        return nomeClinete;
    }

    public void setNomeClinete(String nomeClinete) {
        this.nomeClinete = nomeClinete;
    }

    public String getNomeEmpre() {
        return nomeEmpre;
    }

    public void setNomeEmpre(String nomeEmpre) {
        this.nomeEmpre = nomeEmpre;
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

    public String getReclamacao(){
        return reclamacao;
    }

    public void setReclamacao(String descricao) {
        this.reclamacao = descricao;
    }

    public String getResposta() {
        return resposta;
    }

    public void setResposta(String resposta) {
        this.resposta = resposta;
    }

    public boolean isAnonima() {
        return anonima;
    }

    public void setAnonima(boolean anonima) {
        this.anonima = anonima;
    }
}
