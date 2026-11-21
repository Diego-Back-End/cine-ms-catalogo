package com.cine.ms_catalogo.services;

import com.cine.ms_catalogo.dto.TopicoRequest;
import com.cine.ms_catalogo.dto.TopicoResponse;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.config.ConfigResource;
import org.apache.kafka.common.config.TopicConfig;
import org.apache.kafka.common.errors.TopicExistsException;
import org.apache.kafka.common.errors.UnknownTopicOrPartitionException;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutionException;

@Service
public class TopicoService {

    private final KafkaAdmin kafkaAdmin;

    public TopicoService(KafkaAdmin kafkaAdmin) {
        this.kafkaAdmin = kafkaAdmin;
    }

    public List<TopicoResponse> listar() {
        try (AdminClient admin = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {
            Set<String> nombres = admin.listTopics().names().get();
            if (nombres.isEmpty()) {
                return List.of();
            }
            Map<String, TopicDescription> descripciones = admin.describeTopics(nombres).allTopicNames().get();
            List<TopicoResponse> resultado = new ArrayList<>();
            for (String nombre : nombres) {
                TopicDescription desc = descripciones.get(nombre);
                if (desc == null) continue;
                int particiones = desc.partitions().size();
                short replicationFactor = desc.partitions().isEmpty() ? 0 : (short) desc.partitions().get(0).replicas().size();
                resultado.add(new TopicoResponse(nombre, particiones, replicationFactor));
            }
            resultado.sort(Comparator.comparing(TopicoResponse::nombre));
            return resultado;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Error al listar tópicos", e);
        } catch (ExecutionException e) {
            throw new RuntimeException("Error al listar tópicos", e.getCause());
        }
    }

    public TopicoResponse crear(TopicoRequest request) {
        validarRequest(request, true);
        try (AdminClient admin = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {
            NewTopic topic = new NewTopic(request.nombre().trim(), request.particionesOrDefault(), request.replicationFactorOrDefault());
            admin.createTopics(List.of(topic)).all().get();
            return new TopicoResponse(request.nombre().trim(), request.particionesOrDefault(), request.replicationFactorOrDefault());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Error al crear tópico", e);
        } catch (ExecutionException e) {
            if (e.getCause() instanceof TopicExistsException) {
                throw new IllegalArgumentException("El tópico ya existe");
            }
            throw mapExecutionException(e, "Error al crear tópico");
        }
    }

    public TopicoResponse modificar(String nombre, TopicoRequest request) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del tópico es requerido");
        }
        try (AdminClient admin = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {
            TopicDescription desc = obtenerDescripcion(admin, nombre.trim());

            int actuales = desc.partitions().size();
            int solicitadas = request.particionesOrDefault();
            if (request.particiones() != null && solicitadas < actuales) {
                throw new IllegalArgumentException("Las particiones solo pueden aumentar");
            }
            if (request.particiones() != null && solicitadas > actuales) {
                admin.createPartitions(Map.of(nombre.trim(), NewPartitions.increaseTo(solicitadas))).all().get();
            }

            if (request.retencionMs() != null) {
                if (request.retencionMs() < 0) {
                    throw new IllegalArgumentException("retencionMs debe ser mayor o igual a 0");
                }
                ConfigResource resource = new ConfigResource(ConfigResource.Type.TOPIC, nombre.trim());
                ConfigEntry entry = new ConfigEntry(TopicConfig.RETENTION_MS_CONFIG, String.valueOf(request.retencionMs()));
                AlterConfigOp op = new AlterConfigOp(entry, AlterConfigOp.OpType.SET);
                admin.incrementalAlterConfigs(Map.of(resource, List.of(op))).all().get();
            }

            TopicDescription actualizado = obtenerDescripcion(admin, nombre.trim());
            int particiones = actualizado.partitions().size();
            short replicationFactor = actualizado.partitions().isEmpty() ? 0 : (short) actualizado.partitions().get(0).replicas().size();
            return new TopicoResponse(nombre.trim(), particiones, replicationFactor);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Error al modificar tópico", e);
        } catch (ExecutionException e) {
            throw mapExecutionException(e, "Error al modificar tópico");
        }
    }

    public void eliminar(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del tópico es requerido");
        }
        try (AdminClient admin = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {
            obtenerDescripcion(admin, nombre.trim());
            admin.deleteTopics(List.of(nombre.trim())).all().get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Error al eliminar tópico", e);
        } catch (ExecutionException e) {
            throw mapExecutionException(e, "Error al eliminar tópico");
        }
    }

    private TopicDescription obtenerDescripcion(AdminClient admin, String nombre) throws InterruptedException, ExecutionException {
        try {
            return admin.describeTopics(List.of(nombre)).allTopicNames().get().get(nombre);
        } catch (ExecutionException e) {
            if (e.getCause() instanceof UnknownTopicOrPartitionException) {
                throw new NoSuchElementException("Tópico no encontrado");
            }
            throw e;
        }
    }

    private void validarRequest(TopicoRequest request, boolean requiereNombre) {
        if (request == null) {
            throw new IllegalArgumentException("Request inválido");
        }
        if (requiereNombre && (request.nombre() == null || request.nombre().trim().isEmpty())) {
            throw new IllegalArgumentException("El nombre del tópico es requerido");
        }
        if (request.particiones() != null && request.particiones() < 1) {
            throw new IllegalArgumentException("particiones debe ser mayor o igual a 1");
        }
        if (request.replicationFactor() != null && request.replicationFactor() < 1) {
            throw new IllegalArgumentException("replicationFactor debe ser mayor o igual a 1");
        }
    }

    private RuntimeException mapExecutionException(ExecutionException e, String mensaje) {
        Throwable cause = e.getCause();
        if (cause instanceof UnknownTopicOrPartitionException) {
            return new NoSuchElementException("Tópico no encontrado");
        }
        if (cause instanceof IllegalArgumentException) {
            return (IllegalArgumentException) cause;
        }
        return new RuntimeException(mensaje, cause);
    }
}
