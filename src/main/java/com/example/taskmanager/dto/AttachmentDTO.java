package com.example.taskmanager.dto;

import lombok.Data;

@Data
public class AttachmentDTO {

    private String originalName;   // nama asal file
    private String storedName;     // nama file disimpan (uuid_filename)
    private String contentType;    // application/pdf, image/png, etc
    private Long size;             // saiz file (bytes)
    private String path;           // path dalam server (uploads/xxx.pdf)
}
