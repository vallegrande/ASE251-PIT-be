package vallegrande.edu.pe.backend.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import vallegrande.edu.pe.backend.model.Riego;

public interface RiegoRepository extends JpaRepository<Riego, Long> {
    
    List<Riego> findByParcelaId(Long parcelaId);
    
    List<Riego> findByFechaRiegoBetween(LocalDateTime inicio, LocalDateTime fin);
    
    @Query("SELECT r FROM Riego r WHERE r.parcela.id = :parcelaId AND r.fechaRiego BETWEEN :inicio AND :fin")
    List<Riego> findByParcelaAndFechaBetween(@Param("parcelaId") Long parcelaId,
                                             @Param("inicio") LocalDateTime inicio,
                                             @Param("fin") LocalDateTime fin);
    
    @Query("SELECT COUNT(r) FROM Riego r WHERE r.parcela.id = :parcelaId AND r.fechaRiego >= :fecha")
    Long countRiegosByParcelaDesde(@Param("parcelaId") Long parcelaId, @Param("fecha") LocalDateTime fecha);
}