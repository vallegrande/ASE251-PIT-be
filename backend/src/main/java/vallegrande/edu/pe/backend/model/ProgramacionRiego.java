package vallegrande.edu.pe.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "programacion_riegos")
@EqualsAndHashCode(callSuper = true)
public class ProgramacionRiego extends AuditableEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_programacion")
    private Long idProgramacion;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_parcela", nullable = false)
    private Parcela parcela;
    
    @Column(name = "fecha_programada", nullable = false)
    private LocalDateTime fechaProgramada;
    
    @Column(name = "frecuencia", nullable = false, length = 30)
    private String frecuencia; // diario, semanal, quincenal, mensual
    
    @Column(name = "tipo_riego", nullable = false, length = 50)
    private String tipoRiego;
    
    @Column(name = "duracion_estimada", nullable = false)
    private Integer duracionEstimada = 0;
    
    @Column(name = "activo", nullable = false)
    private Boolean activo = true;
    
    @Column(name = "observaciones", length = 500)
    private String observaciones;

    // Implementar métodos abstractos de CrudEntity
    @Override
    public Long getId() {
        return idProgramacion;
    }

    @Override
    public void setId(Long id) {
        this.idProgramacion = id;
    }
}