package com.obrasocial.solicitud.handler;

import io.camunda.client.api.response.ActivatedJob;
import io.camunda.client.api.worker.JobClient;
import io.camunda.spring.client.annotation.JobWorker;
import io.camunda.spring.client.annotation.Variable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ConfirmarTurnoHandler {

    private static final Logger logger = LoggerFactory.getLogger(ConfirmarTurnoHandler.class);

    @JobWorker(type = "RegistrarTurno")
    public void handleConfirmarTurno(final JobClient client,
            final ActivatedJob job,
            @Variable String num_socio,
            @Variable String fecha_turno) throws InterruptedException {
        try {
            logger.info("Registrando turno para num_socio={}, fecha={}", num_socio, fecha_turno);

            if ("111".equals(num_socio)) {
                client.newThrowErrorCommand(job.getKey())
                        .errorCode("turno_duplicado")
                        .errorMessage("El socio ya tiene un turno registrado")
                        .send()
                        .join();
                return;
            }

            if (fecha_turno == null || fecha_turno.isBlank()) {
                throw new RuntimeException("Fecha inválida");
            }

            Map<String, Object> output = Map.of("confirmacion", true);

            client.newCompleteCommand(job.getKey())
                    .variables(output)
                    .send()
                    .join();

            logger.info("Turno registrado con éxito para el socio {}", num_socio);

        } catch (Exception e) {
            logger.error("Error técnico al registrar el turno", e);
            client.newFailCommand(job.getKey())
                    .retries(job.getRetries() - 1)
                    .errorMessage("Error técnico: " + e.getMessage())
                    .send()
                    .join();
            throw new InterruptedException("Error técnico: " + e.getMessage());
        }
    }
}
