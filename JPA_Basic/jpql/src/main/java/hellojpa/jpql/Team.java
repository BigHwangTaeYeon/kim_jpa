package hellojpa.jpql;

import javax.persistence.*;
import java.util.List;

@Entity
public class Team {
    @Id @GeneratedValue
    @Column(name = "team_id")
    private Long id;
    private String name;
    @OneToMany(mappedBy = "team")
    private List<Member> member;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
