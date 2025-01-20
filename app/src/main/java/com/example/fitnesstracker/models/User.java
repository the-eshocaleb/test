package com.example.fitnesstracker.models;

public class User {
    private String firstName;
    private String username;
    private String dateOfBirth;
    private String sex;
    private int height;
    private double weight;
    private double targetWeight;
    private String goal;
    private String activityLevel;
    private double progressRate;
    private String email;
    private String password;

    // Constructor
    public User() {}

    // Getters
    public String getFirstName() {
        return firstName;
    }

    public String getUsername() {
        return username;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getSex() {
        return sex;
    }

    public int getHeight() {
        return height;
    }

    public double getWeight() {
        return weight;
    }

    public double getTargetWeight() {
        return targetWeight;
    }

    public String getGoal() {
        return goal;
    }

    public String getActivityLevel() {
        return activityLevel;
    }

    public double getProgressRate() {
        return progressRate;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    // Setters
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public void setTargetWeight(double targetWeight) {
        this.targetWeight = targetWeight;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public void setActivityLevel(String activityLevel) {
        this.activityLevel = activityLevel;
    }

    public void setProgressRate(double progressRate) {
        this.progressRate = progressRate;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }
} 