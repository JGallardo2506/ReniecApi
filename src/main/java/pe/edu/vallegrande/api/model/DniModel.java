package pe.edu.vallegrande.api.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table(name = "usuarios")
public class DniModel {
    @Id
    private Long id;

    @Column("dni")
    private String dni; // DNI del usuario

    @Column("nombres")
    private String nombres; // Nombres del usuario

    @Column("apellidoPaterno")
    private String apellidoPaterno; // Apellido paterno del usuario

    @Column("apellidoMaterno")
    private String apellidoMaterno; // Apellido materno del usuario

    @Column("codVerifica")
    private String codVerifica; // Código de verificación

    @Column("status")
    private String status; // A o I
}
