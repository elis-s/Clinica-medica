package br.ucsal.clinica.controller;

import br.ucsal.clinica.model.Prontuario;
import br.ucsal.clinica.repository.ProntuarioRepository;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/** Somente consulta: o prontuario e criado junto com o paciente. */
@RestController
@RequestMapping("/api/prontuarios")
public class ProntuarioController {

    private final ProntuarioRepository repo;

    public ProntuarioController(ProntuarioRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<Prontuario> listar() {
        return repo.findAll(Sort.by("id"));
    }

    @GetMapping("/{id}")
    public Prontuario buscar(@PathVariable Long id) {
        return repo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Prontuário não encontrado"));
    }
}
