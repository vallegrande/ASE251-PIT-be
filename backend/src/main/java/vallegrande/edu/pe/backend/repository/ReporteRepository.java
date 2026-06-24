package vallegrande.edu.pe.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import vallegrande.edu.pe.backend.model.Reporte;

public interface ReporteRepository extends JpaRepository<Reporte, Long> {

	@Query("SELECT r FROM Reporte r WHERE r.estado = false OR r.deletedAt IS NOT NULL")
	List<Reporte> findAllDeleted();
}
