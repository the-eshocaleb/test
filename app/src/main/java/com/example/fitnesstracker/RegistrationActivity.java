package com.example.fitnesstracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.fitnesstracker.helpers.DatabaseHelper;
import com.example.fitnesstracker.models.User;

public class RegistrationActivity extends AppCompatActivity {
    private EditText firstNameInput, usernameInput, dateOfBirthInput, heightInput, 
                     weightInput, targetWeightInput, emailInput, passwordInput;
    private RadioGroup sexGroup;
    private Spinner goalSpinner, activitySpinner, progressRateSpinner;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // Initialize database helper
        dbHelper = new DatabaseHelper(this);

        // Initialize views
        initializeViews();
        setupSpinners();

        Button registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(v -> attemptRegistration());
    }

    private void initializeViews() {
        firstNameInput = findViewById(R.id.firstname);
        usernameInput = findViewById(R.id.username);
        dateOfBirthInput = findViewById(R.id.dateOfBirth);
        heightInput = findViewById(R.id.height);
        weightInput = findViewById(R.id.weight);
        targetWeightInput = findViewById(R.id.targetWeight);
        emailInput = findViewById(R.id.email);
        passwordInput = findViewById(R.id.password);
        sexGroup = findViewById(R.id.sexGroup);
        goalSpinner = findViewById(R.id.goal);
        activitySpinner = findViewById(R.id.activityLevel);
        progressRateSpinner = findViewById(R.id.progressRate);
    }

    private void setupSpinners() {
        // Setup spinners
        //
        ArrayAdapter<CharSequence> goalAdapter = ArrayAdapter.createFromResource(this,
                R.array.goals, android.R.layout.simple_spinner_item);
        goalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        goalSpinner.setAdapter(goalAdapter);

        ArrayAdapter<CharSequence> activityAdapter = ArrayAdapter.createFromResource(this,
                R.array.activity_levels, android.R.layout.simple_spinner_item);
        activityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        activitySpinner.setAdapter(activityAdapter);

        ArrayAdapter<CharSequence> progressRateAdapter = ArrayAdapter.createFromResource(this,
                R.array.progress_rate, android.R.layout.simple_spinner_item);
        progressRateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        progressRateSpinner.setAdapter(progressRateAdapter);
    }

    private void attemptRegistration() {
        // Reset errors
        firstNameInput.setError(null);
        emailInput.setError(null);
        passwordInput.setError(null);

        // Get values
        String firstName = firstNameInput.getText().toString().trim();
        String username = usernameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Validate the fields
        if (TextUtils.isEmpty(firstName)) {
            firstNameInput.setError("This field is required");
            focusView = firstNameInput;
            cancel = true;
        }

        if (TextUtils.isEmpty(email)) {
            emailInput.setError("This field is required");
            focusView = emailInput;
            cancel = true;
        } else if (!isEmailValid(email)) {
            emailInput.setError("Invalid email address");
            focusView = emailInput;
            cancel = true;
        }

        if (TextUtils.isEmpty(password)) {
            passwordInput.setError("This field is required");
            focusView = passwordInput;
            cancel = true;
        } else if (!isPasswordValid(password)) {
            passwordInput.setError("Password must be at least 6 characters");
            focusView = passwordInput;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            registerUser();
        }
    }

    private boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 6;
    }

    private void registerUser() {
        User user = new User();
        user.setFirstName(firstNameInput.getText().toString().trim());
        user.setUsername(usernameInput.getText().toString().trim());
        user.setDateOfBirth(dateOfBirthInput.getText().toString().trim());
        user.setSex(sexGroup.getCheckedRadioButtonId() == R.id.male ? "Male" : "Female");
        user.setHeight(Integer.parseInt(heightInput.getText().toString().trim()));
        user.setWeight(Double.parseDouble(weightInput.getText().toString().trim()));
        user.setTargetWeight(Double.parseDouble(targetWeightInput.getText().toString().trim()));
        user.setGoal(goalSpinner.getSelectedItem().toString());
        user.setActivityLevel(activitySpinner.getSelectedItem().toString());
        user.setProgressRate(Double.parseDouble(progressRateSpinner.getSelectedItem().toString().split(" ")[0]));
        user.setEmail(emailInput.getText().toString().trim());
        user.setPassword(passwordInput.getText().toString());

        try {
            long userId = dbHelper.insertUser(user);
            if (userId != -1) {
                // Set logged in state
                SharedPreferences prefs = getSharedPreferences("FitnessTrackerPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("isLoggedIn", true);
                editor.apply();
        
                // Registration successful
                Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
                // Navigate to Home Activity
                Intent intent = new Intent(RegistrationActivity.this, HomeActivity.class);
                startActivity(intent);
                finish(); // Prevent going back to registration
            } else {
                Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
