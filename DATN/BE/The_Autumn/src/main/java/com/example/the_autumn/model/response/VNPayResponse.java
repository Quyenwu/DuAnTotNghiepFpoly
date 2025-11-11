package com.example.the_autumn.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class VNPayResponse {

    private String paymentUrl;
    private Integer orderId;
    private BigDecimal amount;
    private String orderInfo;
}
