package dev.misei;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


//TODO: Skipping unit test, integration test, json test, component test, containerized test, arch test, load test and mutant test.
//TODO: Adding only real system test.
class AlertTermSystemTest {

    @Test
    public void givenAlerts_whenQueryTerms_thenHappyPath() {
        AlertService.getInstance().processBatchForMatchValidation();
    }

    @Test
    public void givenAlerts_whenQueryTerms_andKeepOrderFalse_thenHappyPath() {

    }

    @Test
    public void givenAlerts_whenQueryTerms_andAnotherLanguage_thenTranslate() {

    }


}