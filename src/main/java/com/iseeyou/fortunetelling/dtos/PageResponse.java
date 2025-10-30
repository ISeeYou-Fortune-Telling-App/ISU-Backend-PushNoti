package com.iseeyou.fortunetelling.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class PageResponse<T> extends AbstractBaseResponse {
    private List<T> data;
    private PagingResponse paging;

    @Data
    @AllArgsConstructor
    public static class PagingResponse {
        // The current page number being returned
        private int page;
        // The maximum number of items per page
        private int limit;
        // The total number of items across all pages
        private long total;
        // The total number of pages available based on the total items and limit
        private int totalPages;
    }
}