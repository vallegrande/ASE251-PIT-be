package vallegrande.edu.pe.backend.service;

import org.springframework.stereotype.Service;

import vallegrande.edu.pe.backend.model.Usuario;
import vallegrande.edu.pe.backend.repository.UsuarioRepository;

@Service
public class UsuarioService extends AbstractCrudService<Usuario> {
	private final UsuarioRepository usuarioRepository;

	public UsuarioService(UsuarioRepository repository) {
		super(repository);
		this.usuarioRepository = repository;
	}

	@Override
	public Usuario create(Usuario entity) {
		// Comentario explicativo: Valida si el correo ya existe en el sistema antes de registrar
		usuarioRepository.findByEmail(entity.getEmail())
				.ifPresent(u -> {
					throw new IllegalArgumentException("El correo electrónico ya se encuentra registrado por otro usuario.");
				});

		// Comentario explicativo: Valida si el número de documento ya existe en el sistema antes de registrar
		usuarioRepository.findByNumeroDocumento(entity.getNumeroDocumento())
				.ifPresent(u -> {
					throw new IllegalArgumentException("El número de documento ya se encuentra registrado por otro usuario.");
				});

		return super.create(entity);
	}

	@Override
	public Usuario update(Long id, Usuario incoming) {
		// Comentario explicativo: Valida si el correo pertenece a otro usuario antes de actualizar
		usuarioRepository.findByEmail(incoming.getEmail())
				.ifPresent(u -> {
					if (!u.getId().equals(id)) {
						throw new IllegalArgumentException("El correo electrónico ya se encuentra registrado por otro usuario.");
					}
				});

		// Comentario explicativo: Valida si el número de documento pertenece a otro usuario antes de actualizar
		usuarioRepository.findByNumeroDocumento(incoming.getNumeroDocumento())
				.ifPresent(u -> {
					if (!u.getId().equals(id)) {
						throw new IllegalArgumentException("El número de documento ya se encuentra registrado por otro usuario.");
					}
				});

		return super.update(id, incoming);
	}

	// Comentario explicativo: Servicio de búsqueda que invoca al repositorio para filtrar usuarios activos por el término dado
	public java.util.List<Usuario> search(String query) {
		if (query == null || query.trim().isEmpty()) {
			return findAll();
		}
		return usuarioRepository.searchActiveUsers(query.trim());
	}

	// Comentario explicativo: Servicio para autenticar un usuario por correo y contraseña
	public Usuario login(String email, String password) {
		return usuarioRepository.findByEmailAndPassword(email, password)
				.orElseThrow(() -> new IllegalArgumentException("Credenciales inválidas"));
	}
}