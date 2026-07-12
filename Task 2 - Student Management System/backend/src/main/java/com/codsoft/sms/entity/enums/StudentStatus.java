package com.codsoft.sms.entity.enums;

/**
 * Lifecycle status of a student's enrollment.
 *
 * <p>Stored as a STRING in the database ({@code VARCHAR(15)}) via
 * {@code @Enumerated(EnumType.STRING)} — ordinal storage is explicitly avoided
 * because ordinal values break silently when enum constants are reordered.
 *
 * <ul>
 *   <li>{@code ACTIVE}    — currently enrolled and attending</li>
 *   <li>{@code INACTIVE}  — enrollment paused or on hold</li>
 *   <li>{@code GRADUATED} — course completed successfully</li>
 *   <li>{@code SUSPENDED} — enrollment suspended due to disciplinary or administrative reasons</li>
 * </ul>
 */
public enum StudentStatus {

    ACTIVE,
    INACTIVE,
    GRADUATED,
    SUSPENDED
}
