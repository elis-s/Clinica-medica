package br.ucsal.clinica.controller;

import br.ucsal.clinica.model.BaseEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

/**
 * CRUD basico reutilizado pelos cadastros. NAO existe exclusao: registros so podem ser inativados.
 */
public abstract class CrudController<T extends BaseEntity> {

    protected final JpaRepository<T, Long> repo;

    protected CrudController(JpaRepository<T, Long> repo) {
        this.repo = repo;
    }

    // ganchos para regras de cada cadastro
    protected void validar(T obj) {}
    protected void antesDeCriar(T obj) {}
    protected void depoisDeCriar(T salvo) {}
    protected void antesDeEditar(T novo, T atual) {}
    protected void antesDeInativar(T obj, String motivo) {}

    @GetMapping
    public List<T> listar() {
        return repo.findAll(Sort.by("id"));
    }

    @GetMapping("/{id}")
    public T buscar(@PathVariable Long id) {
        return repo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Registro não encontrado"));
    }

    @PostMapping
    public T criar(@RequestBody T obj) {
        obj.setId(null);
        validar(obj);
        antesDeCriar(obj);
        T salvo = repo.save(obj);
        depoisDeCriar(salvo);
        return salvo;
    }

    @PutMapping("/{id}")
    public T editar(@PathVariable Long id, @RequestBody T obj) {
        T atual = buscar(id);
        obj.setId(id);
        obj.setDataCadastro(atual.getDataCadastro());
        validar(obj);
        antesDeEditar(obj, atual);
        return repo.save(obj);
    }

    @PatchMapping("/{id}/inativar")
    public T inativar(@PathVariable Long id, @RequestBody(required = false) Map<String, String> corpo) {
        T obj = buscar(id);
        String motivo = corpo == null ? null : corpo.get("motivo");
        antesDeInativar(obj, motivo);
        obj.inativar(motivo);
        return repo.save(obj);
    }
}
