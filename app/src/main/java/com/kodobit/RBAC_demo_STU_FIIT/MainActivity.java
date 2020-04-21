package com.kodobit.RBAC_demo_STU_FIIT;

import androidx.annotation.Nullable;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Objects;

public class MainActivity extends Activity {

    TextView tvTitle, tvName, tvEmail, tvPhone, tvTeacherNumber, tvTeacherYears;
    TextView tvStudentNumber, tvStudentYears, tvStudentDegree;
    LinearLayout LayoutTeacherNumber, LayoutTeacherYears, LayoutStudentNumber, LayoutStudentYears, LayoutStudentDegree;
    LinearLayout layoutTeacher, layoutStudent;
    Button bntLogout;

    ImageView imageAvatar;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;

    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhone = findViewById(R.id.tvPhone);
        tvTeacherNumber = findViewById(R.id.tvTeacherNumber);
        tvTeacherYears = findViewById(R.id.tvTeacherYears);
        tvStudentNumber = findViewById(R.id.tvStudentNumber);
        tvStudentYears = findViewById(R.id.tvStudentYears);
        tvStudentDegree = findViewById(R.id.tvStudentDegree);
        imageAvatar = findViewById(R.id.imageAvatar);
        tvTitle = findViewById(R.id.tvTitle);
        LayoutTeacherNumber = findViewById(R.id.LayoutTeacherNumber);
        LayoutTeacherYears = findViewById(R.id.LayoutTeacherYears);
        LayoutStudentNumber = findViewById(R.id.LayoutStudentNumber);
        LayoutStudentYears = findViewById(R.id.LayoutStudentYears);
        LayoutStudentDegree = findViewById(R.id.LayoutStudentDegree);
        layoutTeacher = findViewById(R.id.layoutTeacher);
        layoutStudent = findViewById(R.id.layoutStudent);
        bntLogout=findViewById(R.id.btnLogout);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        bntLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut(); //logout
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        });

        userId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();

        DocumentReference documentReference = firebaseFirestore.collection("stu_fiit").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                assert documentSnapshot != null;
                String role = documentSnapshot.getString("ROLE");
                assert role != null;
                if (role.equals("teacher")) {
                    tvTitle.setText(role);
                    layoutTeacher.setVisibility(View.VISIBLE);
                    imageAvatar.setBackgroundResource(R.drawable.teacher_avatar);
                    tvName.setText(documentSnapshot.getString("Name"));
                    tvEmail.setText(documentSnapshot.getString("Email"));
                    tvPhone.setText(documentSnapshot.getString("Phone"));
                    LayoutTeacherNumber.setVisibility(View.VISIBLE);
                    LayoutTeacherYears.setVisibility(View.VISIBLE);
                    tvTeacherNumber.setText(documentSnapshot.getString("TeacherNumber"));
                    tvTeacherYears.setText(documentSnapshot.getString("TeacherYears"));
                } else {
                    tvTitle.setText(role);
                    layoutStudent.setVisibility(View.VISIBLE);
                    imageAvatar.setBackgroundResource(R.drawable.student_avatar);
                    tvName.setText(documentSnapshot.getString("Name"));
                    tvEmail.setText(documentSnapshot.getString("Email"));
                    tvPhone.setText(documentSnapshot.getString("Phone"));
                    LayoutStudentNumber.setVisibility(View.VISIBLE);
                    LayoutStudentYears.setVisibility(View.VISIBLE);
                    LayoutStudentDegree.setVisibility(View.VISIBLE);
                    tvStudentNumber.setText(documentSnapshot.getString("StudentNumber"));
                    tvStudentYears.setText(documentSnapshot.getString("StudentYears"));
                    tvStudentDegree.setText(documentSnapshot.getString("StudentDegree"));
                }
            }
        });
    }
}
