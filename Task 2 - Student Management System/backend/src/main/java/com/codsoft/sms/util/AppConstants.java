package com.codsoft.sms.util;

import java.util.Set;

/**
 * Application-wide constants used across controllers and services.
 *
 * <p>Centralising these values here avoids magic strings scattered across
 * multiple classes and makes the allowed value sets easy to extend.
 */
public final class AppConstants {

    private AppConstants() {
        // Utility class — do not instantiate.
    }

    /** Default page index (zero-based) for paginated list endpoints. */
    public static final int DEFAULT_PAGE_NUMBER = 0;

    /** Default number of items returned per page. */
    public static final int DEFAULT_PAGE_SIZE = 10;

    /** Maximum number of items the client may request per page. */
    public static final int MAX_PAGE_SIZE = 100;

    /**
     * Field names accepted as {@code sortBy} query parameters for the student list.
     *
     * <p>The raw {@code sortBy} string from the client is validated against this set
     * before it is passed to Spring Data — it is never interpolated directly into a
     * query string, preventing order-by injection.
     *
     * <p>Per SDD Section 7.4.
     */
    public static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "id", "firstName", "lastName", "rollNumber", "enrollmentDate", "grade"
    );

    /** Default sort field when none is supplied. */
    public static final String DEFAULT_SORT_BY = "id";

    /** Default sort direction when none is supplied. */
    public static final String DEFAULT_SORT_DIR = "asc";
}
