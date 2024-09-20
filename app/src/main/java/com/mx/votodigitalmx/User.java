package com.mx.votodigitalmx;

public class User {
    private String name;
    private String lastName;
    private String email;
    private String password;
    private String address; // Corrige 'adress' a 'address'
    private String gender;
    private String idNumber;

    // Constructor vacío requerido para Firestore
    public User() {
    }

    // Constructor con parámetros
    public User(String name, String lastName, String email, String gender, String idNumber, String password, String address) {
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.gender = gender;
        this.idNumber = idNumber;
        this.password = password;
        this.address = address; // Corrige 'adress' a 'address'
    }

    // Getters y setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String newPassword) {
        this.password = newPassword;
    }

    public String getAddress() { // Corrige 'getAdress' a 'getAddress'
        return address;
    }

    public void setAddress(String newAddress) { // Corrige 'setAdress' a 'setAddress'
        this.address = newAddress;
    }
}
