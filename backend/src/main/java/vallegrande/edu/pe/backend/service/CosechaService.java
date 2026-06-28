package vallegrande.edu.pe.backend.service;

import vallegrande.edu.pe.backend.model.Cosecha;
import vallegrande.edu.pe.backend.repository.CosechaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CosechaService {
    
    private final CosechaRepository cosechaRepository;
    
    @Transactional
    public Cosecha crearCosecha(Cosecha cosecha) {
        cosecha.calcularIngresoTotal(); // Calcula automáticamente
        return cosechaRepository.save(cosecha);
    }
    
    public List<Cosecha> obtenerCosechasPorParcela(Long parcelaId) {
        return cosechaRepository.findByParcelaId(parcelaId);
    }
    
    public List<Cosecha> obtenerCosechasPorCultivo(Long cultivoId) {
        return cosechaRepository.findByCultivoId(cultivoId);
    }
    
    public BigDecimal obtenerProduccionTotalPorCultivo(Long cultivoId, LocalDateTime inicio, LocalDateTime fin) {
        return cosechaRepository.sumCantidadByCultivoAndFecha(cultivoId, inicio, fin);
    }
    
    @Transactional
    public Cosecha actualizarCosecha(Long id, Cosecha cosechaActualizada) {
        Cosecha cosechaExistente = cosechaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Cosecha no encontrada"));
        
        cosechaExistente.setFechaCosecha(cosechaActualizada.getFechaCosecha());
        cosechaExistente.setCantidad(cosechaActualizada.getCantidad());
        cosechaExistente.setUnidadMedida(cosechaActualizada.getUnidadMedida());
        cosechaExistente.setCalidad(cosechaActualizada.getCalidad());
        cosechaExistente.setPrecioVenta(cosechaActualizada.getPrecioVenta());
        cosechaExistente.setObservaciones(cosechaActualizada.getObservaciones());
        cosechaExistente.calcularIngresoTotal();
        
        return cosechaRepository.save(cosechaExistente);
    }
    
    @Transactional
    public void eliminarCosecha(Long id) {
        cosechaRepository.deleteById(id);
    }
}