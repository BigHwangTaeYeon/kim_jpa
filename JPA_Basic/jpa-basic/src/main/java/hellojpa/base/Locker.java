package hellojpa.base;

import javax.persistence.*;

//@Entity
public class Locker {
    @Id @GeneratedValue
    private Long id;

    private String name;

    @OneToOne
    @JoinColumn(name = "member_id")
    private Member member;

}
