package vallegrande.edu.pe.backend.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Service;

import vallegrande.edu.pe.backend.model.Cultivo;
import vallegrande.edu.pe.backend.model.Insumo;
import vallegrande.edu.pe.backend.model.Parcela;
import vallegrande.edu.pe.backend.model.Reporte;
import vallegrande.edu.pe.backend.model.SeguimientoCultivo;
import vallegrande.edu.pe.backend.model.Usuario;
import vallegrande.edu.pe.backend.repository.CultivoRepository;
import vallegrande.edu.pe.backend.repository.InsumoRepository;
import vallegrande.edu.pe.backend.repository.ParcelaRepository;
import vallegrande.edu.pe.backend.repository.ReporteRepository;
import vallegrande.edu.pe.backend.repository.SeguimientoCultivoRepository;
import vallegrande.edu.pe.backend.repository.UsuarioRepository;

@Service
public class ReporteService extends AbstractCrudService<Reporte> {

	// ── Colores (RGB 0–1) ─────────────────────────────────────────
	private static final float[] C_BG_PAGE    = { 0.05f, 0.07f, 0.05f };
	private static final float[] C_GREEN      = { 0.13f, 0.77f, 0.37f };
	private static final float[] C_GREEN_DIM  = { 0.09f, 0.55f, 0.26f };
	private static final float[] C_TEXT_MAIN  = { 0.94f, 0.98f, 0.95f };
	private static final float[] C_TEXT_MUTED = { 0.48f, 0.60f, 0.51f };
	private static final float[] C_CARD_BG    = { 0.07f, 0.10f, 0.07f };
	private static final float[] C_CARD_ALT   = { 0.09f, 0.12f, 0.09f };
	private static final float[] C_LABEL      = { 0.40f, 0.55f, 0.42f };
	private static final float[] C_AMBER      = { 0.98f, 0.75f, 0.14f };
	private static final float[] C_RED        = { 0.95f, 0.43f, 0.43f };

	private static final float PAGE_W   = PDRectangle.LETTER.getWidth();
	private static final float PAGE_H   = PDRectangle.LETTER.getHeight();
	private static final float MARGIN   = 44f;
	private static final float CARD_W   = PAGE_W - MARGIN * 2;

	private static final DateTimeFormatter FMT_FULL  = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
	private static final DateTimeFormatter FMT_DATE  = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	private final ReporteRepository           reporteRepo;
	private final ParcelaRepository           parcelaRepo;
	private final CultivoRepository           cultivoRepo;
	private final UsuarioRepository           usuarioRepo;
	private final InsumoRepository            insumoRepo;
	private final SeguimientoCultivoRepository seguimientoRepo;

	public ReporteService(ReporteRepository reporteRepo,
	                      ParcelaRepository parcelaRepo,
	                      CultivoRepository cultivoRepo,
	                      UsuarioRepository usuarioRepo,
	                      InsumoRepository insumoRepo,
	                      SeguimientoCultivoRepository seguimientoRepo) {
		super(reporteRepo);
		this.reporteRepo     = reporteRepo;
		this.parcelaRepo     = parcelaRepo;
		this.cultivoRepo     = cultivoRepo;
		this.usuarioRepo     = usuarioRepo;
		this.insumoRepo      = insumoRepo;
		this.seguimientoRepo = seguimientoRepo;
	}

	public List<Reporte> findAllDeleted() {
		return reporteRepo.findAllDeleted();
	}

