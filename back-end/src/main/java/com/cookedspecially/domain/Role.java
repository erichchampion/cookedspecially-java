package com.cookedspecially.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;


/**
 * @author abhishek
 *
 */
@Entity
@Table(name="ROLES")
public class Role implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
 @GeneratedValue
 private Integer id;
 
 @Column(name="role")
 private String role;
 
 @OneToMany(cascade=CascadeType.ALL)
 @JoinTable(name="USER_ROLES",
  joinColumns={@JoinColumn(name="roleId", referencedColumnName="id")},
  inverseJoinColumns={@JoinColumn(name="userId", referencedColumnName="userId")})
 private List<User> userList;

public Integer getId() {
	return id;
}

public void setId(Integer id) {
	this.id = id;
}

public String getRole() {
	return role;
}

public void setRole(String role) {
	this.role = role;
}

public List<User> getUserList() {
	return userList;
}

public void setUserList(List<User> userList) {
	this.userList = userList;
}
 
 
 
}
