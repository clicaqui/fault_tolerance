package com.exemplo.cep.service;

import com.exemplo.cep.dto.CepResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;

@Service
public class CepService {

    private static final Logger log = LoggerFactory.getLogger(CepService.class);
    private static final String VIA_CEP_URL = "https://viacep.com.br/ws/{cep}/json/";

    private final RestTemplate restTemplate;

    public CepService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @CircuitBreaker(name = "cepService", fallbackMethod = "fallbackCep")
    @Retry(name = "cepService")
    @TimeLimiter(name = "cepService")
    public CompletableFuture<CepResponse> buscarCep(String cep) {
        log.info("Buscando CEP: {}", cep);

        // Remove hífen se existir
        String cepLimpo = cep.replaceAll("\\D", "");

        CepResponse response = restTemplate.getForObject(VIA_CEP_URL, CepResponse.class, cepLimpo);

        if (response == null || response.cep() == null) {
            throw new RuntimeException("CEP não encontrado: " + cep);
        }

        return CompletableFuture.completedFuture(response);
    }

    // Fallback executado quando o circuit breaker abre ou ocorre falha
    private CompletableFuture<CepResponse> fallbackCep(String cep, Throwable ex) {
        log.warn("Fallback acionado para CEP {}. Motivo: {}", cep, ex.getMessage());

        // Aqui você pode:
        // 1. Retornar dados em cache
        // 2. Retornar resposta padrão
        // 3. Chamar outra API alternativa
        // 4. Retornar um objeto indicando degradação

        return CompletableFuture.completedFuture(
                new CepResponse(
                        cep,
                        "Serviço temporariamente indisponível",
                        "",
                        "",
                        "Fallback",
                        "XX",
                        "",
                        "",
                        "",
                        ""
                )
        );
    }
}