package com.example.the_autumn.model.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class GiaoCaEndRequest {
    @Size(max = 500, message = "Ghi chú không được vượt quá 500 ký tự")
    private String ghiChu;
}
