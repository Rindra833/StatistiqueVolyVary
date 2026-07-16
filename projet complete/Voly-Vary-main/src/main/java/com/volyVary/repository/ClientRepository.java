package com.volyVary.repository;

import com.volyVary.modele.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Integer> {
    public Client findByReferenceContainingIgnoreCase(String reference);

    Optional<Client> findFirstByReferenceIgnoreCase(String reference);

    @Query("SELECT c FROM Client c WHERE c.id = :idClient")
    Client TrouverParIdClient(int idClient);

    @Query("SELECT c FROM Client c WHERE c.telephone = :telephone")
    List<Client> TrouverParTelephone(String telephone);

    @Query("SELECT c FROM Client c ORDER BY c.id DESC")
    List<Client> trouverClientsParIdDesc();
}
