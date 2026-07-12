package com.codsoft.sms.entity.enums;

/**
 * Represents the biological gender of a student.
 *
 * <p>Stored as a STRING in the database ({@code VARCHAR(10)}) via
 * {@code @Enumerated(EnumType.STRING)} — ordinal storage is explicitly avoided
 * because ordinal values break silently when enum constants are reordered.
 */
public enum Gender {

    MALE,
    FEMALE,
    OTHER
}
