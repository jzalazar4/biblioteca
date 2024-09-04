package com.example.biblioteca.dto;

import com.example.biblioteca.entity.Role;
import com.example.biblioteca.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class UserDTO {

  
    @Getter  @Setter
    private String nom;
    @Getter  @Setter
    private String ape;
    @Getter  @Setter
    private String useremail;
    @Getter  @Setter
    private String userpassword;
    @Getter  @Setter
    private String role;
    @Getter  @Setter
    private Long tel;


}
