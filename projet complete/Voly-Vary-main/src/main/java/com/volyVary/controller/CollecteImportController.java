package com.volyVary.controller;

import com.volyVary.modele.*;
import com.volyVary.repository.*;
import com.volyVary.service.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;



@Controller
@RequestMapping("/collectes")
public class CollecteImportController {
    @Autowired
    private ClientRepository clientRepository;
    
    @Autowired
    private ImportService importService;

    @PostMapping("/lire-excel")
    @ResponseBody
    public Map<String, Object> lireExcel(@RequestParam("fichier") MultipartFile fichier) {
        try {
            return importService.lireExcel(fichier);
        } catch (IllegalArgumentException e) {
            Map<String, Object> erreur = new HashMap<>();
            erreur.put("erreur", e.getMessage());
            return erreur;
        }
    }
}