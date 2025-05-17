package Class.Horarios;

public class HorarioIntervalo {
    private String horarioAbertura;
    private String horarioFechamento;

    // Construtor vazio necessário para Firebase
    public HorarioIntervalo() {
    }

    public HorarioIntervalo(String horarioAbertura, String horarioFechamento) {
        this.horarioAbertura = horarioAbertura;
        this.horarioFechamento = horarioFechamento;
    }

    public String getHorarioAbertura() {
        return horarioAbertura;
    }

    public void setHorarioAbertura(String horarioAbertura) {
        this.horarioAbertura = horarioAbertura;
    }

    public String getHorarioFechamento() {
        return horarioFechamento;
    }

    public void setHorarioFechamento(String horarioFechamento) {
        this.horarioFechamento = horarioFechamento;
    }

    @Override
    public String toString() {
        return horarioAbertura + " - " + horarioFechamento;
    }
}
