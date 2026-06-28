package vallegrande.edu.pe.backend.rest;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import vallegrande.edu.pe.backend.model.Usuario;
import vallegrande.edu.pe.backend.service.AbstractCrudService;
import vallegrande.edu.pe.backend.service.UsuarioService;

@RestController
@RequestMapping("/api/v1/usuarios")
@Tag(name = "Usuarios", description = "CRUD base de usuarios")
@RequiredArgsConstructor
public class UsuarioController extends AbstractCrudController<Usuario> {
	private final UsuarioService service;

	@Override
	protected AbstractCrudService<Usuario> service() {
		return service;
	}

	@GetMapping("/eliminados")
	@Operation(summary = "Lista todos los usuarios eliminados lógicamente")
	public List<Usuario> findAllDeleted() {
		return service.findAllDeleted();
	}

	@GetMapping("/buscar")
	@Operation(summary = "Busca usuarios activos por coincidencia en nombre, apellido o documento")
	public List<Usuario> search(@org.springframework.web.bind.annotation.RequestParam(required = false) String query) {
		// Comentario explicativo: Expone el endpoint de búsqueda llamando a service.search con el parámetro query recibido
		return service.search(query);
	}

	@org.springframework.web.bind.annotation.PostMapping("/login")
	@Operation(summary = "Autentica un usuario por email y contraseña")
	public Usuario login(@org.springframework.web.bind.annotation.RequestBody vallegrande.edu.pe.backend.rest.dto.LoginRequest dto) {
		return service.login(dto.getEmail(), dto.getPassword());
	}
}
