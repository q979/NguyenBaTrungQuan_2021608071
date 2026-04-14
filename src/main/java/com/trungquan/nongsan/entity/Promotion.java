package com.trungquan.nongsan.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "promotions")
public class Promotion extends AbstractBase{

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "start_date")
    private Date startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "end_date")
    private Date endDate;

    @Column(name = "image")
    private String image;

    @Column(name = "discount_percentage")
    private Double discountPercentage;

    @JsonIgnore
    @OneToMany(mappedBy = "promotion")
    private List<Product> products = new ArrayList<>();

}
