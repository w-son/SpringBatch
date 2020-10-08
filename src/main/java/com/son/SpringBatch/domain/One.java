package com.son.SpringBatch.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor
public class One {


    @Column(name = "one_id")
    @Id @GeneratedValue
    private Long id;

    private String name;

    private int number;

    @JsonIgnore
    @OneToMany(mappedBy = "one", cascade = CascadeType.ALL)
    private List<Many> manyList = new ArrayList<>();


    public One(String name, int number) {
        this.name = name;
        this.number = number;
    }

    public void addMany(Many many) {
        this.manyList.add(many);
    }


}
