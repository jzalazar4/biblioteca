package com.example.biblioteca.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@Entity
@ToString(exclude = {"user"})
@AllArgsConstructor
@Table(name = "prestamo_activo")
public class PActivo implements Serializable {

    @Id
    @Getter @Setter
    @Column(name = "ID_PRESTAMO")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter @Setter
    @Column(name = "ESTADO")
    private String estado;

    @Getter @Setter
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    @Column(name = "FECHA_INICIO")
    private Date finicio;

    @Getter @Setter
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    @Column(name = "FECHA_FIN")
    private Date ffin;
    private boolean isReturned;

    // datos usuario y libro
    @ManyToOne
    @Getter @Setter
    @JoinColumn(name = "user_id")
    private User user;

    @Getter @Setter
    @ManyToOne
    @JoinColumn(name = "libro_id")
    private Libro libro;


    public PActivo(){

    }


}
