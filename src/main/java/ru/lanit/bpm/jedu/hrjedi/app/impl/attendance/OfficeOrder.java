package ru.lanit.bpm.jedu.hrjedi.app.impl.attendance;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Enum values represent the sort order of offices for attendance report.
 * To change the sort order, simply change the order of the enum declarations.
 * Enum<E> implements Comparable<E> via the natural order of the enum (the order in which the values are declared).
 */
public enum OfficeOrder {
    NIZHNY_NOVGOROD("Нижний Новгород"),
    UFA("Уфа"),
    MOSCOW("Москва"),
    OTHER("Любой другой город");

    private String name;

    private static final Map<String, OfficeOrder> ENUM_MAP;

    OfficeOrder(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    static {
        Map<String, OfficeOrder> map = new HashMap<>();
        for (OfficeOrder instance : OfficeOrder.values()) {
            map.put(instance.getName().toLowerCase(),instance);
        }
        ENUM_MAP = Collections.unmodifiableMap(map);
    }

    public static OfficeOrder get(String name) {
        OfficeOrder office = ENUM_MAP.get(name.toLowerCase());
        return (office != null) ? office : OTHER;
    }
}
