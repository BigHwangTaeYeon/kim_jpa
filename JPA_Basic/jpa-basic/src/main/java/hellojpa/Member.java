package hellojpa;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
//@Table(name = "USER")
//위와 같은 설정으로 Table name 을 USER 로 지정할 수 있다.
public class Member {
    @Id
    private Long id;

//    @Column(name = "username")
//    위와 같은 설정으로 column 이름을 username 으로 지정할 수 있다.
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
