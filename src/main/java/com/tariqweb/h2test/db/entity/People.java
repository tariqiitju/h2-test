package com.tariqweb.h2test.db.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Entity
@Table(name = People.TABLE_NAME)
@Setter
@Getter
@ToString
public class People {
    public final static String TABLE_NAME = "people";
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 100)
    private String name;
    @Column(nullable = false)
    private Date birthDate;
    @Column(nullable = false)
    private java.sql.Timestamp createTime;
    @Column(nullable = false)
    private java.sql.Timestamp memberSince;
    @Column(nullable = false)
    private OffsetDateTime lastUpdated;
    @Column(nullable = false)
    private LocalDateTime lastUpdatedLocal;
    @Column(nullable = false)
    private LocalDate lastUpdatedLocalDate;
}
