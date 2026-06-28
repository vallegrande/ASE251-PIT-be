package vallegrande.edu.pe.backend.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import vallegrande.edu.pe.backend.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
	// Comentario explicativo: Busca un usuario activo o inactivo por su correo electrónico
	Optional<Usuario> findByEmail(String email);

	// Comentario explicativo: Busca un usuario por su correo y contraseña
	Optional<Usuario> findByEmailAndPassword(String email, String password);

	// Comentario explicativo: Busca un usuario activo o inactivo por su número de documento
	Optional<Usuario> findByNumeroDocumento(String numeroDocumento);

	// Comentario explicativo: Realiza una búsqueda dinámica de usuarios activos por coincidencia parcial en nombre, apellido o número de documento
	@Query("SELECT u FROM Usuario u WHERE u.estado = true AND u.deletedAt IS NULL AND " +
	       "(LOWER(u.nombre) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
	       "LOWER(u.apellido) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
	       "LOWER(u.numeroDocumento) LIKE LOWER(CONCAT('%', :query, '%')))")
	List<Usuario> searchActiveUsers(@Param("query") String query);
}