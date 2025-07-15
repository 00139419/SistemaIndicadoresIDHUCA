package com.uca.idhuca.sistema.indicadores.controllers;

import com.uca.idhuca.sistema.indicadores.backup.config.ScheduleConfig;
import com.uca.idhuca.sistema.indicadores.services.impl.IConfigurableBackupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/backup")
@CrossOrigin(origins = "*")
public class BackupController {

    private static final Logger logger = LoggerFactory.getLogger(BackupController.class);

    @Autowired
    private IConfigurableBackupService backupService;

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getSystemStatus() {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("data", backupService.getSystemStatus());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error al obtener estado del sistema: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(createErrorResponse("Error al obtener estado del sistema", e.getMessage()));
        }
    }

    @GetMapping("/schedules")
    public ResponseEntity<Map<String, Object>> getAllSchedules() {
        try {
            List<ScheduleConfig> schedules = backupService.getAllSchedules();
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("data", schedules);
            response.put("total", schedules.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error al obtener schedules: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(createErrorResponse("Error al obtener schedules", e.getMessage()));
        }
    }

    @GetMapping("/schedules/{name}")
    public ResponseEntity<Map<String, Object>> getSchedule(@PathVariable String name) {
        try {
            ScheduleConfig schedule = backupService.getSchedule(name);
            Map<String, Object> response = new HashMap<>();

            if (schedule != null) {
                response.put("status", "success");
                response.put("data", schedule);
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "error");
                response.put("message", "Schedule no encontrado: " + name);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error al obtener schedule '{}': {}", name, e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(createErrorResponse("Error al obtener schedule", e.getMessage()));
        }
    }

    @PostMapping("/schedules")
    public ResponseEntity<Map<String, Object>> addSchedule(@RequestBody ScheduleConfig schedule) {
        try {
            backupService.addSchedule(schedule);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Schedule agregado exitosamente: " + schedule.getName());
            response.put("data", schedule);

            logger.info("Schedule agregado vía API: {}", schedule.getName());
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            logger.warn("Error de validación al agregar schedule: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(createErrorResponse("Error de validación", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error al agregar schedule: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(createErrorResponse("Error al agregar schedule", e.getMessage()));
        }
    }

    @PutMapping("/schedules/{name}")
    public ResponseEntity<Map<String, Object>> updateSchedule(
            @PathVariable String name,
            @RequestBody ScheduleConfig schedule) {
        try {
            backupService.updateSchedule(name, schedule);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Schedule actualizado exitosamente: " + name);
            response.put("data", schedule);

            logger.info("Schedule actualizado vía API: {}", name);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            logger.warn("Error de validación al actualizar schedule '{}': {}", name, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(createErrorResponse("Error de validación", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error al actualizar schedule '{}': {}", name, e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(createErrorResponse("Error al actualizar schedule", e.getMessage()));
        }
    }

    @DeleteMapping("/schedules/{name}")
    public ResponseEntity<Map<String, Object>> removeSchedule(@PathVariable String name) {
        try {
            backupService.removeSchedule(name);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Schedule eliminado exitosamente: " + name);

            logger.info("Schedule eliminado vía API: {}", name);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            logger.warn("Error al eliminar schedule '{}': {}", name, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(createErrorResponse("Error de validación", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error al eliminar schedule '{}': {}", name, e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(createErrorResponse("Error al eliminar schedule", e.getMessage()));
        }
    }

    @PutMapping("/schedules/{name}/enable")
    public ResponseEntity<Map<String, Object>> enableSchedule(
            @PathVariable String name,
            @RequestParam boolean enabled) {
        try {
            backupService.enableSchedule(name, enabled);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Schedule " + (enabled ? "habilitado" : "deshabilitado") + ": " + name);

            logger.info("Schedule {} vía API: {}", enabled ? "habilitado" : "deshabilitado", name);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            logger.warn("Error al cambiar estado del schedule '{}': {}", name, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(createErrorResponse("Error de validación", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error al cambiar estado del schedule '{}': {}", name, e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(createErrorResponse("Error al cambiar estado del schedule", e.getMessage()));
        }
    }

    @PostMapping("/schedules/{name}/execute")
    public ResponseEntity<Map<String, Object>> executeManualBackup(@PathVariable String name) {
        try {
            // Ejecutar backup en un hilo separado para no bloquear la respuesta
            new Thread(() -> {
                try {
                    backupService.realizarBackup(name);
                } catch (Exception e) {
                    logger.error("Error en backup manual '{}': {}", name, e.getMessage());
                }
            }).start();

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Backup manual iniciado: " + name);

            logger.info("Backup manual iniciado vía API: {}", name);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error al ejecutar backup manual '{}': {}", name, e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(createErrorResponse("Error al ejecutar backup manual", e.getMessage()));
        }
    }

    @PostMapping("/execute-all")
    public ResponseEntity<Map<String, Object>> executeAllBackups() {
        try {
            List<ScheduleConfig> schedules = backupService.getAllSchedules();

            // Ejecutar todos los backups habilitados en hilos separados
            schedules.stream()
                    .filter(ScheduleConfig::isEnabled)
                    .forEach(schedule -> {
                        new Thread(() -> {
                            try {
                                backupService.realizarBackup(schedule.getName());
                            } catch (Exception e) {
                                logger.error("Error en backup manual '{}': {}", schedule.getName(), e.getMessage());
                            }
                        }).start();
                    });

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Backups manuales iniciados para todos los schedules habilitados");
            response.put("schedulesExecuted", schedules.stream()
                    .filter(ScheduleConfig::isEnabled)
                    .map(ScheduleConfig::getName)
                    .toArray());

            logger.info("Backups manuales iniciados para todos los schedules habilitados");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error al ejecutar todos los backups: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(createErrorResponse("Error al ejecutar todos los backups", e.getMessage()));
        }
    }

    // Endpoints para configuración rápida de schedules comunes
    @PostMapping("/quick-schedules/daily")
    public ResponseEntity<Map<String, Object>> createDailySchedule(
            @RequestParam String name,
            @RequestParam(defaultValue = "2") int hour,
            @RequestParam(defaultValue = "0") int minute) {
        try {
            String cronExpression = String.format("0 %d %d * * *", minute, hour);
            ScheduleConfig schedule = new ScheduleConfig(
                    name,
                    cronExpression,
                    String.format("Backup diario a las %02d:%02d", hour, minute)
            );

            backupService.addSchedule(schedule);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Schedule diario creado: " + name);
            response.put("data", schedule);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error al crear schedule diario: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(createErrorResponse("Error al crear schedule diario", e.getMessage()));
        }
    }

    @PostMapping("/quick-schedules/weekly")
    public ResponseEntity<Map<String, Object>> createWeeklySchedule(
            @RequestParam String name,
            @RequestParam(defaultValue = "SUN") String dayOfWeek,
            @RequestParam(defaultValue = "3") int hour,
            @RequestParam(defaultValue = "0") int minute) {
        try {
            String cronExpression = String.format("0 %d %d * * %s", minute, hour, dayOfWeek);
            ScheduleConfig schedule = new ScheduleConfig(
                    name,
                    cronExpression,
                    String.format("Backup semanal los %s a las %02d:%02d", dayOfWeek, hour, minute)
            );

            backupService.addSchedule(schedule);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Schedule semanal creado: " + name);
            response.put("data", schedule);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error al crear schedule semanal: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(createErrorResponse("Error al crear schedule semanal", e.getMessage()));
        }
    }

    @PostMapping("/quick-schedules/monthly")
    public ResponseEntity<Map<String, Object>> createMonthlySchedule(
            @RequestParam String name,
            @RequestParam(defaultValue = "1") int dayOfMonth,
            @RequestParam(defaultValue = "4") int hour,
            @RequestParam(defaultValue = "0") int minute) {
        try {
            String cronExpression = String.format("0 %d %d %d * *", minute, hour, dayOfMonth);
            ScheduleConfig schedule = new ScheduleConfig(
                    name,
                    cronExpression,
                    String.format("Backup mensual el día %d a las %02d:%02d", dayOfMonth, hour, minute)
            );

            backupService.addSchedule(schedule);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Schedule mensual creado: " + name);
            response.put("data", schedule);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error al crear schedule mensual: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(createErrorResponse("Error al crear schedule mensual", e.getMessage()));
        }
    }

    @PostMapping("/quick-schedules/interval")
    public ResponseEntity<Map<String, Object>> createIntervalSchedule(
            @RequestParam String name,
            @RequestParam int intervalHours) {
        try {
            String cronExpression = String.format("0 0 */%d * * *", intervalHours);
            ScheduleConfig schedule = new ScheduleConfig(
                    name,
                    cronExpression,
                    String.format("Backup cada %d horas", intervalHours)
            );

            backupService.addSchedule(schedule);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Schedule por intervalo creado: " + name);
            response.put("data", schedule);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error al crear schedule por intervalo: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(createErrorResponse("Error al crear schedule por intervalo", e.getMessage()));
        }
    }

    // Endpoint para obtener ejemplos de expresiones CRON
    @GetMapping("/cron-examples")
    public ResponseEntity<Map<String, Object>> getCronExamples() {
        Map<String, String> examples = new HashMap<>();
        examples.put("cada-minuto", "0 * * * * *");
        examples.put("cada-hora", "0 0 * * * *");
        examples.put("diario-2am", "0 0 2 * * *");
        examples.put("semanal-domingo-3am", "0 0 3 * * SUN");
        examples.put("mensual-dia-1-4am", "0 0 4 1 * *");
        examples.put("cada-2-meses", "0 0 4 1 */2 *");
        examples.put("cada-6-horas", "0 0 */6 * * *");
        examples.put("3-veces-al-dia", "0 0 8,14,20 * * *");
        examples.put("dias-laborables-8am", "0 0 8 * * MON-FRI");
        examples.put("cada-30-minutos", "0 */30 * * * *");

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("data", examples);
        response.put("description", "Ejemplos de expresiones CRON útiles");

        return ResponseEntity.ok(response);
    }

    // Método helper para crear respuestas de error consistentes
    private Map<String, Object> createErrorResponse(String message, String details) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", message);
        response.put("details", details);
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }
}