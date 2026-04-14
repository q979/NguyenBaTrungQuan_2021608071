package com.trungquan.nongsan.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ProductSearchDTO {
    private Long categoryId;
    private String keyword;

}

