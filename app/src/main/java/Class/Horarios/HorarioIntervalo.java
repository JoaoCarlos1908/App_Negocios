package Class.Horarios;

public class HorarioIntervalo {
    private int horaAbertura;
    private int minutoAbertura;
    private int horaFechamento;
    private int minutoFechamento;

    public HorarioIntervalo(int horaAbertura, int minutoAbertura,
                            int horaFechamento, int minutoFechamento) {
        this.horaAbertura = horaAbertura;
        this.minutoAbertura = minutoAbertura;
        this.horaFechamento = horaFechamento;
        this.minutoFechamento = minutoFechamento;
    }

    public String toString() {
        return String.format("%02d:%02d - %02d:%02d",
                horaAbertura, minutoAbertura, horaFechamento, minutoFechamento);
    }

    // Getters e setters omitidos por brevidade
}
