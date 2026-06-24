package vallegrande.edu.pe.backend.rest.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resumen general de datos del sistema")
public record SystemSummaryResponse(
		@Schema(description = "Resumen por recurso") List<ResourceSummaryResponse> resources) {
}
