package br.ucsal.clinica.repository;

import br.ucsal.clinica.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface AuditoriaRepository extends JpaRepository<Auditoria, Long> {
    List<Auditoria> findTop500ByOrderByDataHoraDesc();
}
