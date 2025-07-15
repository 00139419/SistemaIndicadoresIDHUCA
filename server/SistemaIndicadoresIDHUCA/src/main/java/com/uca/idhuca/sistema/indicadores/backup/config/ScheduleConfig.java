package com.uca.idhuca.sistema.indicadores.backup.config;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ScheduleConfig {

    // Getters y setters
    private String name;
    private String cronExpression;
    private boolean enabled;
    private String description;
    private LocalDateTime lastExecution;
    private LocalDateTime nextExecution;

    // Constructor por defecto
    public ScheduleConfig() {}

    // Constructor con parámetros principales
    public ScheduleConfig(String name, String cronExpression, String description) {
        this.name = name;
        this.cronExpression = cronExpression;
        this.description = description;
        this.enabled = true;
    }

    // Constructor completo
    public ScheduleConfig(String name, String cronExpression, String description, boolean enabled) {
        this.name = name;
        this.cronExpression = cronExpression;
        this.description = description;
        this.enabled = enabled;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLastExecution(LocalDateTime lastExecution) {
        this.lastExecution = lastExecution;
    }

    public void setNextExecution(LocalDateTime nextExecution) {
        this.nextExecution = nextExecution;
    }

    // Método helper para validar expresión CRON
    public boolean isValidCronExpression() {
        if (cronExpression == null || cronExpression.trim().isEmpty()) {
            return false;
        }

        try {
            // Validación básica: debe tener 6 partes separadas por espacios
            String[] parts = cronExpression.trim().split("\\s+");
            return parts.length == 6;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String toString() {
        return "ScheduleConfig{" +
                "name='" + name + '\'' +
                ", cronExpression='" + cronExpression + '\'' +
                ", enabled=" + enabled +
                ", description='" + description + '\'' +
                ", lastExecution=" + lastExecution +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScheduleConfig that = (ScheduleConfig) o;
        return name != null ? name.equals(that.name) : that.name == null;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
