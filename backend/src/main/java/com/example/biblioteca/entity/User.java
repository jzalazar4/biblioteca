package com.example.biblioteca.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@ToString(exclude = {"prestamos_activos"})
@Table(name="user", schema = "public" )
public class User implements UserDetails {

    @Getter @Setter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Id
    private Long id;

    @Getter @Setter
    @Column(name = "nombre")
    private String nombre;

    @Getter @Setter
    @Column(name = "apellido")
    private String apellido;

    @Getter @Setter
    @Column(name = "telefono")
    private Long telefono;

    @Getter @Setter
    @Column(name = "email")
    private String email;

    @Getter @Setter
    @Column(name = "estado")
    private String estado;

    @Column(name = "password")
    private String password;


    @Column(name = "locked")
    private boolean locked;

    @Setter
    @Column(name = "enabled")
    private boolean enabled;

    @Getter @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "rol")
    private Role role;

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(
                new SimpleGrantedAuthority(role.name())
        );
    }

    @JsonIgnore
    @Getter @Setter
    @OneToMany(mappedBy = "user" , cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PActivo> prestamos_activos;

    @JsonIgnore
    @Getter @Setter
    @OneToMany(mappedBy = "user" , cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PInactivo> prestamos_inactivos;


    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
