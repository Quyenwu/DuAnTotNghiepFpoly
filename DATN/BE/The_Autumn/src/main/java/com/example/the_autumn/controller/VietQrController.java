package com.example.the_autumn.controller;

import com.example.the_autumn.model.request.VietQRRequest;
import com.example.the_autumn.model.response.VietQRResponse;
import com.example.the_autumn.service.VietQRService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vietqr")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class VietQrController {

    private final VietQRService vietQRService;

    @PostMapping("/tao-qr")
    public VietQRResponse taoMaQR(@RequestBody VietQRRequest req) {

        return vietQRService.taoVietQR(
                req.getAmount(),
                req.getNoiDung(),
                req.getOrderId(),
                req.getTenKhachHang()
        );
    }
}
