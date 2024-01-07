package hellojpa;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Team {
    @Id
    @GeneratedValue
    @Column(name="team_id")
    private Long id;

    private String name;

    @OneToMany(mappedBy = "team")   //Member Entity의 team 변수명에 맞춰줄 것이다라는 뜻
    private List<Member> members = new ArrayList<>();

    public void addMember(Member member){
        member.setTeam(this);
        members.add(member);
    }

    public Team() {
    }

    public List<Member> getMembers() {
        return members;
    }

    public void setMembers(List<Member> members) {
        this.members = members;
    }

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
