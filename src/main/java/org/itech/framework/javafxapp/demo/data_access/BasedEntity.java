package org.itech.framework.javafxapp.demo.data_access;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@MappedSuperclass
@Data
public class BasedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_date_time")
    private Date createdDateTime;
    @Column(name = "updated_date_time")
    private Date updatedDateTime;
}