	// ══════════════════════════════════════════════════════════════
	//  PDF EXPORT
	// ══════════════════════════════════════════════════════════════
	public byte[] exportPdf() throws IOException {

		List<Reporte> reportes = findAll();

		PDType1Font fBold    = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
		PDType1Font fNormal  = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
		PDType1Font fItalic  = new PDType1Font(Standard14Fonts.FontName.HELVETICA_OBLIQUE);

		try (PDDocument doc = new PDDocument()) {

			// ── Estado del cursor entre páginas ──────────────────────
			PDPage   currentPage = newPage(doc);
			PDPageContentStream cs = openStream(doc, currentPage);
			float y = PAGE_H - MARGIN;

			// ── Portada / encabezado global ───────────────────────────
			y = drawPageHeader(cs, fBold, fNormal, y, reportes.size());

			// ── Separador ────────────────────────────────────────────
			y -= 10;
			drawHLine(cs, y, C_GREEN, 1f);
			y -= 18;

			// ── Sección etiqueta ─────────────────────────────────────
			drawText(cs, fBold, 8, MARGIN, y, "DETALLE DE REPORTES", C_GREEN);
			y -= 14;

			// ── Tarjeta por cada reporte ──────────────────────────────
			for (int idx = 0; idx < reportes.size(); idx++) {
				Reporte r = reportes.get(idx);

				// Resolver entidades relacionadas
				Parcela           parcela    = r.getIdParcela()           != null ? parcelaRepo.findById(r.getIdParcela()).orElse(null)             : null;
				Cultivo           cultivo    = parcela != null && parcela.getIdCultivo()    != null ? cultivoRepo.findById(parcela.getIdCultivo()).orElse(null)       : null;
				Usuario           usuario    = parcela != null && parcela.getIdUsuario()    != null ? usuarioRepo.findById(parcela.getIdUsuario()).orElse(null)       : null;
				Insumo            insumo     = parcela != null && parcela.getIdInsumo()     != null ? insumoRepo.findById(parcela.getIdInsumo()).orElse(null)         : null;
				SeguimientoCultivo seg       = parcela != null && parcela.getIdSeguimientoActual() != null ? seguimientoRepo.findById(parcela.getIdSeguimientoActual()).orElse(null) : null;

				// Calcular altura de la tarjeta (filas fijas)
				float cardH = 162f;

				// ¿Cabe en esta página?
				if (y - cardH < MARGIN + 30) {
					drawPageFooter(cs, fNormal, doc.getNumberOfPages());
					cs.close();
					currentPage = newPage(doc);
					cs = openStream(doc, currentPage);
					y = PAGE_H - MARGIN;
					drawContinuationHeader(cs, fBold, fNormal, y);
					y -= 48;
					drawHLine(cs, y, C_GREEN, 0.6f);
					y -= 14;
				}

				// Fondo de la tarjeta (alterno)
				float[] cardBg = (idx % 2 == 0) ? C_CARD_BG : C_CARD_ALT;
				fillRect(cs, MARGIN, y - cardH + 8, CARD_W, cardH, cardBg);

				// Barra verde izquierda de la tarjeta
				fillRect(cs, MARGIN, y - cardH + 8, 4, cardH, C_GREEN);

				// Número de reporte (badge)
				float badgeX = MARGIN + 14;
				fillRect(cs, badgeX, y - 14, 22, 18, C_GREEN);
				drawText(cs, fBold, 9, badgeX + 4, y - 10, String.valueOf(idx + 1), C_BG_PAGE);

				// Nombre del reporte
				drawText(cs, fBold,   13, badgeX + 28, y - 6,  safe(r.getNombreReporte()), C_TEXT_MAIN);

				// Fecha creación del reporte
				String fechaRep = r.getCreatedAt() != null ? r.getCreatedAt().format(FMT_DATE) : "-";
				String fechaLabel = "Creado: " + fechaRep;
				float fw = fNormal.getStringWidth(fechaLabel) / 1000f * 8.5f;
				drawText(cs, fNormal, 8.5f, PAGE_W - MARGIN - fw - 8, y - 6, fechaLabel, C_TEXT_MUTED);

				// Estado badge
				boolean activo = Boolean.TRUE.equals(r.getEstado());
				float[] estadoColor = activo ? C_GREEN : C_RED;
				String estadoTxt    = activo ? "ACTIVO" : "INACTIVO";
				float estadoX = PAGE_W - MARGIN - 70;
				fillRect(cs, estadoX, y - 22, 62, 14, estadoColor);
				drawText(cs, fBold, 7.5f, estadoX + 6, y - 17, estadoTxt, C_BG_PAGE);

				y -= 26;

				// Línea fina bajo encabezado de tarjeta
				drawHLine(cs, y, C_GREEN_DIM, 0.4f);
				y -= 14;

				// ── Datos de la parcela en dos columnas ──────────────
				float col1 = MARGIN + 14;
				float col2 = MARGIN + CARD_W / 2 + 6;
				float rowH = 16f;

				// Fila 1
				drawLabelValue(cs, fBold, fNormal, col1, y, "Parcela:",       safe(parcela != null ? parcela.getNombre() : "—"));
				drawLabelValue(cs, fBold, fNormal, col2, y, "Dirección:",     safe(parcela != null ? parcela.getDireccionGeografica() : "—"));
				y -= rowH;

				// Fila 2
				String disp = parcela != null ? (Boolean.TRUE.equals(parcela.getDisponibilidad()) ? "Disponible" : "No disponible") : "—";
				String area = parcela != null && parcela.getArea() != null ? String.format("%.2f ha", parcela.getArea()) : "—";
				drawLabelValue(cs, fBold, fNormal, col1, y, "Disponibilidad:", disp);
				drawLabelValue(cs, fBold, fNormal, col2, y, "Área:",           area);
				y -= rowH;

				// Fila 3
				drawLabelValue(cs, fBold, fNormal, col1, y, "Cultivo:",       safe(cultivo  != null ? cultivo.getNombre()  : "—"));
				drawLabelValue(cs, fBold, fNormal, col2, y, "Usuario:",       safe(usuario  != null ? usuario.getNombre() + " " + usuario.getApellido() : "—"));
				y -= rowH;

				// Fila 4
				drawLabelValue(cs, fBold, fNormal, col1, y, "Insumo:",        safe(insumo   != null ? insumo.getNombre()   : "—"));
				drawLabelValue(cs, fBold, fNormal, col2, y, "Seguim. actual:", safe(seg     != null ? seg.getNombre()      : "—"));
				y -= rowH;

				// Fila 5 — observaciones (span completo)
				String obs = parcela != null && parcela.getObservaciones() != null ? parcela.getObservaciones() : "Sin observaciones";
				drawLabelValue(cs, fBold, fItalic, col1, y, "Observaciones:", truncate(obs, 90));
				y -= rowH;

				// Espacio entre tarjetas
				y -= 14;
			}

			// ── Pie de última página ──────────────────────────────────
			drawPageFooter(cs, fNormal, doc.getNumberOfPages());
			cs.close();

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			doc.save(out);
			return out.toByteArray();
		}
	}

