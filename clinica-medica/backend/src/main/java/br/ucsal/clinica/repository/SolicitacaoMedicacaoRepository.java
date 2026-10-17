package br.ucsal.clinica.repository;

import br.ucsal.clinica.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface SolicitacaoMedicacaoRepository extends JpaRepository<SolicitacaoMedicacao, Long> {
    List<SolicitacaoMedicacao> findBySolicitanteIdOrderByIdDesc(Long colaboradorId);
}
