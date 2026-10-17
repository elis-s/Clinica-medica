package br.ucsal.clinica.repository;

import br.ucsal.clinica.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface MovimentacaoEstoqueRepository extends JpaRepository<MovimentacaoEstoque, Long> {
    List<MovimentacaoEstoque> findByMedicacaoIdOrderByDataHoraDesc(Long medicacaoId);
}
