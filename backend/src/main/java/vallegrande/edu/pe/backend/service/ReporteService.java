package vallegrande.edu.pe.backend.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Service;

import vallegrande.edu.pe.backend.model.Reporte;
import vallegrande.edu.pe.backend.repository.ReporteRepository;

@Service
public class ReporteService extends AbstractCrudService<Reporte> {
	public ReporteService(ReporteRepository repository) {
		super(repository);
	}

	public byte[] exportPdf() throws IOException {
		List<Reporte> reportes = findAll();
		try (PDDocument document = new PDDocument()) {
			PDPage page = new PDPage(PDRectangle.LETTER);
			document.addPage(page);

			try (PDPageContentStream content = new PDPageContentStream(document, page)) {
				content.beginText();
				content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 18);
				content.newLineAtOffset(50, 720);
				content.showText("Reporte de registros");
				content.endText();

				content.beginText();
				content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
				content.newLineAtOffset(50, 690);

				if (reportes.isEmpty()) {
					content.showText("No hay reportes disponibles.");
				} else {
					for (int i = 0; i < reportes.size(); i++) {
						Reporte reporte = reportes.get(i);
						String line = String.format("%d. %s", i + 1, reporte.getNombreReporte());
						content.showText(line);
						content.newLineAtOffset(0, -18);
					}
				}

				content.endText();
			}

			ByteArrayOutputStream output = new ByteArrayOutputStream();
			document.save(output);
			return output.toByteArray();
		}
	}
}