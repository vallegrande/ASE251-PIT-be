package vallegrande.edu.pe.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "riegos")
@EqualsAndHashCode(callSuper = true)
public class Riego extends AuditableEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_riego")
    private Long idRiego;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_parcela", nullable = false)
    private Parcela parcela;
    
    @Column(name = "fecha_riego", nullable = false)
    private LocalDateTime fechaRiego;
    
    @Column(name = "tipo_riego", nullable = false, length = 50)
    private String tipoRiego; // manual, aspersion, goteo, inundacion
    
    @Column(name = "duracion_minutos", nullable = false)
    private Integer duracionMinutos = 0;
    
    @Column(name = "cantidad_agua", precision = 10, scale = 2)
    private BigDecimal cantidadAgua;
    
    @Column(name = "responsable", length = 100)
    private String responsable;
    
    @Column(name = "observaciones", length = 500)
    private String observaciones;

    // Implementar métodos abstractos de CrudEntity (si AuditableEntity no los tiene)
    @Override
    public Long getId() {
        return idRiego;
    }

    @Override
    public void setId(Long id) {
        this.idRiego = id;
    }
}