package Class.Horarios;

import java.util.EnumMap;

public class HorariosFuncionamento {
    private EnumMap<DiaSemana, DiaHorario> horariosPorDia;

    public HorariosFuncionamento() {
        horariosPorDia = new EnumMap<>(DiaSemana.class);
        for (DiaSemana dia : DiaSemana.values()) {
            horariosPorDia.put(dia, new DiaHorario());
        }
    }

    public DiaHorario getHorarioDoDia(DiaSemana dia) {
        return horariosPorDia.get(dia);
    }

    public void setHorarioDoDia(DiaSemana dia, DiaHorario horario) {
        horariosPorDia.put(dia, horario);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (DiaSemana dia : DiaSemana.values()) {
            sb.append(dia.name()).append(": ")
                    .append(horariosPorDia.get(dia).toString())
                    .append("\n");
        }
        return sb.toString();
    }
}
