package com.raszsixt._d2h.common.search.dto;

import lombok.Data;
@Data
public class SearchDto {
    private String type;
    private String keyword;
    private boolean signOut;
    private String sort;
    private String sortColumn;
}
