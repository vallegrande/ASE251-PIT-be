package vallegrande.edu.pe.backend.rest;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import vallegrande.edu.pe.backend.model.Insumo;
import vallegrande.edu.pe.backend.service.AbstractCrudService;
import vallegrande.edu.pe.backend.service.InsumoService;

@RestController
@RequestMapping("/api/v1/insumos")
@Tag(name = "Insumos", description = "CRUD base de insumos")
@RequiredArgsConstructor
public class InsumoController extends AbstractCrudController<Insumo> {
	private final InsumoService service;

	@Override
	protected AbstractCrudService<Insumo> service() {
		return service;
	}

	@GetMapping("/eliminados")
	@Operation(summary = "Lista todos los insumos eliminados lógicamente")
	public List<Insumo> findAllDeleted() {
		return service.findAllDeleted();
	}
}
