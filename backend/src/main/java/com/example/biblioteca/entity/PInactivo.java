package com.example.biblioteca.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@Entity
@Table(name = "prestamo_inactivo")
public class PInactivo  implements Serializable {
    @Id
    @Column(name = "ID_PRESTAMOFIN")
    @Getter @Setter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter @Setter
    @Column(name = "estado")
    private String estado;

    @Temporal(TemporalType.TIMESTAMP)
    @Getter @Setter
    @JsonFormat(pattern = "dd-MM-yyyy")
    @Column(name = "i_finicio")
    private Date finicio;

    @Temporal(TemporalType.TIMESTAMP)
    @Getter @Setter
    @JsonFormat(pattern = "dd-MM-yyyy")
    @Column(name = "i_ffin")
    private Date ffin;

    @Getter @Setter
    @ManyToOne
    @JoinColumn(name = "libro_id")
    private Libro libro;

    @Getter @Setter
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public PInactivo(){}


}
