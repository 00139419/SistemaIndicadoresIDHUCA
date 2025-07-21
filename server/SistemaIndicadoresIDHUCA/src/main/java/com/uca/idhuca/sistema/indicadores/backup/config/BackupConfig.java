package com.uca.idhuca.sistema.indicadores.backup.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.ArrayList;

@Getter
@Component
@ConfigurationProperties(prefix = "backup")
public class BackupConfig {
    // Getters y setters
    private boolean enabled = true;
    private String backupDir = "/app/data/backups";
    private String dbHost = "localhost";
    private String dbPort = "3306";
    private String dbUser = "usuario";
    private String dbPassword = "contra";
    private String dbName = "nombre_base_datos";
    private List<ScheduleConfig> schedules = new ArrayList<>();

    // Constructor por defecto
    public BackupConfig() {}

    // Constructor con parámetros
    public BackupConfig(String backupDir, String dbPort, String dbHost, String dbUser,
                        String dbPassword, String dbName, List<ScheduleConfig> schedules) {
        this.backupDir = backupDir;
        this.dbHost = dbHost;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;
        this.dbName = dbName;
        this.dbPort = dbPort;
        this.schedules = schedules != null ? schedules : new ArrayList<>();
        this.enabled = true;
    }

    public void setBackupDir(String backupDir) {
        this.backupDir = backupDir;
    }

    public void setDbPort(String dbPort) {
        this.dbPort = dbPort;
    }

    public void setDbHost(String dbHost) {
        this.dbHost = dbHost;
    }

    public void setDbUser(String dbUser) {
        this.dbUser = dbUser;
    }

    public void setDbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public void setSchedules(List<ScheduleConfig> schedules) {
        this.schedules = schedules != null ? schedules : new ArrayList<>();
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return "BackupConfig{" +
                "backupDir='" + backupDir + '\'' +
                ", dbHost='" + dbHost + '\'' +
                ", dbUser='" + dbUser + '\'' +
                ", dbName='" + dbName + '\'' +
                ", enabled=" + enabled +
                ", dbPort='" + dbPort + '\'' +
                ", schedules=" + schedules.size() +
                '}';
    }
}
