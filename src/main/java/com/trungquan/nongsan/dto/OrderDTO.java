package com.trungquan.nongsan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class OrderDTO {

	private String receiveFullName;
	private String receivePhoneNumber;
	private String receiveEmail;
	private String createdAt;
	private Double totalPrice;
	private String status;
	private String paymentMethod;

}
