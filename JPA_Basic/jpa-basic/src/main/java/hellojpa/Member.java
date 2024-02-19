package hellojpa;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class Member extends BaseEntity{
    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    @Column(name = "user_name")
    private String userName;

//    @Embedded
//    private Period workPeriod;

    @Embedded
    private Adress homeAdress;

    @Embedded
    @AttributeOverrides(
            value = {@AttributeOverride(name="city", column=@Column(name = "work_city")),
            @AttributeOverride(name="street", column=@Column(name = "work_street")),
            @AttributeOverride(name="zipcode", column=@Column(name = "work_zipcode"))}
    )
    private Adress workAdress;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "favorit_food", joinColumns =
        @JoinColumn(name = "member_id")
    )
    @Column(name = "food_name") // 하나이기 때문에 가능
    private Set<String> favoritFoods = new HashSet<>();

//    @ElementCollection(fetch = FetchType.LAZY)
//    @CollectionTable(name = "address", joinColumns =
//        @JoinColumn(name = "member_id")
//    )
//    private List<Adress> adressHistory = new ArrayList<>();

    // 1:N
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "member_id")
    private List<AddressEntity> addressHistory = new ArrayList<>();

    public Member() {
    }

    public List<AddressEntity> getAddressHistory() {
        return addressHistory;
    }

    public void setAddressHistory(List<AddressEntity> addressHistory) {
        this.addressHistory = addressHistory;
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

    public Adress getWorkAdress() {
        return workAdress;
    }

    public void setWorkAdress(Adress workAdress) {
        this.workAdress = workAdress;
    }

    public Set<String> getFavoritFoods() {
        return favoritFoods;
    }

    public void setFavoritFoods(Set<String> favoritFoods) {
        this.favoritFoods = favoritFoods;
    }

    public Adress getHomeAdress() {
        return homeAdress;
    }

    public void setHomeAdress(Adress homeAdress) {
        this.homeAdress = homeAdress;
    }
}
