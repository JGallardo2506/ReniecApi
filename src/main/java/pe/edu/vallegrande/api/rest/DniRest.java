package pe.edu.vallegrande.api.rest;

import org.springframework.web.bind.annotation.*;
import pe.edu.vallegrande.api.model.DniModel;
import pe.edu.vallegrande.api.service.DniService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/ip")
public class DniRest {
    private final DniService service;

    public DniRest(DniService service) {
        this.service = service;
    }

    @GetMapping("/status")
    public Flux<DniModel> getIpQueriesByStatus(@RequestParam String status) {
        return service.getByStatus(status);
    }

    @GetMapping("/all")
    public Flux<DniModel> getAllIpQueries() {
        return service.getAll();
    }

    @DeleteMapping("/delete/{id}")
    public Mono<String> deleteIp(@PathVariable Long id) {
        return service.deleteDni(id);
    }

    @PutMapping("/restore/{id}")
    public Mono<String> restoreIp(@PathVariable Long id) {
        return service.restoreDni(id);
    }

    @PostMapping("/consultar")
    public Mono<DniModel> consultarDni(@RequestParam String dni) {
        return service.consultarYGuardarDni(dni);
    }

    @PutMapping("/update/{id}")
    public Mono<String> updateDni(@PathVariable Long id, @RequestParam String dni) {
        return service.updateDni(id, dni);
    }

}
