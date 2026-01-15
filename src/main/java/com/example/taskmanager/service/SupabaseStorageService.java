package com.example.taskmanager.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Slf4j
@Service
public class SupabaseStorageService {

    @Value("${supabase.url:}")
    private String supabaseUrl;

    @Value("${supabase.key:}")
    private String supabaseKey;

    @Value("${supabase.bucket:task-attachments}")
    private String bucketName;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Upload a file to Supabase Storage
     * @return the stored file name (path in bucket)
     */
    public String uploadFile(MultipartFile file) {
        if (supabaseUrl == null || supabaseUrl.isEmpty() || supabaseKey == null || supabaseKey.isEmpty()) {
            log.warn("Supabase not configured, skipping upload");
            return null;
        }

        try {
            String storedName = UUID.randomUUID() + "_" + sanitizeFilename(file.getOriginalFilename());
            String uploadUrl = supabaseUrl + "/storage/v1/object/" + bucketName + "/" + storedName;

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + supabaseKey);
            headers.set("apikey", supabaseKey);
            headers.setContentType(MediaType.parseMediaType(file.getContentType()));

            HttpEntity<byte[]> requestEntity = new HttpEntity<>(file.getBytes(), headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    uploadUrl,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("File uploaded successfully to Supabase: {}", storedName);
                return storedName;
            } else {
                log.error("Failed to upload file to Supabase: {}", response.getBody());
                throw new RuntimeException("Failed to upload file to Supabase");
            }

        } catch (Exception e) {
            log.error("Error uploading file to Supabase: {}", e.getMessage());
            throw new RuntimeException("Failed to upload file: " + e.getMessage());
        }
    }

    /**
     * Get public URL for a file
     */
    public String getPublicUrl(String storedName) {
        if (supabaseUrl == null || supabaseUrl.isEmpty()) {
            return null;
        }
        return supabaseUrl + "/storage/v1/object/public/" + bucketName + "/" + storedName;
    }

    /**
     * Delete a file from Supabase Storage
     */
    public void deleteFile(String storedName) {
        if (supabaseUrl == null || supabaseUrl.isEmpty() || supabaseKey == null || supabaseKey.isEmpty()) {
            log.warn("Supabase not configured, skipping delete");
            return;
        }

        try {
            String deleteUrl = supabaseUrl + "/storage/v1/object/" + bucketName + "/" + storedName;

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + supabaseKey);
            headers.set("apikey", supabaseKey);

            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

            restTemplate.exchange(deleteUrl, HttpMethod.DELETE, requestEntity, String.class);
            log.info("File deleted from Supabase: {}", storedName);

        } catch (Exception e) {
            log.warn("Error deleting file from Supabase: {}", e.getMessage());
        }
    }

    /**
     * Check if Supabase is configured
     */
    public boolean isConfigured() {
        return supabaseUrl != null && !supabaseUrl.isEmpty() 
            && supabaseKey != null && !supabaseKey.isEmpty();
    }

    private String sanitizeFilename(String filename) {
        if (filename == null) return "file";
        // Replace spaces and special characters
        return filename.replaceAll("[^a-zA-Z0-9.-]", "_");
    }
}
