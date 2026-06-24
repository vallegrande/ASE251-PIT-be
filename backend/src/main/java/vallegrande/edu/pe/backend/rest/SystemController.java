package vallegrande.edu.pe.backend.rest;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.List;
import java.time.OffsetDateTime;
import java.time.ZoneId;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import vallegrande.edu.pe.backend.model.AuditableEntity;
import vallegrande.edu.pe.backend.model.CrudEntity;
import vallegrande.edu.pe.backend.rest.dto.ConnectionStatusResponse;
import vallegrande.edu.pe.backend.rest.dto.ResourceSummaryResponse;
import vallegrande.edu.pe.backend.rest.dto.SystemInfoResponse;
import vallegrande.edu.pe.backend.rest.dto.SystemSummaryResponse;
import vallegrande.edu.pe.backend.service.AbstractCrudService;
import vallegrande.edu.pe.backend.service.CategoriaService;
import vallegrande.edu.pe.backend.service.CultivoService;
import vallegrande.edu.pe.backend.service.InsumoService;
import vallegrande.edu.pe.backend.service.ParcelaService;
import vallegrande.edu.pe.backend.service.ReporteService;
import vallegrande.edu.pe.backend.service.SeguimientoCultivoService;
import vallegrande.edu.pe.backend.service.UsuarioService;

@RestController
@RequestMapping("/api/v1/system")
@RequiredArgsConstructor
@Tag(name = "Sistema", description = "Verificación del estado del backend y la base de datos")
public class SystemController {

	private final JdbcTemplate jdbcTemplate;
	private final UsuarioService usuarioService;
	private final CategoriaService categoriaService;
	private final CultivoService cultivoService;
	private final ParcelaService parcelaService;
	private final InsumoService insumoService;
	private final SeguimientoCultivoService seguimientoService;
	private final ReporteService reporteService;

	@Value("${spring.application.name:backend}")
	private String applicationName;

	@Value("${app.version:0.0.1}")
	private String applicationVersion;

	@GetMapping("/info")
	@Operation(summary = "Obtiene información general del sistema")
	public SystemInfoResponse getSystemInfo() {
		ZoneId zoneId = ZoneId.systemDefault();
		return new SystemInfoResponse(
				applicationName,
				applicationVersion,
				zoneId.getId(),
				OffsetDateTime.now(zoneId).toString());
	}

	@GetMapping("/summary")
	@Operation(summary = "Obtiene conteos activos y eliminados por recurso")
	public SystemSummaryResponse getSystemSummary() {
		return new SystemSummaryResponse(List.of(
				summary("Usuarios", usuarioService),
				summary("Categorías", categoriaService),
				summary("Cultivos", cultivoService),
				summary("Parcelas", parcelaService),
				summary("Insumos", insumoService),
				summary("Seguimientos", seguimientoService),
				summary("Reportes", reporteService)));
	}

	@GetMapping("/db-connection")
	@Operation(summary = "Verifica la conexión con SQL Server")
	public ResponseEntity<ConnectionStatusResponse> checkDatabaseConnection() {
		try {
			return jdbcTemplate.execute((Connection connection) -> {
				DatabaseMetaData metaData = connection.getMetaData();
				String url = metaData.getURL();
				String databaseProduct = metaData.getDatabaseProductName();
				return ResponseEntity.ok(new ConnectionStatusResponse(
						"UP",
						"Conexión correcta con la base de datos",
						databaseProduct,
						url));
			});
		} catch (RuntimeException ex) {
			return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
					.body(new ConnectionStatusResponse(
							"DOWN",
							"No se pudo conectar con SQL Server",
							"SQL Server",
							null));
		}
	}

	private <T extends AuditableEntity & CrudEntity> ResourceSummaryResponse summary(
			String resource,
			AbstractCrudService<T> service) {
		return new ResourceSummaryResponse(
				resource,
				service.findAll().size(),
				service.findAllDeleted().size());
	}
}
