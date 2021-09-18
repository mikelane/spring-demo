package io.mikelane.demo.student;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class StudentService {
  private final StudentRepository studentRepository;

  @Autowired
  public StudentService(StudentRepository studentRepository) {
    this.studentRepository = studentRepository;
  }

  public List<Student> getStudents() {
    return studentRepository.findAll();
  }

  public void addNewStudent(Student student) {
    Optional<Student> optionalStudent = studentRepository.findStudentByEmail(student.getEmail());
    if (optionalStudent.isPresent()) {
      throw new IllegalStateException("email taken");
    }
    studentRepository.save(student);
  }

  public void deleteStudent(Long studentId) {
    boolean studentExists = studentRepository.existsById(studentId);
    if (!studentExists) {
      throw new IllegalStateException("student with id " + studentId + " does not exist");
    }

    studentRepository.deleteById(studentId);
  }

  @Transactional
  public void updateStudent(Long studentId, String name, String email) {
    Student student = studentRepository.findById(studentId).orElseThrow(
        () -> new IllegalStateException("student with id " + studentId + " does not exist")
    );

    if (name != null && name.length() > 0 && student.getName().equalsIgnoreCase(name)) {
      student.setName(name);
    }

    if (email != null && email.length() > 0 && !Objects.equals(student.getEmail(), email)) {
      studentRepository.findStudentByEmail(email).ifPresent(s -> {
        throw new IllegalStateException("email exists");
      });
      student.setEmail(email);
    }

  }
}
