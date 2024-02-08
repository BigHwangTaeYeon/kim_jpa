package hellojpa;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Member extends BaseEntity{
    @Id @GeneratedValue
    private Long id;

    private String userName;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn
    private Team team;

    public Member() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
