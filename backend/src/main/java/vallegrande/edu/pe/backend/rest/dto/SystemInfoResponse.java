package vallegrande.edu.pe.backend.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Información general del sistema")
public record SystemInfoResponse(
		@Schema(description = "Nombre de la aplicación") String applicationName,
		@Schema(description = "Versión de la aplicación") String version,
		@Schema(description = "Zona horaria del servidor") String zoneId,
		@Schema(description = "Fecha y hora actual del servidor") String serverTime) {
}
