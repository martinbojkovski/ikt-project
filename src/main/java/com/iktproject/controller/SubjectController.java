package com.iktproject.controller;

import com.iktproject.model.Question;
import com.iktproject.model.Subject;
import com.iktproject.model.Type;
import com.iktproject.model.User;
import com.iktproject.service.MaterialService;
import com.iktproject.service.SubjectService;
import com.iktproject.service.impl.TestService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@Controller
public class SubjectController {

    private final SubjectService subjectService;
    private final MaterialService materialService;
    private final TestService testService;

    public SubjectController(
            SubjectService subjectService, MaterialService materialService, TestService testService) {
        this.subjectService = subjectService;
        this.materialService = materialService;
        this.testService = testService;
    }

    @GetMapping("/subjects")
    public String listSubjects(Model model) {
        model.addAttribute("subjects", subjectService.findAll());
        return "subjects";
    }

    @GetMapping("/subjects/{id}/materials")
    public String showMaterials(@PathVariable Long id, Model model) {
        Subject subject = subjectService.findById(id);
        model.addAttribute("subject", subject);
        model.addAttribute("materials", subject.getMaterials());
        return "materials";
    }

    @GetMapping("/subjects/{id}/generate-material")
    public String showGenerateMaterialForm(@PathVariable Long id, Model model) {
        Subject subject = subjectService.findById(id); // get subject details
        model.addAttribute("subject", subject);
        return "generate-material";
    }

    @PostMapping("/subjects/{id}/generate-material")
    public String generateMaterial(@PathVariable Long id, @RequestParam("prompt") String prompt, Model model)
            throws IOException {
        Subject subject = subjectService.findById(id); // get subject details

        try {
            testService.generateMaterial(id, prompt);
        } catch (Exception e) {
            model.addAttribute("error", "An error occurred while generating material: " + e.getMessage());
            model.addAttribute("subject", subject);
            return "generate-material"; // return to the same page with error message
        }

        return "redirect:/subjects/" + id + "/materials";
    }

    @GetMapping("/subjects/{id}/generate-test")
    public String generateTest(@PathVariable Long id, Model model) {
        List<Question> questions = testService.generateTestForMaterial(id);
        model.addAttribute("questions", questions);
        model.addAttribute("subjectId", id);
        return "test";
    }

    @PostMapping("/submit-test")
    public String submitTest(@RequestParam Map<String, String> formData,
                             Principal principal)
    {
        double grade = testService.calculateGrade(formData);

        User user = testService.getLoggedInUser(principal.getName());
            if (user.getType() == Type.STUDENT) {
                user.setGrade_average(grade);
                testService.saveUser(user);
            }

            return "redirect:/subjects";
        }
}