package com.codsoft.sms.dto.response;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Pagination envelope for list endpoints that return a {@link org.springframework.data.domain.Page}.
 *
 * <p>Shape per SDD Section 7.1:
 * <pre>{@code
 * {
 *   "content":       [ { ... } ],
 *   "pageNumber":    0,
 *   "pageSize":      10,
 *   "totalElements": 57,
 *   "totalPages":    6,
 *   "last":          false
 * }
 * }</pre>
 *
 * <p>Wraps a Spring {@link Page} via the {@link #from(Page)} factory so controllers
 * never reference the Spring {@code Page} type directly in their response bodies —
 * keeping the REST contract stable even if the underlying data-access strategy changes.
 *
 * @param <T> the type of items in the page
 */
@Getter
@Builder
public final class PagedResponse<T> {

    /** The items in the current page. */
    private final List<T> content;

    /** Zero-based page index. */
    private final int pageNumber;

    /** Number of items requested per page. */
    private final int pageSize;

    /** Total number of items across all pages. */
    private final long totalElements;

    /** Total number of pages available. */
    private final int totalPages;

    /** {@code true} when this is the final page. */
    private final boolean last;

    /**
     * Builds a {@link PagedResponse} from a Spring Data {@link Page}.
     *
     * <p>The content list is provided separately so that the mapper layer can
     * convert the entity page into a DTO page before wrapping it here.
     *
     * @param page    the Spring {@link Page} providing metadata
     * @param content the already-mapped DTO list for this page
     * @param <T>     the DTO type
     * @return a fully-populated {@link PagedResponse}
     */
    public static <T> PagedResponse<T> from(Page<?> page, List<T> content) {
        return PagedResponse.<T>builder()
                .content(content)
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }
}
