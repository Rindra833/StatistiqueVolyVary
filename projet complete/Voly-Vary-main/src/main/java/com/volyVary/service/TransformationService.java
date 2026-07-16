package com.volyVary.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.volyVary.modele.*;
import com.volyVary.repository.*;

@Service
public class TransformationService {
    @Autowired
    private TransformationRepository transformationRepository;

    public Transformation getTransformation() {
        return transformationRepository.findTopByOrderByIdTransformationDesc();
    }


}
