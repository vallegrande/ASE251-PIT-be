package vallegrande.edu.pe.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "usuarios")
public class Usuario extends AuditableEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_usuario")
	private Long id;

	@Column(name = "nombre", nullable = false, length = 100)
	private String nombre;

	@Column(name = "apellido", nullable = false, length = 100)
	private String apellido;

	@Column(name = "email", nullable = false, unique = true, length = 150)
	private String email;

	@Column(name = "telefono", length = 20)
	private String telefono;

	@Column(name = "tipo_documento", nullable = false, length = 30)
	private String tipoDocumento;

	@Column(name = "numero_documento", nullable = false, unique = true, length = 30)
	private String numeroDocumento;

	@Column(name = "direccion", length = 250)
	private String direccion;

	@Column(name = "password", nullable = false, length = 255)
	private String password;
}