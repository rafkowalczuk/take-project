package pl.polsl.take.ejb;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pl.polsl.take.dto.StudentDTO;
import pl.polsl.take.entity.Student;
import pl.polsl.take.exceptions.EmailAlreadyInUseException;
import pl.polsl.take.exceptions.EmailNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class StudentServiceTest {

    @Mock
    private EntityManager em;

    @InjectMocks
    private StudentService studentService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddStudent_Success() {

        StudentDTO studentDTO = new StudentDTO();
        studentDTO.setEmail("Testowaniezaleznosci@com.xd");
        studentDTO.setFirstName("Rafal");
        studentDTO.setLastName("Student");

        TypedQuery<Long> mockQuery = mock(TypedQuery.class);
        when(em.createQuery(anyString(), eq(Long.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter(anyString(), anyString())).thenReturn(mockQuery);
        when(mockQuery.getSingleResult()).thenReturn(0L);


        assertDoesNotThrow(() -> studentService.addStudent(studentDTO));


        ArgumentCaptor<Student> studentCaptor = ArgumentCaptor.forClass(Student.class);
        verify(em).persist(studentCaptor.capture());
        assertEquals("Rafal", studentCaptor.getValue().getFirstName());
        assertEquals("Student", studentCaptor.getValue().getLastName());
        assertEquals("Testowaniezaleznosci@com.xd", studentCaptor.getValue().getEmail());
    }

    @Test
    public void testAddStudent_EmailAlreadyInUse() {
        // Arrange
        StudentDTO studentDTO = new StudentDTO();
        studentDTO.setEmail("Testowaniezaleznosci@com.xd");
        studentDTO.setFirstName("Rafal");
        studentDTO.setLastName("Student");

        TypedQuery<Long> mockQuery = mock(TypedQuery.class);
        when(em.createQuery(anyString(), eq(Long.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter(anyString(), anyString())).thenReturn(mockQuery);
        when(mockQuery.getSingleResult()).thenReturn(1L);

        // Act & Assert
        EmailAlreadyInUseException exception = assertThrows(EmailAlreadyInUseException.class, () -> {
            studentService.addStudent(studentDTO);
        });
        assertEquals("Email Testowaniezaleznosci@com.xd is already in use.", exception.getMessage());
    }

    @Test
    public void testAddStudent_InvalidEmail() {
        // Arrange
        StudentDTO studentDTO = new StudentDTO();
        studentDTO.setEmail("invalid-email");
        studentDTO.setFirstName("Rafal");
        studentDTO.setLastName("Student");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            studentService.addStudent(studentDTO);
        });
        assertEquals("Invalid email: invalid-email", exception.getMessage());
    }

    @Test
    public void testDeleteStudentByEmail_EmailNotFound() {
        // Arrange
        String email = "nonexistent@example.com";

        TypedQuery<Student> mockQuery = mock(TypedQuery.class);
        when(em.createQuery(anyString(), eq(Student.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter(anyString(), eq(email))).thenReturn(mockQuery);
        when(mockQuery.getSingleResult()).thenThrow(new NoResultException());

        // Act & Assert
        EmailNotFoundException exception = assertThrows(EmailNotFoundException.class, () -> {
            studentService.deleteStudentByEmail(email);
        });
        assertEquals("No student found with the email: " + email, exception.getMessage());
    }


}


