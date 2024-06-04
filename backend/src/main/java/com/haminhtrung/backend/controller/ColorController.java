package com.haminhtrung.backend.controller;

import com.haminhtrung.backend.entity.Color;
import com.haminhtrung.backend.service.ColorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/colors")
@CrossOrigin(origins = { "http://localhost:3000", "http://localhost:3001" }, exposedHeaders = "Content-Range")
public class ColorController {

    @Autowired
    private ColorService colorService;

    @GetMapping
    public ResponseEntity<List<Color>> getAllColors() {
        List<Color> colors = colorService.getAllColors();
        return ResponseEntity.ok(colors);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Color> getColorById(@PathVariable("id") Long id) {
        Color color = colorService.getColorById(id);
        return color != null ? ResponseEntity.ok(color) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Color> createColor(@RequestBody Color color) {
        Color createdColor = colorService.createColor(color);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdColor);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Color> updateColor(@PathVariable("id") Long id, @RequestBody Color color) {
        Color updatedColor = colorService.updateColor(id, color);
        return updatedColor != null ? ResponseEntity.ok(updatedColor) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteColor(@PathVariable("id") Long id) {
        colorService.deleteColor(id);
        return ResponseEntity.noContent().build();
    }
}
