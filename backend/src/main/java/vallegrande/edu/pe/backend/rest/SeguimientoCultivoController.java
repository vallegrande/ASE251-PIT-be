package vallegrande.edu.pe.backend.rest;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import vallegrande.edu.pe.backend.model.SeguimientoCultivo;
import vallegrande.edu.pe.backend.service.AbstractCrudService;
import vallegrande.edu.pe.backend.service.SeguimientoCultivoService;

@RestController
@RequestMapping("/api/v1/seguimientos")
@Tag(name = "Seguimiento Cultivo", description = "CRUD base de seguimiento del cultivo")
@RequiredArgsConstructor
public class SeguimientoCultivoController extends AbstractCrudController<SeguimientoCultivo> {
	private final SeguimientoCultivoService service;

	@Override
	protected AbstractCrudService<SeguimientoCultivo> service() {
		return service;
	}

	@GetMapping("/eliminados")
	@Operation(summary = "Lista todos los seguimientos eliminados lógicamente")
	public List<SeguimientoCultivo> findAllDeleted() {
		return service.findAllDeleted();
	}
}
