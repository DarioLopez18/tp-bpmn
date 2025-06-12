package com.obrasocial.solicitud.handler;

import io.camunda.client.api.response.ActivatedJob;
import io.camunda.client.api.worker.JobClient;
import io.camunda.spring.client.annotation.JobWorker;
import io.camunda.spring.client.annotation.Variable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class NotificarSinTurnoHandler {

    private static final Logger logger = LoggerFactory.getLogger(NotificarSinTurnoHandler.class);

    @JobWorker(type = "NotificarNoDisponibilidad")
    public void handleNotificarSinTurno(final JobClient client,
            final ActivatedJob job,
            @Variable String num_socio) throws InterruptedException {
        try {
            logger.info("Notificando falta de turnos al socio {}", num_socio);

            if ("888".equals(num_socio)) {
                client.newThrowErrorCommand(job.getKey())
                        .errorCode("email_invalido")
                        .errorMessage("El email del socio es inválido")
                        .send()
                        .join();
                return;
            }

            // Simulación: todo sale bien
            client.newCompleteCommand(job.getKey()).send().join();
            logger.info("Notificación enviada con éxito al socio {}", num_socio);

        } catch (Exception e) {
            logger.error("Error técnico al notificar sin turno", e);
            client.newFailCommand(job.getKey())
                    .retries(job.getRetries() - 1)
                    .errorMessage("SMTP/API error: " + e.getMessage())
                    .send()
                    .join();
            throw new InterruptedException("Error técnico: " + e.getMessage());
        }
    }
}
