package vallegrande.edu.pe.backend.repository;

import vallegrande.edu.pe.backend.model.Cosecha;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.math.BigDecimal;

public interface CosechaRepository extends JpaRepository<Cosecha, Long> {
    
    List<Cosecha> findByParcelaId(Long parcelaId);
    
    List<Cosecha> findByCultivoId(Long cultivoId);
    
    List<Cosecha> findByFechaCosechaBetween(LocalDateTime inicio, LocalDateTime fin);
    
    @Query("SELECT c FROM Cosecha c WHERE c.parcela.id = :parcelaId AND c.fechaCosecha BETWEEN :inicio AND :fin")
    List<Cosecha> findByParcelaAndFechaBetween(@Param("parcelaId") Long parcelaId,
                                               @Param("inicio") LocalDateTime inicio,
                                               @Param("fin") LocalDateTime fin);
    
    @Query("SELECT SUM(c.cantidad) FROM Cosecha c WHERE c.cultivo.id = :cultivoId AND c.fechaCosecha BETWEEN :inicio AND :fin")
    BigDecimal sumCantidadByCultivoAndFecha(@Param("cultivoId") Long cultivoId,
                                           @Param("inicio") LocalDateTime inicio,
                                           @Param("fin") LocalDateTime fin);
}