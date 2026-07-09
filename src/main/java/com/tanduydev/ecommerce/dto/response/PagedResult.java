package com.tanduydev.ecommerce.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagedResult<T> implements Serializable {
    private List<T> data;
    private PaginationResponse pagination;

    public PagedResult(Page<T> page) {
        this.data = page.getContent();
        this.pagination = new PaginationResponse(page);
    }
}