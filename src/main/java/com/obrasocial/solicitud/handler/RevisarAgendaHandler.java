package com.obrasocial.solicitud.handler;

import io.camunda.client.api.response.ActivatedJob;
import io.camunda.client.api.worker.JobClient;
import io.camunda.spring.client.annotation.JobWorker;
import io.camunda.spring.client.annotation.Variable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class RevisarAgendaHandler {

    private static final Logger logger = LoggerFactory.getLogger(RevisarAgendaHandler.class);

    @JobWorker(type = "ConsultarDisponibilidadTurnos")
    public void handleRevisarAgenda(final JobClient client,
            final ActivatedJob job,
            @Variable String especialidad,
            @Variable String motivo) throws InterruptedException {
        try {
            logger.info("Consultando disponibilidad para especialidad={}, motivo={}", especialidad, motivo);

            boolean turnoDisponible = true;
            String fechaTurno = "2025-06-20";

            // Simulación: si es Traumatología no hay turno
            if ("Traumatología".equalsIgnoreCase(especialidad)) {
                turnoDisponible = false;
            }

            if (!turnoDisponible) {
                client.newThrowErrorCommand(job.getKey())
                        .errorCode("sin_turno")
                        .errorMessage("No hay turnos disponibles para la especialidad")
                        .send()
                        .join();
                return;
            }

            Map<String, Object> variables = new HashMap<>();
            variables.put("turnoDisponible", true);
            variables.put("fecha_turno", fechaTurno);

            client.newCompleteCommand(job.getKey())
                    .variables(variables)
                    .send()
                    .join();

            logger.info("Turno disponible para la fecha: {}", fechaTurno);

        } catch (Exception e) {
            logger.error("Error técnico al consultar turnos", e);
            client.newFailCommand(job.getKey())
                    .retries(job.getRetries() - 1)
                    .errorMessage("Error técnico: " + e.getMessage())
                    .send()
                    .join();
            throw new InterruptedException("Error técnico: " + e.getMessage());
        }
    }
}
