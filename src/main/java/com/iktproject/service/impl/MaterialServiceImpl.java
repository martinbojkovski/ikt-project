package com.iktproject.service.impl;

import com.iktproject.model.Material;
import com.iktproject.model.Subject;
import com.iktproject.repository.MaterialRepository;
import com.iktproject.service.MaterialService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
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

    @Override
    public List<Material> importFromFile(MultipartFile file, Subject subject) throws IOException {
        List<Material> importedMaterials = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            StringBuilder contentBuilder = new StringBuilder();
            String currentTitle = null;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("===") && line.endsWith("===")) {
                    currentTitle = line.substring(3, line.length() - 3).trim();
                } else {
                    if (contentBuilder.length() > 0) {
                        contentBuilder.append("\n");
                    }
                    contentBuilder.append(line);
                }
            }

            if (currentTitle != null && contentBuilder.length() > 0) {
                Material material = new Material(
                        currentTitle,
                        contentBuilder.toString(),
                        subject
                );
                importedMaterials.add(save(material));
            }
        }

        return importedMaterials;
    }
}
