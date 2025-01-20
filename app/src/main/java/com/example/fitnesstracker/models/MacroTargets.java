package com.example.fitnesstracker.models;

public class MacroTargets {
    private int carbsTarget;
    private int proteinTarget;
    private int fatTarget;
    private int calorieTarget;

    public MacroTargets(int carbsTarget, int proteinTarget, int fatTarget, int calorieTarget) {
        this.carbsTarget = carbsTarget;
        this.proteinTarget = proteinTarget;
        this.fatTarget = fatTarget;
        this.calorieTarget = calorieTarget;
    }

    // Getters
    public int getCarbsTarget() { return carbsTarget; }
    public int getProteinTarget() { return proteinTarget; }
    public int getFatTarget() { return fatTarget; }
    public int getCalorieTarget() { return calorieTarget; }
} 