package br.ucsal.clinica.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@MappedSuperclass
@Getter
@Setter
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate dataCadastro;

    @PrePersist
    void antesDePersistir() {
        if (dataCadastro == null) dataCadastro = LocalDate.now();
    }

    /** Nunca excluimos fisicamente: cada entidade com historico sabe se inativar. */
    public void inativar(String motivo) {
        throw new UnsupportedOperationException("Este registro nao pode ser inativado");
    }
}
