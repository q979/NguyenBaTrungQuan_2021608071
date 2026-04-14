package com.trungquan.nongsan.service.impl;

import com.trungquan.nongsan.dto.ProductDto;
import com.trungquan.nongsan.dto.CategoryDto;
import com.trungquan.nongsan.dto.OrderDTO;
import com.trungquan.nongsan.entity.User;
import com.trungquan.nongsan.service.ExportService;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class ExportSerivceImpl implements ExportService {

    @Override
    public String exportOrderReport(User user, List<OrderDTO> orderDTOList, String keyword) {
        try {
            InputStream reportStream = new ClassPathResource("orders.jrxml").getInputStream();
            Map<String, Object> parameters = new HashMap<>();
            JRBeanCollectionDataSource orderDataSource = new JRBeanCollectionDataSource(orderDTOList);
            parameters.put("fullName", user.getFullName());
            parameters.put("email", user.getEmail());
            parameters.put("phoneNumber", user.getPhoneNumber());
            parameters.put("orderDataSet", orderDataSource);

            JasperReport report = JasperCompileManager.compileReport(reportStream);
            JasperPrint print = JasperFillManager.fillReport(report, parameters, orderDataSource);
            String outputFile = "C:/trungquan_datn/File/xuatfile/order";
            if ("pdf".equals(keyword)) {
                JasperExportManager.exportReportToPdfFile(print, outputFile + ".pdf");
            } else if ("html".equals(keyword)) {
                JasperExportManager.exportReportToHtmlFile(print, outputFile + ".html");
            }
        } catch (Exception e) {
            return "Lỗi khi xuất file: " + e.getMessage();
        }
        return "Xuất file thành công!";
    }

    @Override
    public String exportCategoryReport(User user, List<CategoryDto> categoryDTOList, String keyword) {
        try {
            InputStream reportStream = new ClassPathResource("category1.jrxml").getInputStream();
            Map<String, Object> parameters = new HashMap<>();
            JRBeanCollectionDataSource categoryDataSource = new JRBeanCollectionDataSource(categoryDTOList);
            parameters.put("categoryDataSet", categoryDataSource);

            JasperReport report = JasperCompileManager.compileReport(reportStream);
            JasperPrint print = JasperFillManager.fillReport(report, parameters, categoryDataSource);
            String outputFile = "C:/trungquan_datn/File/xuatfile/category";
            if ("pdf".equals(keyword)) {
                JasperExportManager.exportReportToPdfFile(print, outputFile + ".pdf");
            } else if ("html".equals(keyword)) {
                JasperExportManager.exportReportToHtmlFile(print, outputFile + ".html");
            }
        } catch (Exception e) {
            return "Lỗi khi xuất file: " + e.getMessage();
        }
        return "Xuất file thành công!";
    }

    @Override
    public String exportProductReport(User user, List<ProductDto> productDtoList, String keyword) {
        try {
            InputStream reportStream = new ClassPathResource("product1.jrxml").getInputStream();
            Map<String, Object> parameters = new HashMap<>();
            JRBeanCollectionDataSource productDataSource = new JRBeanCollectionDataSource(productDtoList);
            parameters.put("productDataSet", productDataSource);

            JasperReport report = JasperCompileManager.compileReport(reportStream);
            JasperPrint print = JasperFillManager.fillReport(report, parameters, productDataSource);
            String outputFile = "C:/trungquan_datn/File/xuatfile/product";
            if ("pdf".equals(keyword)) {
                JasperExportManager.exportReportToPdfFile(print, outputFile + ".pdf");
            } else if ("html".equals(keyword)) {
                JasperExportManager.exportReportToHtmlFile(print, outputFile + ".html");
            }
        } catch (Exception e) {
            return "Lỗi khi xuất file: " + e.getMessage();
        }
        return "Xuất file thành công!";
    }

}
