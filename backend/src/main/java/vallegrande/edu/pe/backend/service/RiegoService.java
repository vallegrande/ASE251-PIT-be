package vallegrande.edu.pe.backend.service;

import vallegrande.edu.pe.backend.model.Riego;
import vallegrande.edu.pe.backend.repository.RiegoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RiegoService {
    
    private final RiegoRepository riegoRepository;
    
    @Transactional
    public Riego crearRiego(Riego riego) {
        return riegoRepository.save(riego);
    }
    
    public List<Riego> obtenerRiegosPorParcela(Long parcelaId) {
        return riegoRepository.findByParcelaId(parcelaId);
    }
    
    public List<Riego> obtenerRiegosPorFecha(LocalDateTime inicio, LocalDateTime fin) {
        return riegoRepository.findByFechaRiegoBetween(inicio, fin);
    }
    
    public Long contarRiegosRecientes(Long parcelaId, LocalDateTime desde) {
        return riegoRepository.countRiegosByParcelaDesde(parcelaId, desde);
    }
    
    @Transactional
    public Riego actualizarRiego(Long id, Riego riegoActualizado) {
        Riego riegoExistente = riegoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Riego no encontrado"));
        
        riegoExistente.setFechaRiego(riegoActualizado.getFechaRiego());
        riegoExistente.setTipoRiego(riegoActualizado.getTipoRiego());
        riegoExistente.setDuracionMinutos(riegoActualizado.getDuracionMinutos());
        riegoExistente.setCantidadAgua(riegoActualizado.getCantidadAgua());
        riegoExistente.setResponsable(riegoActualizado.getResponsable());
        riegoExistente.setObservaciones(riegoActualizado.getObservaciones());
        
        return riegoRepository.save(riegoExistente);
    }
    
    @Transactional
    public void eliminarRiego(Long id) {
        riegoRepository.deleteById(id);
    }
}