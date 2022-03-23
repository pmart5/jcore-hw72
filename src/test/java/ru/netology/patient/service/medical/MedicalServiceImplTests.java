package ru.netology.patient.service.medical;

import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.MockitoSession;
import ru.netology.patient.entity.*;
import ru.netology.patient.repository.*;
import ru.netology.patient.service.alert.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public class MedicalServiceImplTests {

    MedicalServiceImpl sut;
    MockitoSession session;
    PatientInfoRepository PatientInfoFileRepositoryMock = Mockito.mock(PatientInfoFileRepository.class);
    SendAlertService sendAlertServiceMock = Mockito.mock(SendAlertServiceImpl.class);

    @BeforeAll
    public static void startedAll() {
        System.out.println("Тесты запушены");
    }

    @BeforeEach
    public void started() {
        sut = new MedicalServiceImpl(PatientInfoFileRepositoryMock, sendAlertServiceMock);
        session = Mockito.mockitoSession()
                .initMocks(this)
                .startMocking();
        System.out.println("Тест запушен");
    }

    @AfterEach
    public void finished() {
        session.finishMocking();
        System.out.println();
        System.out.println("Тест выполнен");
    }

    @AfterAll
    public static void finishedAll() {
        System.out.println("Тесты выполнены");
    }

    @Test
    void testCheckBloodPressureMessageOutput() {
        String patientId = "1111";
        BloodPressure currentPressure = new BloodPressure(60, 120);
        String expected = "Warning, patient with id: null, need help";
        Mockito.when(PatientInfoFileRepositoryMock.getById(Mockito.anyString())).thenReturn(
                new PatientInfo("Семен", "Михайлов", LocalDate.of(1982, 1, 16),
                new HealthInfo(new BigDecimal("36.6"), new BloodPressure(125, 78))));
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        sut.checkBloodPressure(patientId, currentPressure);
        Mockito.verify(sendAlertServiceMock).send(argumentCaptor.capture());
        Assertions.assertEquals(expected, argumentCaptor.getValue());
    }

    @Test
    void testCheckBloodPressureNoMessageOutput() {
        String patientId = "1111";
        BloodPressure currentPressure = new BloodPressure(125, 78);
        Mockito.when(PatientInfoFileRepositoryMock.getById(Mockito.anyString())).thenReturn(
                new PatientInfo("Семен", "Михайлов", LocalDate.of(1982, 1, 16),
                        new HealthInfo(new BigDecimal("36.6"), new BloodPressure(125, 78))));
        sut.checkBloodPressure(patientId, currentPressure);
        Mockito.verify(sendAlertServiceMock, Mockito.never()).send(Mockito.anyString());
    }

    @Test
    void testCheckTemperatureMessageOutput() {
        String patientId = "1111";
        // Если у человека температура < 35.1 - ему помощь нужна, а если > 35.1 - то не нужна.
        // Пусть спокойно загибается когда она перевалит за 40. Нижнюю границу установили, а про верхнюю забыли.
        BigDecimal currentTemperature = new BigDecimal("35.0");
        String expected = "Warning, patient with id: null, need help";
        Mockito.when(PatientInfoFileRepositoryMock.getById(Mockito.anyString())).thenReturn(
                new PatientInfo("Семен", "Михайлов", LocalDate.of(1982, 1, 16),
                        new HealthInfo(new BigDecimal("36.6"), new BloodPressure(125, 78))));
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        sut.checkTemperature(patientId, currentTemperature);
        Mockito.verify(sendAlertServiceMock).send(argumentCaptor.capture());
        Assertions.assertEquals(expected, argumentCaptor.getValue());
    }

    @Test
    void testCheckTemperatureNoMessageOutput() {
        String patientId = "1111";
        BigDecimal currentTemperature = new BigDecimal("36.6");
        Mockito.when(PatientInfoFileRepositoryMock.getById(Mockito.anyString())).thenReturn(
                new PatientInfo("Семен", "Михайлов", LocalDate.of(1982, 1, 16),
                        new HealthInfo(new BigDecimal("36.6"), new BloodPressure(125, 78))));
        sut.checkTemperature(patientId, currentTemperature);
        Mockito.verify(sendAlertServiceMock, Mockito.never()).send(Mockito.anyString());
    }
}