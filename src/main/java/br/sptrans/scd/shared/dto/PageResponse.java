package br.sptrans.scd.shared.dto;

import java.util.List;

public record PageResponse<T>(
    List<T> content,
    int page,
    int size,
    long totalElements,
    int totalPages,
    boolean first,
    boolean last
) {
    public static <T> PageResponse<T> of(List<T> content, int page, int size, long totalElements) {
        int totalPages = size > 0 ? (int) Math.ceil((double) totalElements / size) : 0;
        return new PageResponse<>(
            content,
            page,
            size,
            totalElements,
            totalPages,
            page == 0,
            page >= totalPages - 1
        );
    }

    public static <T> PageResponse<T> fromList(List<T> allItems, int page, int size) {
        long totalElements = allItems.size();
        int start = Math.min(page * size, allItems.size());
        int end = Math.min(start + size, allItems.size());
        List<T> content = allItems.subList(start, end);
        return of(content, page, size, totalElements);
    }
}
