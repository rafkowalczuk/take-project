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
import pl.polsl.take.dto.SimpleSubjectDTO;
import pl.polsl.take.dto.SubjectProfileDTO;
import pl.polsl.take.entity.Answer;
import pl.polsl.take.entity.Subject;
import pl.polsl.take.entity.Survey;
import pl.polsl.take.exceptions.SubjectAlreadyExistsException;
import pl.polsl.take.exceptions.SubjectNotFoundException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class SubjectServiceTest {
    @Mock
    private EntityManager em;

    @InjectMocks
    private SubjectService subjectService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllSubjects() {
        // Arrange
        List<Subject> subjects = new ArrayList<>();
        subjects.add(new Subject(1L, "Math"));
        subjects.add(new Subject(2L, "Physics"));

        TypedQuery<Subject> mockQuery = mock(TypedQuery.class);
        when(em.createQuery(anyString(), eq(Subject.class))).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(subjects);

        // Act
        List<SimpleSubjectDTO> result = subjectService.getAllSubjects();

        // Assert
        assertEquals(2, result.size());
        assertEquals("Math", result.get(0).getName());
        assertEquals("Physics", result.get(1).getName());
    }

    @Test
    public void testAddSubject_Success() {
        // Arrange
        SimpleSubjectDTO subjectDTO = new SimpleSubjectDTO();
        subjectDTO.setName("Chemistry");

        TypedQuery<Long> mockQuery = mock(TypedQuery.class);
        when(em.createQuery(anyString(), eq(Long.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter(anyString(), anyString())).thenReturn(mockQuery);
        when(mockQuery.getSingleResult()).thenReturn(0L);

        // Act
        assertDoesNotThrow(() -> subjectService.addSubject(subjectDTO));

        // Assert
        ArgumentCaptor<Subject> subjectCaptor = ArgumentCaptor.forClass(Subject.class);
        verify(em).persist(subjectCaptor.capture());
        assertEquals("Chemistry", subjectCaptor.getValue().getName());
    }

    @Test
    public void testAddSubject_SubjectAlreadyExists() {
        // Arrange
        SimpleSubjectDTO subjectDTO = new SimpleSubjectDTO();
        subjectDTO.setName("Chemistry");

        TypedQuery<Long> mockQuery = mock(TypedQuery.class);
        when(em.createQuery(anyString(), eq(Long.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter(anyString(), anyString())).thenReturn(mockQuery);
        when(mockQuery.getSingleResult()).thenReturn(1L);

        // Act & Assert
        SubjectAlreadyExistsException exception = assertThrows(SubjectAlreadyExistsException.class, () -> {
            subjectService.addSubject(subjectDTO);
        });
        assertEquals("Subject with name Chemistry already exists.", exception.getMessage());
    }

    @Test
    public void testUpdateSubject_Success() {
        // Arrange
        Long subjectId = 1L;
        SimpleSubjectDTO subjectDTO = new SimpleSubjectDTO();
        subjectDTO.setName("Advanced Chemistry");

        Subject existingSubject = new Subject(subjectId, "Chemistry");

        when(em.find(Subject.class, subjectId)).thenReturn(existingSubject);

        TypedQuery<Long> mockQuery = mock(TypedQuery.class);
        when(em.createQuery(anyString(), eq(Long.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter(anyString(), anyString())).thenReturn(mockQuery);
        when(mockQuery.setParameter(anyString(), eq(subjectId))).thenReturn(mockQuery);
        when(mockQuery.getSingleResult()).thenReturn(0L);

        // Act
        assertDoesNotThrow(() -> subjectService.updateSubject(subjectId, subjectDTO));

        // Assert
        verify(em).merge(existingSubject);
        assertEquals("Advanced Chemistry", existingSubject.getName());
    }





    @Test
    public void testDeleteSubjectByName_Success() {

        String name = "Math";
        Subject subject = new Subject(1L, name);

        TypedQuery<Subject> mockQuery = mock(TypedQuery.class);
        when(em.createQuery(anyString(), eq(Subject.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter(anyString(), eq(name))).thenReturn(mockQuery);
        when(mockQuery.getSingleResult()).thenReturn(subject);

        TypedQuery<Answer> mockAnswerQuery = mock(TypedQuery.class);
        when(em.createQuery(anyString(), eq(Answer.class))).thenReturn(mockAnswerQuery);
        when(mockAnswerQuery.setParameter(anyString(), any(Subject.class))).thenReturn(mockAnswerQuery);
        when(mockAnswerQuery.getResultList()).thenReturn(new ArrayList<>());

        TypedQuery<Survey> mockSurveyQuery = mock(TypedQuery.class);
        when(em.createQuery(anyString(), eq(Survey.class))).thenReturn(mockSurveyQuery);
        when(mockSurveyQuery.setParameter(anyString(), any(Subject.class))).thenReturn(mockSurveyQuery);
        when(mockSurveyQuery.getResultList()).thenReturn(new ArrayList<>());

        // Act
        assertDoesNotThrow(() -> subjectService.deleteSubjectByName(name));

        // Assert
        verify(em).remove(subject);
    }

    @Test
    public void testDeleteSubjectByName_SubjectNotFound() {
        // Arrange
        String name = "Math";

        TypedQuery<Subject> mockQuery = mock(TypedQuery.class);
        when(em.createQuery(anyString(), eq(Subject.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter(anyString(), eq(name))).thenReturn(mockQuery);
        when(mockQuery.getSingleResult()).thenThrow(new NoResultException());

        // Act & Assert
        SubjectNotFoundException exception = assertThrows(SubjectNotFoundException.class, () -> {
            subjectService.deleteSubjectByName(name);
        });
        assertEquals("No subject found with the name: " + name, exception.getMessage());
    }

}