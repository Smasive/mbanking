package com.example.mobilebanking;

public class User {
    public String firstName;
    public String lastName;
    public String idNo;
    public String email;
    public String tel;
    public String password;
    public String level;

    public String accNo;

    public void setEmail(String tel) {
        this.tel = tel;
    }

    public void setIdNo(String idNo) {
        this.idNo = idNo;
    }

    public void setTel(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public User(){

    }

    public User (String firstName, String lastName, String idNo, String email, String tel, String password, String level, String accNo){
        this.firstName = firstName;
        this.lastName = lastName;
        this.idNo = idNo;
        this.tel = tel;
        this.email = email;
        this.password = password;
        this.level = level;
        this.accNo = accNo;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getIdNo() {
        return idNo;
    }

    public String getTel() {
        return tel;
    }

    public String getEmail() {
        return email;
    }

    public String getLevel(){return level;}
    public String getAccNo(){return accNo;}


}

