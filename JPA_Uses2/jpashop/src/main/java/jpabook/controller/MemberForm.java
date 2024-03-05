package jpabook.controller;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MemberForm {

    @NotEmpty(message = "회원 이름은 필수 입니다.")
    // validation.constraints.NotEmpty 필수값으로 받을 변수
    private String name;
    private String city;
    private String street;
    private String zipcode;
}
