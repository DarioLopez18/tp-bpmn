package com.obrasocial.solicitud.rest;

import io.camunda.client.CamundaClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/proceso")
public class StartProcesoValidacionController {

  private static final Logger LOG = LoggerFactory.getLogger(StartProcesoValidacionController.class);
  private static final String PROCESS_ID = "validacion_datos_paciente_1"; 

  @Autowired
  private CamundaClient zeebe;

  @PostMapping("/start")
  public void startProcessInstance(@RequestBody Map<String, Object> variables) {

    LOG.info("Starting process `paymentProcess` with variables: " + variables);

    zeebe
        .newCreateInstanceCommand()
        .bpmnProcessId(PROCESS_ID)
        .latestVersion()
        .variables(variables)
        .send();
  }
}
