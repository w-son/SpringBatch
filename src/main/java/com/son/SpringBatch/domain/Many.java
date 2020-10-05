package com.son.SpringBatch.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
@NoArgsConstructor
public class Many {


    @Column(name = "many_id")
    @Id @GeneratedValue
    private Long id;

    private String name;

    private int number;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "one_id")
    private One one;


    public Many(String name, int number) {
        this.name = name;
        this.number = number;
    }

    public void updateOne(One one) {
        this.one = one;
    }


}
