package com.trungquan.nongsan.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserSearchDTO {
    private Long categoryId;
    private String sortBy;
    private String keyword;
    private String amountGap;

    public boolean isEmpty() {
        return categoryId == null
                && (keyword == null || keyword.isEmpty())
                && (sortBy == null || sortBy.isEmpty())
                && (amountGap == null || amountGap.isEmpty());
    }
}
