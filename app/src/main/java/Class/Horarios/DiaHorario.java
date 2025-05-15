package Class.Horarios;

import java.util.ArrayList;
import java.util.List;

public class DiaHorario {
    private boolean aberto24h;
    private boolean fechado;
    private List<HorarioIntervalo> intervalos;

    public DiaHorario() {
        this.aberto24h = false;
        this.fechado = false;
        this.intervalos = new ArrayList<>();
    }

    public void adicionarIntervalo(HorarioIntervalo intervalo) {
        this.intervalos.add(intervalo);
    }

    public void setAberto24h(boolean aberto24h) {
        this.aberto24h = aberto24h;
        if (aberto24h) {
            this.fechado = false;
            this.intervalos.clear();
        }
    }

    public void setFechado(boolean fechado) {
        this.fechado = fechado;
        if (fechado) {
            this.aberto24h = false;
            this.intervalos.clear();
        }
    }

    public String toString() {
        if (aberto24h) return "Aberto 24h";
        if (fechado) return "Fechado";
        return intervalos.isEmpty() ? "Sem horário" : intervalos.toString();
    }

    // Getters e setters omitidos por brevidade
}
