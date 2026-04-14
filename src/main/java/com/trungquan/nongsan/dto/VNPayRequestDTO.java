package com.trungquan.nongsan.dto;

import lombok.Data;

@Data
public class VNPayRequestDTO {

    private String fullName;

    private String phoneNumber;

    private String email;

    private String bankCode;

    private Long totalAmount;

    private String address;

}
