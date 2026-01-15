package com.example.taskmanager.controller;

import com.example.taskmanager.service.SupabaseStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/files")
@CrossOrigin(origins = "*")
public class FileController {

    @Autowired
    private SupabaseStorageService supabaseStorageService;

    /**
     * Redirect to Supabase public URL for file access
     */
    @GetMapping("/{filename:.+}")
    public ResponseEntity<Void> getFile(@PathVariable String filename) {
        if (!supabaseStorageService.isConfigured()) {
            return ResponseEntity.notFound().build();
        }

        String publicUrl = supabaseStorageService.getPublicUrl(filename);
        
        if (publicUrl == null) {
            return ResponseEntity.notFound().build();
        }

        // Redirect to Supabase public URL
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(publicUrl));
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }
}
