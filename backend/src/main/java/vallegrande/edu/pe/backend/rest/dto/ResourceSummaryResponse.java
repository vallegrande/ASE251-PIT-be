package vallegrande.edu.pe.backend.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resumen de registros por recurso")
public record ResourceSummaryResponse(
		@Schema(description = "Nombre del recurso") String resource,
		@Schema(description = "Cantidad de registros activos") int active,
		@Schema(description = "Cantidad de registros eliminados") int deleted) {
}
