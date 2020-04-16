package com.kodobit.RBAC_demo_STU_FIIT;

import androidx.annotation.NonNull;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class RegisterActivity extends Activity {

    EditText etName, etEmail, etPhone, etPassword;
    Button btnRegister;
    TextView textViewToLogin;
    ProgressBar progressBar;
    Switch Switch;
    LinearLayout layoutTeacher, layoutStudent;
    Boolean checked = false;

    //teacher
    EditText etTeacherNumber, etTeacherYears;
    //student
    EditText etStudentNumber, etStudentYears, etStudentDegree;

    String userID;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etPhone = findViewById(R.id.etPhone);
        btnRegister = findViewById(R.id.btnRegister);
        textViewToLogin = findViewById(R.id.textViewToLogin);
        progressBar = findViewById(R.id.progressBar);
        Switch = findViewById(R.id.Switch);

        layoutTeacher = findViewById(R.id.layoutTeacher);
        layoutStudent = findViewById(R.id.layoutStudent);
        etTeacherNumber = findViewById(R.id.etTeacherNumber);
        etTeacherYears = findViewById(R.id.etTeacherYears);
        etStudentNumber = findViewById(R.id.etStudentNumber);
        etStudentYears = findViewById(R.id.etStudentYears);
        etStudentDegree = findViewById(R.id.etStudentDegree);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        Switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    checked = true;
                    layoutStudent.setVisibility(View.INVISIBLE);
                    layoutTeacher.setVisibility(View.VISIBLE);
                } else {
                    checked = false;
                    layoutStudent.setVisibility(View.VISIBLE);
                    layoutTeacher.setVisibility(View.INVISIBLE);
                }
            }
        });

        if (firebaseAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finish();
        }

        textViewToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                final String name = etName.getText().toString();
                final String phone = etPhone.getText().toString();
                final String teacherNumber = etTeacherNumber.getText().toString();
                final String teacherYears = etTeacherYears.getText().toString();
                final String studentNumber = etStudentNumber.getText().toString();
                final String studentYears = etStudentYears.getText().toString();
                final String studentDegree = etStudentDegree.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    etEmail.setError("Email is required");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    etPassword.setError("Password is required");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                //register the user
                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "User Created", Toast.LENGTH_SHORT).show();
                            userID = firebaseAuth.getCurrentUser().getUid();

                            if (!checked) {
                                DocumentReference documentReference = firebaseFirestore.collection("stu_fiit").document(userID);
                                Map<String, Object> user = new HashMap<>();
                                user.put("ROLE","student");
                                user.put("Name", name);
                                user.put("Email", email);
                                user.put("Phone", phone);
                                user.put("StudentNumber", studentNumber);
                                user.put("StudentYears", studentYears);
                                user.put("StudentDegree", studentDegree);

                                documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "onSuccess: user profile is created for " + userID);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "onFailure: " + e.toString());
                                    }
                                });
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            } else {
                                DocumentReference documentReference = firebaseFirestore.collection("stu_fiit").document(userID);
                                Map<String, Object> user = new HashMap<>();
                                user.put("ROLE","teacher");
                                user.put("Name", name);
                                user.put("Email", email);
                                user.put("Phone", phone);
                                user.put("TeacherNumber", teacherNumber);
                                user.put("TeacherYears", teacherYears);
                                documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "onSuccess: user profile is created for " + userID);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "onFailure: " + e.toString());
                                    }
                                });
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });
    }
}
