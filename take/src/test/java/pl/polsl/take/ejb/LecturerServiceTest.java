package pl.polsl.take.ejb;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import pl.polsl.take.dto.LecturerDTO;
import pl.polsl.take.entity.Answer;
import pl.polsl.take.entity.Lecturer;
import pl.polsl.take.entity.Subject;
import pl.polsl.take.entity.Survey;
import pl.polsl.take.exceptions.EmailAlreadyInUseException;
import pl.polsl.take.exceptions.EmailNotFoundException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class LecturerServiceTest {

    @Mock
    private EntityManager em;

    @InjectMocks
    private LecturerService lecturerService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddLecturer_Success() {
        // Arrange
        LecturerDTO lecturerDTO = new LecturerDTO();
        lecturerDTO.setEmail("test@example.com");
        lecturerDTO.setFirstName("John");
        lecturerDTO.setLastName("Doe");
        lecturerDTO.setSubjectIds(Arrays.asList(1L, 2L));

        TypedQuery<Long> mockQuery = mock(TypedQuery.class);
        when(em.createQuery(anyString(), eq(Long.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter(anyString(), anyString())).thenReturn(mockQuery);
        when(mockQuery.getSingleResult()).thenReturn(0L);

        TypedQuery<Subject> mockSubjectQuery = mock(TypedQuery.class);
        when(em.createQuery(anyString(), eq(Subject.class))).thenReturn(mockSubjectQuery);
        when(mockSubjectQuery.setParameter(anyString(), anyCollection())).thenReturn(mockSubjectQuery);
        when(mockSubjectQuery.getResultList()).thenReturn(new ArrayList<>());

        // Act
        assertDoesNotThrow(() -> lecturerService.addLecturer(lecturerDTO));

        // Assert
        ArgumentCaptor<Lecturer> lecturerCaptor = ArgumentCaptor.forClass(Lecturer.class);
        verify(em).persist(lecturerCaptor.capture());
        assertEquals("John", lecturerCaptor.getValue().getFirstName());
        assertEquals("Doe", lecturerCaptor.getValue().getLastName());
        assertEquals("test@example.com", lecturerCaptor.getValue().getEmail());
    }

    @Test
    public void testAddLecturer_EmailAlreadyInUse() {
        // Arrange
        LecturerDTO lecturerDTO = new LecturerDTO();
        lecturerDTO.setEmail("test@example.com");
        lecturerDTO.setFirstName("John");
        lecturerDTO.setLastName("Doe");

        TypedQuery<Long> mockQuery = mock(TypedQuery.class);
        when(em.createQuery(anyString(), eq(Long.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter(anyString(), anyString())).thenReturn(mockQuery);
        when(mockQuery.getSingleResult()).thenReturn(1L);

        // Act & Assert
        EmailAlreadyInUseException exception = assertThrows(EmailAlreadyInUseException.class, () -> {
            lecturerService.addLecturer(lecturerDTO);
        });
        assertEquals("Email test@example.com is already in use.", exception.getMessage());
    }

    @Test
    public void testDeleteLecturerByEmail_Success() {
        // Arrange
        String email = "test@example.com";
        Lecturer lecturer = new Lecturer();
        lecturer.setLecturerId(1L);
        lecturer.setEmail(email);
        lecturer.setSubjects(new HashSet<>());

        TypedQuery<Lecturer> mockQuery = mock(TypedQuery.class);
        when(em.createQuery(anyString(), eq(Lecturer.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter(anyString(), eq(email))).thenReturn(mockQuery);
        when(mockQuery.getSingleResult()).thenReturn(lecturer);

        TypedQuery<Answer> mockAnswerQuery = mock(TypedQuery.class);
        when(em.createQuery(anyString(), eq(Answer.class))).thenReturn(mockAnswerQuery);
        when(mockAnswerQuery.setParameter(anyString(), any(Lecturer.class))).thenReturn(mockAnswerQuery);
        when(mockAnswerQuery.getResultList()).thenReturn(new ArrayList<>());

        TypedQuery<Survey> mockSurveyQuery = mock(TypedQuery.class);
        when(em.createQuery(anyString(), eq(Survey.class))).thenReturn(mockSurveyQuery);
        when(mockSurveyQuery.setParameter(anyString(), any(Lecturer.class))).thenReturn(mockSurveyQuery);
        when(mockSurveyQuery.getResultList()).thenReturn(new ArrayList<>());

        // Act
        assertDoesNotThrow(() -> lecturerService.deleteLecturerByEmail(email));

        // Assert
        verify(em).remove(lecturer);
    }

    @Test
    public void testDeleteLecturerByEmail_LecturerNotFound() {
        // Arrange
        String email = "test@example.com";

        TypedQuery<Lecturer> mockQuery = mock(TypedQuery.class);
        when(em.createQuery(anyString(), eq(Lecturer.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter(anyString(), eq(email))).thenReturn(mockQuery);
        when(mockQuery.getSingleResult()).thenThrow(new NoResultException());

        // Act & Assert
        EmailNotFoundException exception = assertThrows(EmailNotFoundException.class, () -> {
            lecturerService.deleteLecturerByEmail(email);
        });
        assertEquals("No lecturer found with the email: " + email, exception.getMessage());
    }

    @Test
    public void testUpdateLecturer_EmailAlreadyInUse() {
        // Arrange
        Long lecturerId = 1L;
        LecturerDTO lecturerDTO = new LecturerDTO();
        lecturerDTO.setEmail("new-email@example.com");

        Lecturer existingLecturer = new Lecturer();
        existingLecturer.setLecturerId(lecturerId);
        existingLecturer.setFirstName("OldName");
        existingLecturer.setLastName("OldLastName");
        existingLecturer.setEmail("old-email@example.com");

        when(em.find(Lecturer.class, lecturerId)).thenReturn(existingLecturer);

        TypedQuery<Long> mockQuery = mock(TypedQuery.class);
        when(em.createQuery(anyString(), eq(Long.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter(anyString(), anyString())).thenReturn(mockQuery);
        when(mockQuery.setParameter(anyString(), eq(lecturerId))).thenReturn(mockQuery);
        when(mockQuery.getSingleResult()).thenReturn(1L);

        // Act & Assert
        EmailAlreadyInUseException exception = assertThrows(EmailAlreadyInUseException.class, () -> {
            lecturerService.updateLecturer(lecturerId, lecturerDTO);
        });
        assertEquals("Email new-email@example.com is already in use.", exception.getMessage());
    }




}





