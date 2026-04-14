package com.trungquan.nongsan.service.impl;

import com.trungquan.nongsan.service.VietQRService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class VietQRServiceImpl implements VietQRService {

    @Value("${vietqr.account.number}")
    private String accountNumber;

    @Value("${vietqr.account.name}")
    private String accountName;

    @Value("${vietqr.bank.code}")
    private String bankCode;

    @Value("${vietqr.template}")
    private String template;

    @Override
    public String generateQRUrl(double amount, String orderCode) {
        // Content chuyển khoản = mã đơn hàng
        String addInfo = orderCode;

        // Số tiền: bỏ phần thập phân (VD: 150000)
        long amountLong = (long) amount;

        // Encode addInfo để URL an toàn
        String encodedAddInfo = URLEncoder.encode(addInfo, StandardCharsets.UTF_8);

        // URL VietQR động: template + params
        // Format: https://img.vietqr.io/image/{bankCode}-{accountNumber}-{template}.png
        //          ?addInfo={noi dung}&amount={so tien}
        String url = String.format(
                "https://img.vietqr.io/image/%s-%s-%s.png?addInfo=%s&amount=%d",
                bankCode,
                accountNumber,
                template,
                encodedAddInfo,
                amountLong
        );

        return url;
    }

    // Getter để template có thể hiển thị thông tin
    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAccountName() {
        return accountName;
    }

    public String getBankCode() {
        return bankCode;
    }
}
