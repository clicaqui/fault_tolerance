package com.exemplo.cep.controller;

import com.exemplo.cep.dto.CepResponse;
import com.exemplo.cep.service.CepService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/cep")
public class CepController {

    private final CepService cepService;

    public CepController(CepService cepService) {
        this.cepService = cepService;
    }

    @GetMapping("/{cep}")
    public CompletableFuture<ResponseEntity<CepResponse>> buscar(@PathVariable String cep) {
        return cepService.buscarCep(cep)
                .thenApply(ResponseEntity::ok);
    }
}
