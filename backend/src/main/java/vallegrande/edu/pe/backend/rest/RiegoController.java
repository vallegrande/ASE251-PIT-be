package vallegrande.edu.pe.backend.rest;

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
import vallegrande.edu.pe.backend.model.Riego;
import vallegrande.edu.pe.backend.service.RiegoService;

@RestController
@RequestMapping("/api/riegos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RiegoController {
    
    private final RiegoService riegoService;
    
    @PostMapping
    public ResponseEntity<Riego> crearRiego(@RequestBody Riego riego) {
        return ResponseEntity.ok(riegoService.crearRiego(riego));
    }
    
    @GetMapping("/parcela/{parcelaId}")
    public ResponseEntity<List<Riego>> obtenerRiegosPorParcela(@PathVariable Long parcelaId) {
        return ResponseEntity.ok(riegoService.obtenerRiegosPorParcela(parcelaId));
    }
    
    @GetMapping("/fecha")
    public ResponseEntity<List<Riego>> obtenerRiegosPorFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        return ResponseEntity.ok(riegoService.obtenerRiegosPorFecha(inicio, fin));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Riego> obtenerRiegoPorId(@PathVariable Long id) {
        // Implementar si es necesario
        return ResponseEntity.ok(null);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Riego> actualizarRiego(@PathVariable Long id, @RequestBody Riego riego) {
        return ResponseEntity.ok(riegoService.actualizarRiego(id, riego));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarRiego(@PathVariable Long id) {
        riegoService.eliminarRiego(id);
        return ResponseEntity.noContent().build();
    }
}