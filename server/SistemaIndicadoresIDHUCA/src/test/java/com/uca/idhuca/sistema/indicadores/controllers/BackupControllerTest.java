package com.uca.idhuca.sistema.indicadores.controllers;

import com.uca.idhuca.sistema.indicadores.backup.config.ScheduleConfig;
import com.uca.idhuca.sistema.indicadores.services.impl.IConfigurableBackupService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BackupControllerTest {

    @Mock
    private IConfigurableBackupService backupService;

    @InjectMocks
    private BackupController backupController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetSystemStatus_Success() {
        // Arrange
        Map<String, String> mockStatus = new HashMap<>();
        mockStatus.put("activeSchedules", "2");
        when(backupService.getSystemStatus()).thenReturn(mockStatus);

        // Act
        ResponseEntity<Map<String, Object>> response = backupController.getSystemStatus();

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("success", response.getBody().get("status"));
        assertEquals(mockStatus, response.getBody().get("data"));
        verify(backupService, times(1)).getSystemStatus();
    }

    @Test
    void testAddSchedule_Success() {
        // Arrange
        ScheduleConfig schedule = new ScheduleConfig("test-schedule", "0 0 2 * * *", "Test backup");
        doNothing().when(backupService).addSchedule(schedule);

        // Act
        ResponseEntity<Map<String, Object>> response = backupController.addSchedule(schedule);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("success", response.getBody().get("status"));
        assertEquals("Schedule agregado exitosamente: test-schedule", response.getBody().get("message"));
        verify(backupService, times(1)).addSchedule(schedule);
    }

    @Test
    void testGetAllSchedules_Success() {
        // Arrange
        List<ScheduleConfig> mockSchedules = Arrays.asList(
                new ScheduleConfig("test-schedule", "0 0 2 * * *", "Test backup")
        );
        when(backupService.getAllSchedules()).thenReturn(mockSchedules);

        // Act
        ResponseEntity<Map<String, Object>> response = backupController.getAllSchedules();

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("success", response.getBody().get("status"));
        assertEquals(mockSchedules, response.getBody().get("data"));
        assertEquals(1, response.getBody().get("total"));
        verify(backupService, times(1)).getAllSchedules();
    }

    @Test
    void testRemoveSchedule_Success() {
        // Arrange
        String scheduleName = "test-schedule";
        doNothing().when(backupService).removeSchedule(scheduleName);

        // Act
        ResponseEntity<Map<String, Object>> response = backupController.removeSchedule(scheduleName);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("success", response.getBody().get("status"));
        assertEquals("Schedule eliminado exitosamente: test-schedule", response.getBody().get("message"));
        verify(backupService, times(1)).removeSchedule(scheduleName);
    }

    @Test
    void testEnableSchedule_Success() {
        // Arrange
        String scheduleName = "test-schedule";
        boolean enabled = true;
        doNothing().when(backupService).enableSchedule(scheduleName, enabled);

        // Act
        ResponseEntity<Map<String, Object>> response = backupController.enableSchedule(scheduleName, enabled);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("success", response.getBody().get("status"));
        assertEquals("Schedule habilitado: test-schedule", response.getBody().get("message"));
        verify(backupService, times(1)).enableSchedule(scheduleName, enabled);
    }

    @Test
    void testExecuteManualBackup_Success() {
        // Arrange
        String scheduleName = "test-schedule";
        doNothing().when(backupService).realizarBackup(scheduleName);

        // Act
        ResponseEntity<Map<String, Object>> response = backupController.executeManualBackup(scheduleName);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("success", response.getBody().get("status"));
        assertEquals("Backup manual iniciado: test-schedule", response.getBody().get("message"));
    }
}