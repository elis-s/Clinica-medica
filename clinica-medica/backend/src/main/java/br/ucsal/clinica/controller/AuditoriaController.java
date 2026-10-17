package br.ucsal.clinica.controller;

import br.ucsal.clinica.model.Auditoria;
import br.ucsal.clinica.repository.AuditoriaRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/auditoria")
public class AuditoriaController {

    private final AuditoriaRepository repo;

    public AuditoriaController(AuditoriaRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<Auditoria> listar() {
        return repo.findTop500ByOrderByDataHoraDesc();
    }
}
