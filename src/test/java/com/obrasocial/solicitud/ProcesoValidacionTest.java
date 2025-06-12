package com.obrasocial.solicitud;

import io.camunda.client.CamundaClient;
import io.camunda.client.api.response.ProcessInstanceEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ProcesoValidacionTest {

    @Autowired
    private CamundaClient camundaClient;

    private static final String PROCESS_ID = "validacion_datos_paciente_1";

    @Test
    void iniciarProcesoConDatosValidosYAptos_deberiaCrearInstancia() {
        Map<String, Object> variables = Map.of(
                "num_socio", "123456",
                "especialidad", "Cardiología",
                "motivo", "Chequeo general");

        ProcessInstanceEvent result = camundaClient
                .newCreateInstanceCommand()
                .bpmnProcessId(PROCESS_ID)
                .latestVersion()
                .variables(variables)
                .send()
                .join();

        assertThat(result).isNotNull();
        assertThat(result.getProcessInstanceKey()).isGreaterThan(0L);
    }
}
