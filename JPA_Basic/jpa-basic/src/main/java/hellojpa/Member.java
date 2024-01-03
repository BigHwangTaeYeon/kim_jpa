package hellojpa;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Entity // (name="Member") 다른 패키지에 같은 이름의 Class가 있을 때 보통 사용
//@Table(name = "USER")
//위와 같은 설정으로 Table name 을 USER 로 지정할 수 있다.
public class Member {
    @Id
    private Long id;
//    @Column(name = "username")
//    위와 같은 설정으로 column 이름을 username 으로 지정할 수 있다.
    @Column(name = "name")
    private String username;

    private Integer age;

    @Enumerated(EnumType.STRING)
    private RoleType roleType;

    @Temporal(TemporalType.TIMESTAMP)   // 날짜, 시간, 날짜시간 데이터베이스에는 구분해서 줘야한다.
    private Date createdDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModifiedDate;

    private LocalDate createDate2;
    private LocalDateTime lastModifiedDate2;

    @Lob
    private  String description;

    @Transient  // DB 상관없이 어플리케이션 메모리에서만 사용하겠다 선언.
    private int temp;

    public Member(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public RoleType getRoleType() {
        return roleType;
    }

    public void setRoleType(RoleType roleType) {
        this.roleType = roleType;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getTemp() {
        return temp;
    }

    public void setTemp(int temp) {
        this.temp = temp;
    }
}
