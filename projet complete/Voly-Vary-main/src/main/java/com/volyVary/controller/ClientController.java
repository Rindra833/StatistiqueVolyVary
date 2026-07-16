package com.volyVary.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.volyVary.modele.*;
import com.volyVary.service.*;

@Controller
@RequestMapping("/client")
public class ClientController {
    @Autowired
    private ClientService clientService;

    @CrossOrigin(origins = "*")
    @GetMapping("/rechercher/{referenceClient}")
    @ResponseBody
    public Map<String, Object> rechercherClientParReference(@PathVariable("referenceClient") String referenceClient) {
        Client client = clientService.getClientParReference(referenceClient);

        Map<String, Object> resultat = new HashMap<>();

        if(client == null) return null;

        resultat.put("referenceClient", client.getReference());
        resultat.put("nom", client.getNom());
        resultat.put("prenom", client.getPrenom());
        resultat.put("telephone", client.getTelephone());

        return resultat;
    }
}
