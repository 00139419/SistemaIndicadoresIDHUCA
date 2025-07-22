package com.uca.idhuca.sistema.indicadores.services.impl;

import com.uca.idhuca.sistema.indicadores.backup.config.BackupConfig;
import com.uca.idhuca.sistema.indicadores.backup.config.ScheduleConfig;
import com.uca.idhuca.sistema.indicadores.models.Usuario;
import com.uca.idhuca.sistema.indicadores.services.IAuditoria;
import com.uca.idhuca.sistema.indicadores.utils.Utilidades;
import jakarta.annotation.PostConstruct;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.SimpleTriggerContext;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import static com.uca.idhuca.sistema.indicadores.utils.Constantes.CREAR;

@Service
public class IConfigurableBackupService {

    private static final Logger logger = LoggerFactory.getLogger(IConfigurableBackupService.class);
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    @Autowired
    private BackupConfig config;

    @Value("${backup.postgres.bin.path:}")
    private String postgresBinPath;

    @Value("${backup.base.dir:./data}")
    private String baseBackupDir;

    @Value("${backup.db.subdir:database}")
    private String dbBackupSubdir;

    private ThreadPoolTaskScheduler taskScheduler;

    @Autowired
    private IAuditoria auditoriaService;

    @Autowired
    private Utilidades utils;

    private Map<String, ScheduledFuture<?>> scheduledTasks;

    @PostConstruct
    public void init() {
        this.scheduledTasks = new ConcurrentHashMap<>();
        this.taskScheduler = new ThreadPoolTaskScheduler();
        this.taskScheduler.setPoolSize(10);
        this.taskScheduler.setThreadNamePrefix("backup-scheduler-");
        this.taskScheduler.setWaitForTasksToCompleteOnShutdown(true);
        this.taskScheduler.setAwaitTerminationSeconds(30);
        this.taskScheduler.initialize();

        // Validar pg_dump al inicio
        validatePgDump();
    }