	// ══════════════════════════════════════════════════════════════
	//  HELPERS DE DIBUJO
	// ══════════════════════════════════════════════════════════════

	private float drawPageHeader(PDPageContentStream cs, PDType1Font fBold, PDType1Font fNormal,
	                              float y, int total) throws IOException {
		// Fondo header
		fillRect(cs, 0, y - 52, PAGE_W, 62, C_BG_PAGE);
		fillRect(cs, 0, y - 52, 6,      62, C_GREEN);

		drawText(cs, fBold,   20, MARGIN + 12, y - 10, "Sistema ASE251 PIT",                         C_TEXT_MAIN);
		drawText(cs, fNormal,  9, MARGIN + 12, y - 25, "Reporte completo de seguimiento de producción por parcela", C_TEXT_MUTED);

		String gen = "Generado: " + LocalDateTime.now().format(FMT_FULL);
		drawText(cs, fNormal,  8, PAGE_W - MARGIN - 140, y - 25, gen, C_TEXT_MUTED);
		drawText(cs, fBold,    9, PAGE_W - MARGIN - 140, y - 10, "Total registros: " + total, C_GREEN);

		return y - 52;
	}

	private void drawContinuationHeader(PDPageContentStream cs, PDType1Font fBold,
	                                     PDType1Font fNormal, float y) throws IOException {
		fillRect(cs, 0, y - 32, PAGE_W, 38, C_BG_PAGE);
		fillRect(cs, 0, y - 32, 6,      38, C_GREEN);
		drawText(cs, fBold,   13, MARGIN + 12, y - 8,  "Sistema ASE251 PIT",          C_TEXT_MAIN);
		drawText(cs, fNormal,  8, MARGIN + 12, y - 22, "Continuación del reporte…",   C_TEXT_MUTED);
	}

	private void drawPageFooter(PDPageContentStream cs, PDType1Font fNormal,
	                             int pageNum) throws IOException {
		drawHLine(cs, MARGIN - 10, C_GREEN_DIM, 0.4f);
		drawText(cs, fNormal, 7.5f, MARGIN, MARGIN - 22,
			"Documento generado automáticamente por ASE251 PIT  •  Página " + pageNum,
			C_TEXT_MUTED);
	}

	private void drawLabelValue(PDPageContentStream cs,
	                             PDType1Font fLabel, PDType1Font fValue,
	                             float x, float y,
	                             String label, String value) throws IOException {
		drawText(cs, fLabel, 8f,  x,  y, label, C_LABEL);
		float lw = fLabel.getStringWidth(label) / 1000f * 8f;
		drawText(cs, fValue, 8.5f, x + lw + 4, y, value, C_TEXT_MAIN);
	}

	private void drawText(PDPageContentStream cs, PDType1Font font, float size,
	                       float x, float y, String text, float[] rgb) throws IOException {
		if (text == null || text.isBlank()) return;
		cs.beginText();
		cs.setNonStrokingColor(rgb[0], rgb[1], rgb[2]);
		cs.setFont(font, size);
		cs.newLineAtOffset(x, y);
		cs.showText(text);
		cs.endText();
	}

	private void fillRect(PDPageContentStream cs, float x, float y,
	                       float w, float h, float[] rgb) throws IOException {
		cs.setNonStrokingColor(rgb[0], rgb[1], rgb[2]);
		cs.addRect(x, y, w, h);
		cs.fill();
	}

	private void drawHLine(PDPageContentStream cs, float y,
	                        float[] rgb, float lw) throws IOException {
		cs.setStrokingColor(rgb[0], rgb[1], rgb[2]);
		cs.setLineWidth(lw);
		cs.moveTo(MARGIN, y);
		cs.lineTo(PAGE_W - MARGIN, y);
		cs.stroke();
	}

	private PDPage newPage(PDDocument doc) {
		PDPage p = new PDPage(PDRectangle.LETTER);
		doc.addPage(p);
		return p;
	}

	private PDPageContentStream openStream(PDDocument doc, PDPage page) throws IOException {
		// Fondo de página
		PDPageContentStream cs = new PDPageContentStream(doc, page, AppendMode.APPEND, true);
		fillRect(cs, 0, 0, PAGE_W, PAGE_H, C_BG_PAGE);
		return cs;
	}

	private String safe(String s)             { return s != null ? s : "—"; }
	private String truncate(String s, int max) {
		if (s == null) return "—";
		return s.length() <= max ? s : s.substring(0, max - 1) + "…";
	}
}
