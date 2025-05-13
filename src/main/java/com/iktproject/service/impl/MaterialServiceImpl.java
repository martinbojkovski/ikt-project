package com.iktproject.service.impl;

import com.iktproject.model.Material;
import com.iktproject.model.Subject;
import com.iktproject.repository.MaterialRepository;
import com.iktproject.service.MaterialService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MaterialServiceImpl implements MaterialService {

    private final MaterialRepository materialRepository;

    public MaterialServiceImpl(MaterialRepository materialRepository) {
        this.materialRepository = materialRepository;
    }

    @Override
    public List<Material> findAll() {
        return materialRepository.findAll();
    }

    @Override
    public Material findById(Long id) {
        return materialRepository.findById(id).orElse(null);
    }

    @Override
    public Material save(Material material) {
        return materialRepository.save(material);
    }

    @Override
    public Material update(Long id, Material material) {
        Material material1 = findById(id);
        material1.setSubject(material.getSubject());
        material1.setContent(material.getContent());
        material1.setTitle(material.getTitle());
        return material1;
    }

    @Override
    public void delete(Long id) {
        Material material = findById(id);
        materialRepository.delete(material);
    }
}