    /**
     * Valida que pg_dump esté disponible en el sistema
     */
    private void validatePgDump() {
        try {
            String command = getPgDumpCommand();
            ProcessBuilder pb = new ProcessBuilder(command, "--version");
            Process process = pb.start();
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                logger.info("pg_dump encontrado y funcional: {}", command);
            } else {
                logger.error("pg_dump no funciona correctamente. Código de salida: {}", exitCode);
            }
        } catch (Exception e) {
            logger.error("pg_dump no está disponible: {}. Verifica la instalación de PostgreSQL.", e.getMessage());
        }
    }

    /**
     * Obtiene el comando completo para pg_dump
     */
    private String getPgDumpCommand() {
        // 1. Verificar si se especificó una ruta personalizada
        if (postgresBinPath != null && !postgresBinPath.trim().isEmpty()) {
            String customPath = postgresBinPath.trim();
            File pgDumpFile;

            // Si ya incluye el ejecutable
            if (customPath.endsWith("pg_dump.exe") || customPath.endsWith("pg_dump")) {
                pgDumpFile = new File(customPath);
            } else {
                // Si es solo el directorio, agregar el ejecutable
                String os = System.getProperty("os.name").toLowerCase();
                String executable = os.contains("windows") ? "pg_dump.exe" : "pg_dump";
                pgDumpFile = new File(customPath, executable);
            }

            if (pgDumpFile.exists()) {
                logger.info("Usando pg_dump desde ruta personalizada: {}", pgDumpFile.getAbsolutePath());
                return pgDumpFile.getAbsolutePath();
            }
            logger.warn("pg_dump no encontrado en ruta personalizada: {}", customPath);
        }

        String os = System.getProperty("os.name").toLowerCase();
        String[] commonPaths;

        if (os.contains("windows")) {
            commonPaths = new String[]{
                    "C:\\Program Files\\PostgreSQL\\16\\bin\\pg_dump.exe",
                    "C:\\Program Files\\PostgreSQL\\15\\bin\\pg_dump.exe",
                    "C:\\Program Files\\PostgreSQL\\14\\bin\\pg_dump.exe",
                    "C:\\Program Files\\PostgreSQL\\13\\bin\\pg_dump.exe",
                    "C:\\Program Files\\PostgreSQL\\12\\bin\\pg_dump.exe",
                    "C:\\Program Files (x86)\\PostgreSQL\\16\\bin\\pg_dump.exe",
                    "C:\\Program Files (x86)\\PostgreSQL\\15\\bin\\pg_dump.exe",
                    "C:\\Program Files (x86)\\PostgreSQL\\14\\bin\\pg_dump.exe",
                    "C:\\Program Files (x86)\\PostgreSQL\\13\\bin\\pg_dump.exe",
                    "C:\\Program Files (x86)\\PostgreSQL\\12\\bin\\pg_dump.exe"
            };
        } else {
            commonPaths = new String[]{
                    "/usr/bin/pg_dump",
                    "/usr/local/bin/pg_dump",
                    "/opt/postgresql/bin/pg_dump",
                    "/usr/local/pgsql/bin/pg_dump"
            };
        }

        // 2. Buscar en rutas comunes
        for (String path : commonPaths) {
            if (new File(path).exists()) {
                logger.info("pg_dump encontrado en: {}", path);
                return path;
            }
        }

        // 3. Intentar usar PATH del sistema
        logger.warn("pg_dump no encontrado en rutas comunes. Intentando usar PATH del sistema...");
        return os.contains("windows") ? "pg_dump.exe" : "pg_dump";
    }

    public void initializeSchedules() {
        logger.info("Inicializando sistema de backup...");
        logger.info("Configuración: {}", config);

        if (config != null && config.isEnabled() && config.getSchedules() != null) {
            logger.info("Configurando {} schedules...", config.getSchedules().size());

            for (ScheduleConfig schedule : config.getSchedules()) {
                if (schedule.isEnabled()) {
                    try {
                        scheduleBackup(schedule);
                        logger.info("Schedule '{}' configurado correctamente", schedule.getName());
                    } catch (Exception e) {
                        logger.error("Error al configurar schedule '{}': {}",
                                schedule.getName(), e.getMessage());
                    }
                }
            }
        } else {
            logger.warn("No se encontraron schedules configurados o el sistema está deshabilitado");
        }
    }

    /**
     * Obtiene el directorio completo donde se guardarán los backups de base de datos
     * Maneja tanto ambiente local como dockerizado
     */
    private String getFullBackupDirectory() {
        // Si config.getBackupDir() es una ruta absoluta, la usamos directamente
        if (config.getBackupDir().startsWith("/") || (config.getBackupDir().length() > 1 && config.getBackupDir().charAt(1) == ':')) {
            return config.getBackupDir();
        }

        // Si es una ruta relativa, la combinamos con baseBackupDir y dbBackupSubdir
        Path fullPath = Paths.get(baseBackupDir, dbBackupSubdir, config.getBackupDir());
        return fullPath.toString();
    }

    /**
     * Crea y valida el directorio de backup
     * Incluye validaciones mejoradas para permisos
     */
    private File createAndValidateBackupDirectory() throws IOException {
        String fullBackupPath = getFullBackupDirectory();
        File backupDirectory = new File(fullBackupPath);

        // Crear directorio si no existe
        if (!backupDirectory.exists()) {
            logger.info("Creando directorio de backup: {}", fullBackupPath);
            if (!backupDirectory.mkdirs()) {
                throw new IOException("No se pudo crear el directorio de backup: " + fullBackupPath);
            }
        }

        // Validar permisos de escritura
        if (!backupDirectory.canWrite()) {
            throw new IOException("No hay permisos de escritura en el directorio de backup: " + fullBackupPath);
        }

        // Log de información útil
        logger.info("Directorio de backup configurado: {}", backupDirectory.getAbsolutePath());
        logger.info("Espacio libre en disco: {} MB", backupDirectory.getFreeSpace() / (1024 * 1024));

        return backupDirectory;
    }

    public void scheduleBackup(ScheduleConfig schedule) {
        if (!schedule.isValidCronExpression()) {
            throw new IllegalArgumentException("Expresión CRON inválida: " + schedule.getCronExpression());
        }

        try {
            // Cancelar tarea existente si existe
            cancelScheduledTask(schedule.getName());

            CronTrigger cronTrigger = new CronTrigger(schedule.getCronExpression());

            ScheduledFuture<?> scheduledTask = taskScheduler.schedule(
                    () -> {
                        logger.info("Ejecutando backup programado: {}", schedule.getName());
                        realizarBackup(schedule);
                    },
                    cronTrigger
            );

            scheduledTasks.put(schedule.getName(), scheduledTask);

            // Actualizar próxima ejecución
            try {
                SimpleTriggerContext triggerContext = new SimpleTriggerContext();
                triggerContext.update(null, null, java.util.Date.from(java.time.Instant.now()));
                LocalDateTime nextExecution = cronTrigger.nextExecutionTime(triggerContext).toInstant()
                        .atZone(java.time.ZoneId.systemDefault())
                        .toLocalDateTime();
                schedule.setNextExecution(nextExecution);
            } catch (Exception e) {
                logger.warn("No se pudo calcular próxima ejecución para {}: {}",
                        schedule.getName(), e.getMessage());
            }

            logger.info("Backup programado exitosamente: '{}' con expresión CRON: '{}'",
                    schedule.getName(), schedule.getCronExpression());

        } catch (Exception e) {
            logger.error("Error al programar backup '{}': {}", schedule.getName(), e.getMessage());
            throw new RuntimeException("Error al programar backup: " + e.getMessage(), e);
        }
    }

    public void realizarBackup(ScheduleConfig schedule) {
        realizarBackup(schedule.getName());
    }

    public void realizarBackup(String scheduleName) {
        if (config == null || !config.isEnabled()) {
            logger.warn("Backup cancelado: sistema deshabilitado");
            return;
        }

        BackupConfig backupConfig = null;

        long startTime = System.currentTimeMillis();
        logger.info("Iniciando backup: {}", scheduleName);

        try {
            // Validar configuración
            if (!validarConfiguracion()) {
                logger.error("Configuración de base de datos inválida");
                return;
            }

            String pgDumpCommand = getPgDumpCommand();
            if (pgDumpCommand == null || pgDumpCommand.isEmpty()) {
                throw new IllegalStateException("pg_dump command not found");
            }

            // Crear comando pg_dump
            List<String> command = new ArrayList<>();
            command.add(pgDumpCommand);
            command.add("-h");
            command.add(config.getDbHost());
            command.add("-p");
            command.add(config.getDbPort());
            command.add("-U");
            command.add(config.getDbUser());
            command.add("-d");
            command.add(config.getDbName());
            command.add("-F");
            command.add("p"); // plain text format
            command.add("--no-password");

            // Crear nombre de archivo único
            String fecha = LocalDateTime.now().format(DATE_FORMAT);
            String nombreArchivo = String.format("backup_%s_%s.sql",
                    scheduleName.replaceAll("[^a-zA-Z0-9]", "_"),
                    fecha);

            // Crear y validar directorio de backup (método mejorado)
            File backupDirectory = createAndValidateBackupDirectory();

            // Archivo de backup
            File backupFile = new File(backupDirectory, nombreArchivo);
            command.add("-f");
            command.add(backupFile.getAbsolutePath());

            // Configurar entorno y ejecutar comando
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.environment().put("PGPASSWORD", config.getDbPassword());
            pb.redirectError(ProcessBuilder.Redirect.INHERIT);

            // Log del comando (ocultando la password)
            String commandString = String.join(" ", command);
            logger.info("Ejecutando comando: {}", commandString);

            Process process = pb.start();
            int exitCode = process.waitFor();

            long duration = System.currentTimeMillis() - startTime;

            if (exitCode == 0 && backupFile.exists() && backupFile.length() > 0) {
                // Log con información adicional útil para debugging
                logger.info("Backup completado exitosamente: '{}' | Archivo: {} | Tamaño: {} bytes | Duración: {} ms | Directorio: {}",
                        scheduleName, backupFile.getName(), backupFile.length(), duration, backupDirectory.getAbsolutePath());

                updateLastExecution(scheduleName);

                Usuario u = new Usuario();
                
                u.setNombre("Sistema interno");
                u.setEmail("SYSTEM");
                
                auditoriaService.add(utils.crearDto(u, CREAR, backupConfig));

                // Opcional: Limpiar backups antiguos
                cleanupOldBackups(backupDirectory, scheduleName);

            } else {
                logger.error("Error al realizar backup '{}'. Código de salida: {} | Duración: {} ms",
                        scheduleName, exitCode, duration);
                if (backupFile.exists()) {
                    backupFile.delete();
                }
            }
        } catch (Exception e) {
            logger.error("Error al realizar backup '{}': {}", scheduleName, e.getMessage(), e);
        }
    }

    /**
     * Limpia backups antiguos para evitar llenar el disco
     * Mantiene solo los últimos N backups por schedule
     */
    @Value("${backup.retention.count:7}")
    private int backupRetentionCount;

    private void cleanupOldBackups(File backupDirectory, String scheduleName) {
        try {
            String schedulePrefix = "backup_" + scheduleName.replaceAll("[^a-zA-Z0-9]", "_") + "_";

            File[] backupFiles = backupDirectory.listFiles((dir, name) ->
                    name.startsWith(schedulePrefix) && name.endsWith(".sql"));

            if (backupFiles != null && backupFiles.length > backupRetentionCount) {
                // Ordenar por fecha de modificación (más antiguos primero)
                Arrays.sort(backupFiles, Comparator.comparingLong(File::lastModified));

                // Eliminar archivos más antiguos
                int filesToDelete = backupFiles.length - backupRetentionCount;
                for (int i = 0; i < filesToDelete; i++) {
                    if (backupFiles[i].delete()) {
                        logger.info("Backup antiguo eliminado: {}", backupFiles[i].getName());
                    } else {
                        logger.warn("No se pudo eliminar backup antiguo: {}", backupFiles[i].getName());
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("Error al limpiar backups antiguos: {}", e.getMessage());
        }
    }


    private boolean validarConfiguracion() {
        boolean valid = config.getDbHost() != null && !config.getDbHost().trim().isEmpty() &&
                config.getDbUser() != null && !config.getDbUser().trim().isEmpty() &&
                config.getDbPassword() != null &&
                config.getDbName() != null && !config.getDbName().trim().isEmpty() &&
                config.getBackupDir() != null && !config.getBackupDir().trim().isEmpty();

        if (!valid) {
            logger.error("Configuración inválida - Host: {}, User: {}, Password: {}, DB: {}, BackupDir: {}",
                    config.getDbHost(), config.getDbUser(),
                    config.getDbPassword() != null ? "***" : null,
                    config.getDbName(), config.getBackupDir());
        }

        return valid;
    }

    private void updateLastExecution(String scheduleName) {
        if (config.getSchedules() != null) {
            config.getSchedules().stream()
                    .filter(s -> s.getName().equals(scheduleName))
                    .findFirst()
                    .ifPresent(schedule -> schedule.setLastExecution(LocalDateTime.now()));
        }
    }

    private void cancelScheduledTask(String scheduleName) {
        ScheduledFuture<?> existingTask = scheduledTasks.get(scheduleName);
        if (existingTask != null && !existingTask.isCancelled()) {
            existingTask.cancel(false);
            scheduledTasks.remove(scheduleName);
            logger.info("Tarea programada cancelada: {}", scheduleName);
        }
    }

    // Métodos públicos para gestión dinámica
    public void addSchedule(ScheduleConfig schedule) {
        if (schedule == null || schedule.getName() == null) {
            throw new IllegalArgumentException("Schedule y nombre no pueden ser null");
        }

        if (config.getSchedules() == null) {
            config.setSchedules(new ArrayList<>());
        }

        boolean exists = config.getSchedules().stream()
                .anyMatch(s -> s.getName().equals(schedule.getName()));

        if (exists) {
            throw new IllegalArgumentException("Ya existe un schedule con el nombre: " + schedule.getName());
        }

        config.getSchedules().add(schedule);

        if (schedule.isEnabled()) {
            scheduleBackup(schedule);
        }


        logger.info("Schedule agregado: {}", schedule.getName());
    }

    public void removeSchedule(String scheduleName) {
        if (scheduleName == null || scheduleName.trim().isEmpty()) {
            throw new IllegalArgumentException("Nombre de schedule no puede ser null o vacío");
        }

        cancelScheduledTask(scheduleName);

        if (config.getSchedules() != null) {
            boolean removed = config.getSchedules().removeIf(s -> s.getName().equals(scheduleName));
            if (removed) {
                logger.info("Schedule eliminado: {}", scheduleName);
            } else {
                logger.warn("Schedule no encontrado para eliminar: {}", scheduleName);
            }
        }
    }

    public void updateSchedule(String scheduleName, ScheduleConfig newSchedule) {
        if (scheduleName == null || newSchedule == null) {
            throw new IllegalArgumentException("Nombre de schedule y nueva configuración no pueden ser null");
        }

        removeSchedule(scheduleName);
        newSchedule.setName(scheduleName);
        addSchedule(newSchedule);
    }

    public void enableSchedule(String scheduleName, boolean enabled) {
        if (scheduleName == null || scheduleName.trim().isEmpty()) {
            throw new IllegalArgumentException("Nombre de schedule no puede ser null o vacío");
        }

        ScheduleConfig schedule = config.getSchedules().stream()
                .filter(s -> s.getName().equals(scheduleName))
                .findFirst()
                .orElse(null);

        if (schedule != null) {
            schedule.setEnabled(enabled);

            if (enabled) {
                scheduleBackup(schedule);
                logger.info("Schedule habilitado: {}", scheduleName);
            } else {
                cancelScheduledTask(scheduleName);
                logger.info("Schedule deshabilitado: {}", scheduleName);
            }

        } else {
            throw new IllegalArgumentException("Schedule no encontrado: " + scheduleName);
        }
    }

    public List<ScheduleConfig> getAllSchedules() {
        return config.getSchedules() != null ? new ArrayList<>(config.getSchedules()) : new ArrayList<>();
    }

    public ScheduleConfig getSchedule(String scheduleName) {
        return config.getSchedules().stream()
                .filter(s -> s.getName().equals(scheduleName))
                .findFirst()
                .orElse(null);
    }

    public Map<String, String> getSystemStatus() {
        Map<String, String> status = new ConcurrentHashMap<>();
        status.put("enabled", String.valueOf(config.isEnabled()));
        status.put("totalSchedules", String.valueOf(config.getSchedules() != null ? config.getSchedules().size() : 0));
        status.put("activeSchedules", String.valueOf(scheduledTasks.size()));
        status.put("configuredBackupDir", config.getBackupDir());
        status.put("fullBackupDirectory", getFullBackupDirectory());
        status.put("baseBackupDir", baseBackupDir);
        status.put("dbBackupSubdir", dbBackupSubdir);
        status.put("dbHost", config.getDbHost());
        status.put("dbName", config.getDbName());
        status.put("pgdumpCommand", getPgDumpCommand());
        status.put("backupRetentionCount", String.valueOf(backupRetentionCount));

        // Información del directorio
        try {
            File backupDir = new File(getFullBackupDirectory());
            status.put("backupDirExists", String.valueOf(backupDir.exists()));
            status.put("backupDirWritable", String.valueOf(backupDir.canWrite()));
            if (backupDir.exists()) {
                status.put("backupDirFreeSpace", String.valueOf(backupDir.getFreeSpace() / (1024 * 1024)) + " MB");
            }
        } catch (Exception e) {
            status.put("backupDirError", e.getMessage());
        }

        return status;
    }

    public void shutdown() {
        logger.info("Cerrando sistema de backup...");

        scheduledTasks.values().forEach(task -> {
            if (!task.isCancelled()) {
                task.cancel(false);
            }
        });
        scheduledTasks.clear();

        if (taskScheduler != null) {
            taskScheduler.shutdown();
            logger.info("Task scheduler cerrado");
        }

        logger.info("Sistema de backup cerrado correctamente");
    }

}