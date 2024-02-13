package hellojpa;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
public class Member extends BaseEntity{
    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    @Column(name = "user_name")
    private String userName;

    // 기간 period
//    private LocalDateTime startDate;
//    private LocalDateTime endDate;
    @Embedded
    private Period workPeriod;

    // 주소
//    private String city;
//    private String street;
//    private String zipcode;
    @Embedded
    private Adress homeAdress;

    @Embedded
    @AttributeOverrides(
            value = {@AttributeOverride(name="city", column=@Column(name = "work_city")),
            @AttributeOverride(name="street", column=@Column(name = "work_street")),
            @AttributeOverride(name="zipcode", column=@Column(name = "work_zipcode"))}
    )
    private Adress workAdress;

    public Member() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Period getWorkPeriod() {
        return workPeriod;
    }

    public void setWorkPeriod(Period workPeriod) {
        this.workPeriod = workPeriod;
    }

    public Adress getHomeAdress() {
        return homeAdress;
    }

    public void setHomeAdress(Adress homeAdress) {
        this.homeAdress = homeAdress;
    }
}
