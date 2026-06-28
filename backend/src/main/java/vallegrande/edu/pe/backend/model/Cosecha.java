package vallegrande.edu.pe.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "cosechas")
@EqualsAndHashCode(callSuper = true)
public class Cosecha extends AuditableEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cosecha")
    private Long idCosecha;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_parcela", nullable = false)
    private Parcela parcela;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cultivo", nullable = false)
    private Cultivo cultivo;
    
    @Column(name = "fecha_cosecha", nullable = false)
    private LocalDateTime fechaCosecha;
    
    @Column(name = "cantidad", nullable = false, precision = 12, scale = 3)
    private BigDecimal cantidad;
    
    @Column(name = "unidad_medida", nullable = false, length = 20)
    private String unidadMedida; // kg, t, unidades
    
    @Column(name = "calidad", nullable = false, length = 50)
    private String calidad; // excelente, buena, regular, mala
    
    @Column(name = "precio_venta", precision = 10, scale = 2)
    private BigDecimal precioVenta;
    
    @Column(name = "ingreso_total", precision = 12, scale = 2)
    private BigDecimal ingresoTotal;
    
    @Column(name = "observaciones", length = 500)
    private String observaciones;
    
    @PrePersist
    @PreUpdate
    public void calcularIngresoTotal() {
        if (cantidad != null && precioVenta != null) {
            this.ingresoTotal = cantidad.multiply(precioVenta);
        }
    }

    // Implementar métodos abstractos de CrudEntity
    @Override
    public Long getId() {
        return idCosecha;
    }

    @Override
    public void setId(Long id) {
        this.idCosecha = id;
    }
}