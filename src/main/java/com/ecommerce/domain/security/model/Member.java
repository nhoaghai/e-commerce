package com.ecommerce.domain.security.model;

import com.ecommerce.domain.member.model.Address;
import com.ecommerce.domain.member.model.Seller;
import com.ecommerce.domain.order.model.Order;
import com.ecommerce.domain.shoppingCart.model.ShoppingCart;
import com.ecommerce.domain.shoppingCart.model.WishList;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "member")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "member_id")
    private String memberId;

    @Column(name = "email")
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "password")
    private String password;

    @Column(name = "birthday")
    private LocalDate birthday;

    @Column(name = "create_at")
    private LocalDateTime createAt;

    @Column(name = "update_at")
    private LocalDateTime updateAt;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "gender")
    private boolean gender;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "is_active", columnDefinition = "boolean default true")
    private boolean isActive;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Address> addressList;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "member_role",
            joinColumns = @JoinColumn(name = "member_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<ShoppingCart> shoppingCarts;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<WishList> wishLists;

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL)
    private Seller seller;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Order> order;

    @OneToMany(mappedBy = "member")
    private List<Token> tokens;

    @Transient
    private String fullName;

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
