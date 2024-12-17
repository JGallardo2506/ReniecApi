package pe.edu.vallegrande.api.service;

import lombok.Getter;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.api.model.DniModel;
import pe.edu.vallegrande.api.repository.DniRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class DniService {
    private final DniRepository repository;
    private final OkHttpClient client = new OkHttpClient();

    @Getter
    private final String token;

    public DniService(DniRepository repository, @Value("${spring.contentmoderator.token}") String token) {
        this.repository = repository;
        this.token = token;
    }

    public Flux<DniModel> getByStatus(String status) {
        return repository.findByStatus(status);
    }

    public Flux<DniModel> getAll() {
        return repository.findAll();
    }

    public Mono<String> deleteDni(Long id) {
        return repository.findById(id)
                .flatMap(existingIp -> {
                    existingIp.setStatus("I"); // Cambiar estado a "Inactivo"
                    return repository.save(existingIp)
                            .then(Mono.just("DNI eliminada lógicamente con éxito: " + existingIp.getDni()));
                })
                .switchIfEmpty(Mono.just("DNI no encontrada."));
    }

    public Mono<String> restoreDni(Long id) {
        return repository.findById(id)
                .flatMap(existingIp -> {
                    existingIp.setStatus("A"); // Cambiar estado a "Activo"
                    return repository.save(existingIp)
                            .then(Mono.just("DNI restaurada con éxito: " + existingIp.getDni()));
                })
                .switchIfEmpty(Mono.just("DNI no encontrada."));
    }
    public Mono<DniModel> consultarYGuardarDni(String dni) {
        String url = "https://dniruc.apisperu.com/api/v1/dni/" + dni + "?token=" + token;

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        return Mono.fromCallable(() -> {
            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    JSONObject json = new JSONObject(responseBody);

                    if (json.getBoolean("success")) {
                        DniModel dniModel = new DniModel();
                        dniModel.setDni(json.getString("dni"));
                        dniModel.setNombres(json.getString("nombres"));
                        dniModel.setApellidoPaterno(json.getString("apellidoPaterno"));
                        dniModel.setApellidoMaterno(json.getString("apellidoMaterno"));
                        dniModel.setCodVerifica(json.getString("codVerifica"));
                        dniModel.setStatus("A"); // Establecer el estado como Activo

                        // Guardar en la base de datos y devolver el resultado
                        return repository.save(dniModel); // No usar block()
                    } else {
                        throw new RuntimeException("No se pudo obtener información del DNI.");
                    }
                } else {
                    throw new RuntimeException("Error en la consulta del DNI.");
                }
            }
        }).flatMap(dniModel -> dniModel); // Encadenar el resultado
    }
    public Mono<String> updateDni(Long id, String dni) {
        return repository.findById(id)
                .flatMap(existingDni -> {
                    String url = "https://dniruc.apisperu.com/api/v1/dni/" + dni + "?token=" + token;

                    Request request = new Request.Builder()
                            .url(url)
                            .get()
                            .build();

                    return Mono.fromCallable(() -> {
                        try (Response response = client.newCall(request).execute()) {
                            if (response.isSuccessful() && response.body() != null) {
                                String responseBody = response.body().string();
                                JSONObject json = new JSONObject(responseBody);

                                if (json.getBoolean("success")) {
                                    // Actualizar los campos solo si el DNI es válido
                                    existingDni.setDni(json.getString("dni"));
                                    existingDni.setNombres(json.getString("nombres"));
                                    existingDni.setApellidoPaterno(json.getString("apellidoPaterno"));
                                    existingDni.setApellidoMaterno(json.getString("apellidoMaterno"));
                                    existingDni.setCodVerifica(json.getString("codVerifica"));

                                    return repository.save(existingDni)
                                            .then(Mono.just("DNI actualizado con éxito: " + existingDni.getDni()));
                                } else {
                                    // Si el DNI no es válido, devolver un mensaje sin lanzar excepción
                                    return Mono.just("DNI no válido.");
                                }
                            } else {
                                // Manejar error en la consulta
                                return Mono.just("Error en la consulta del DNI.");
                            }
                        }
                    }).flatMap(result -> result); // Encadenar el resultado
                })
                .switchIfEmpty(Mono.just("DNI no encontrado.")); // Esto está correcto
    }
}
