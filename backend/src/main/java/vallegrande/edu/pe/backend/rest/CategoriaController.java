package vallegrande.edu.pe.backend.rest;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import vallegrande.edu.pe.backend.model.Categoria;
import vallegrande.edu.pe.backend.service.AbstractCrudService;
import vallegrande.edu.pe.backend.service.CategoriaService;

@RestController
@RequestMapping("/api/v1/categorias")
@Tag(name = "Categorias", description = "CRUD base de categorías")
@RequiredArgsConstructor
public class CategoriaController extends AbstractCrudController<Categoria> {
	private final CategoriaService service;

	@Override
	protected AbstractCrudService<Categoria> service() {
		return service;
	}

	@GetMapping("/eliminados")
	@Operation(summary = "Lista todas las categorías eliminadas lógicamente")
	public List<Categoria> findAllDeleted() {
		return service.findAllDeleted();
	}
}
