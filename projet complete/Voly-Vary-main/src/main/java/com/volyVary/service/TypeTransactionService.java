package com.volyVary.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.volyVary.modele.*;
import com.volyVary.repository.*;

@Service
public class TypeTransactionService {
    @Autowired
    private TypeTransactionRepository transactionTypeRepository;

    public List<TypeTransaction> getTypesTransaction() {
        return transactionTypeRepository.findAllByOrderByLibelleAsc();
    }

    public TypeTransaction trouverTypeTransaction(int idTypeTransaction) {
        return transactionTypeRepository.findById(idTypeTransaction).orElse(null);
    }

    public List<Map<String, Object>> convertirTypesTransaction(List<TypeTransaction> typesTransaction) {
        List<Map<String, Object>> resultat = new ArrayList<>();
        for (TypeTransaction typeTransaction : typesTransaction) {
            resultat.add(convertirTypeTransaction(typeTransaction));
        }
        return resultat;
    }

    public Map<String, Object> convertirTypeTransaction(TypeTransaction typeTransaction) {
        Map<String, Object> resultat = new HashMap<>();
        resultat.put("idTypeTransaction", typeTransaction.getId());
        resultat.put("libelleTypeTransaction", typeTransaction.getLibelle());
        return resultat;
    }
}
