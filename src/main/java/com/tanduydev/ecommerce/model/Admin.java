package com.tanduydev.ecommerce.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "\"admins\"")
@Getter
@Setter
@PrimaryKeyJoinColumn(name = "user_id")
public class Admin extends User {

@Column(nullable = false)
    private String department;
}