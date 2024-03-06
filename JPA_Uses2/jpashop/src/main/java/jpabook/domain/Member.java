package jpabook.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Member {
    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    @NotEmpty // Controller에 @Valid에서 검증을 진행해준다
    private String name;

    @Embedded
    private Address address;

    @JsonIgnore // 외부 노출에서 제외하고 싶다. (memberV1 에서 바디로 데이터를 받아보면 빠져있다.)
    @OneToMany(mappedBy = "member") // order table 에 있는 member 에 의해 매핑됬을 뿐이야 읽기 전용이 될 뿐이야
    private List<Order> orders = new ArrayList<>();
}
