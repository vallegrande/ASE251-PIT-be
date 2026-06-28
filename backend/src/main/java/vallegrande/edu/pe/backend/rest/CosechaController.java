package vallegrande.edu.pe.backend.rest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import vallegrande.edu.pe.backend.model.Cosecha;
import vallegrande.edu.pe.backend.service.CosechaService;

@RestController
@RequestMapping("/api/cosechas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CosechaController {
    
    private final CosechaService cosechaService;
    
    @PostMapping
    public ResponseEntity<Cosecha> crearCosecha(@RequestBody Cosecha cosecha) {
        return ResponseEntity.ok(cosechaService.crearCosecha(cosecha));
    }
    
    @GetMapping("/parcela/{parcelaId}")
    public ResponseEntity<List<Cosecha>> obtenerCosechasPorParcela(@PathVariable Long parcelaId) {
        return ResponseEntity.ok(cosechaService.obtenerCosechasPorParcela(parcelaId));
    }
    
    @GetMapping("/cultivo/{cultivoId}")
    public ResponseEntity<List<Cosecha>> obtenerCosechasPorCultivo(@PathVariable Long cultivoId) {
        return ResponseEntity.ok(cosechaService.obtenerCosechasPorCultivo(cultivoId));
    }
    
    @GetMapping("/produccion/cultivo/{cultivoId}")
    public ResponseEntity<BigDecimal> obtenerProduccionTotal(
            @PathVariable Long cultivoId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        return ResponseEntity.ok(cosechaService.obtenerProduccionTotalPorCultivo(cultivoId, inicio, fin));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Cosecha> actualizarCosecha(@PathVariable Long id, @RequestBody Cosecha cosecha) {
        return ResponseEntity.ok(cosechaService.actualizarCosecha(id, cosecha));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCosecha(@PathVariable Long id) {
        cosechaService.eliminarCosecha(id);
        return ResponseEntity.noContent().build();
    }
}