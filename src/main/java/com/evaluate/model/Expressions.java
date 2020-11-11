package com.evaluate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "Expressions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Expressions extends AbstractEntity<Expressions> {
    @Id
    @Column(nullable = false, name = "USER_EMAIL")
    private String userEmail;

    @Column(name = "ADD_COUNT")
    public int addCount;

    @Column(name = "SUBTRACT_COUNT")
    public int subtractCount;

    @Column(name = "MULTIPLY_COUNT")
    public int multiplyCount;

    @Column(name = "DIVIDE_COUNT")
    public int divideCount;
}
