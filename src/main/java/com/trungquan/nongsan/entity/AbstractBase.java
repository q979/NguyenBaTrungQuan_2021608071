package com.trungquan.nongsan.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

@MappedSuperclass
@DynamicUpdate
@Getter
@Setter
public class AbstractBase implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_date")
    @JsonFormat(
        shape = JsonFormat.Shape.STRING,
        pattern = "hh:mm, dd-MM-yyyy"
    )
    protected Date createdAt;

    @Column(
        name = "updated_at",
        columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
    )
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "hh:mm, dd-MM-yyyy"
    )
    protected Date updatedAt;
    @CreatedBy
    @Column(
            name = "created_by",
            nullable = true
    )
    private String createdBy;
    @LastModifiedBy
    @Column(
            name = "updated_by"
    )
    private String updatedBy;
    @Column(
            name = "active_flag"
    )
    private Boolean activeFlag = true;
    @Column(
            name = "delete_flag"
    )
    private Boolean deleteFlag = false;


}
