package com.iktproject.service;

import com.iktproject.model.Material;

import java.util.List;

public interface MaterialService {
    List<Material> findAll();
    Material findById(Long id);
    Material save(Material material);
    Material update(Long id, Material material);
    void delete(Long id);
}
