package com.example.the_autumn.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "shipping_address_mapping")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShippingAddressMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tinh_thanh_id")
    private TinhThanh tinhThanh;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quan_huyen_id")
    private QuanHuyen quanHuyen;

    @Column(name = "ghn_district_id")
    private Integer ghnDistrictId;

    @Column(name = "ghn_ward_code")
    private String ghnWardCode;

    @Column(name = "ghtk_province_name")
    private String ghtkProvinceName;

    @Column(name = "ghtk_district_name")
    private String ghtkDistrictName;

    @Column(name = "shipping_provider")
    private String shippingProvider = "BOTH";
}