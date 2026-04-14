package com.trungquan.nongsan.service;

public interface VietQRService {
    /**
     * Generate a dynamic VietQR URL for a specific order.
     *
     * @param amount    Total amount in VND (will be converted to integer)
     * @param orderCode Unique order code, e.g. "DH123456"
     * @return VietQR image URL
     */
    String generateQRUrl(double amount, String orderCode);

    String getAccountNumber();

    String getAccountName();

    String getBankCode();
}
