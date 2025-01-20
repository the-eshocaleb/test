package com.example.fitnesstracker.models;

public class UserMetrics {
    private int age;
    private double height; // in cm
    private double weight; // in kg
    private String sex;
    private String activityLevel;

    public UserMetrics(int age, double height, double weight, String sex, String activityLevel) {
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.sex = sex;
        this.activityLevel = activityLevel;
    }

    public double calculateBMR() {
        // Mifflin-St Jeor Equation
        if (sex.equalsIgnoreCase("male")) {
            return (10 * weight) + (6.25 * height) - (5 * age) + 5;
        } else {
            return (10 * weight) + (6.25 * height) - (5 * age) - 161;
        }
    }

    public double calculateTDEE() {
        double bmr = calculateBMR();
        double activityMultiplier;
        
        switch(activityLevel.toLowerCase()) {
            case "sedentary":
                activityMultiplier = 1.2;
                break;
            case "lightly active":
                activityMultiplier = 1.375;
                break;
            case "moderately active":
                activityMultiplier = 1.55;
                break;
            case "very active":
                activityMultiplier = 1.725;
                break;
            case "extra active":
                activityMultiplier = 1.9;
                break;
            default:
                activityMultiplier = 1.2;
        }
        
        return bmr * activityMultiplier;
    }

    public MacroTargets calculateMacroTargets() {
        double tdee = calculateTDEE();
        
        // Standard macro split (40% carbs, 30% protein, 30% fat)
        double carbsCalories = tdee * 0.4;
        double proteinCalories = tdee * 0.3;
        double fatCalories = tdee * 0.3;

        // Convert to grams
        int carbsGrams = (int) (carbsCalories / 4); // 4 calories per gram of carbs
        int proteinGrams = (int) (proteinCalories / 4); // 4 calories per gram of protein
        int fatGrams = (int) (fatCalories / 9); // 9 calories per gram of fat

        return new MacroTargets(carbsGrams, proteinGrams, fatGrams, (int) tdee);
    }
}