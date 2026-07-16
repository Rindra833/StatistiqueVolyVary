package com.volyVary.modele;

import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import jakarta.persistence.*;

@Entity
@Table(name = "utilisateur")
public class Utilisateur implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "nom")
    private String nom;

    @Column(name = "mdp")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String mdp;

    @Column(name = "role")
    private String role;

    @OneToOne
    @JoinColumn(name = "id_employee")
    private Employee employee;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNom(){
        return nom;
    }
    public void setNom(String nom){
        this.nom = nom;
    }
    public String getMdp(){
        return mdp;
    }
    public void setMdp(String mdp){
        this.mdp = mdp;
    }
    public String getRole(){
        return role;
    }
    public void setRole(String role){
        this.role = role;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }


    @Override
    @JsonIgnore
    /**
     * Transforme le rôle métier en autorité Spring Security préfixée par ROLE_.
     */
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (role == null || role.isBlank()) {
            return List.of();
        }
        String authority = role.startsWith("ROLE_") ? role : "ROLE_" + role;
        return List.of(new SimpleGrantedAuthority(authority));
    }

    @Override
    @JsonIgnore
    /**
     * Retourne le hash BCrypt stocké dans mdp à Spring Security, sans l'exposer dans le JSON.
     */
    public String getPassword() {
        return mdp;
    }

    @Override
    @JsonIgnore
    /**
     * Indique à Spring Security que le champ nom constitue l'identifiant de connexion.
     */
    public String getUsername() {
        return nom;
    }

}
