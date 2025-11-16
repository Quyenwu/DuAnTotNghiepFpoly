package com.example.the_autumn.controller;

import com.example.the_autumn.service.MultipartInputStreamFileResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/cccd")
public class CCCDController {

    @Value("${fpt.ai.key}")
    private String FPT_API_KEY;

    private static final String FPT_API_URL = "https://api.fpt.ai/vision/idr/vnm";

    @PostMapping("/scan")
    public ResponseEntity<?> scanCCCD(@RequestParam("file") MultipartFile file) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("api-key", FPT_API_KEY);
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("image", new MultipartInputStreamFileResource(file.getInputStream(), file.getOriginalFilename()));

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            RestTemplate restTemplate = new RestTemplate();

            String response = restTemplate.postForObject(FPT_API_URL, requestEntity, String.class);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "OCR failed", "message", e.getMessage()));
        }
    }
}
