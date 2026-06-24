package vallegrande.edu.pe.backend.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import vallegrande.edu.pe.backend.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
	// Comentario explicativo: Busca un usuario activo o inactivo por su correo electrónico
	Optional<Usuario> findByEmail(String email);

	// Comentario explicativo: Busca un usuario activo o inactivo por su número de documento
	Optional<Usuario> findByNumeroDocumento(String numeroDocumento);
}