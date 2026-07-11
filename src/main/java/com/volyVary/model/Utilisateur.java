package com.volyVary.model;

import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

@Entity
/**
 * Compte de connexion de l'application. Il reste distinct de l'entité Employee et implémente
 * UserDetails afin d'être compris directement par Spring Security.
 */
public class Utilisateur implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true)
    private String nom;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String mdp;
    private String role;

    @OneToOne
    @JoinColumn(name = "employee_id")
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
