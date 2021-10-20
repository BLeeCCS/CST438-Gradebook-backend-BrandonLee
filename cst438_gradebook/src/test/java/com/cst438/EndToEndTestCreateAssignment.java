package com.cst438;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.cst438.domain.Assignment;
import com.cst438.domain.AssignmentGrade;
import com.cst438.domain.AssignmentGradeRepository;
import com.cst438.domain.AssignmentRepository;
import com.cst438.domain.Course;
import com.cst438.domain.CourseRepository;
import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentRepository;

@SpringBootTest
public class EndToEndTestCreateNewAssignment
{
   public static final String CHROME_DRIVER_FILE_LOCATION = "C:/chromedriver_win32/chromedriver.exe";

   public static final String URL = "https://cst438grade-fe-blee.herokuapp.com/";
   public static final String TEST_USER_EMAIL = "test@csumb.edu";
   public static final String TEST_INSTRUCTOR_EMAIL = "dwisneski@csumb.edu";
   public static final int SLEEP_DURATION = 1000; 
   public static final int TEST_COURSE_ID = 112233;

   @Autowired
   EnrollmentRepository enrollmentRepository;

   @Autowired
   CourseRepository courseRepository;

   @Autowired
   AssignmentGradeRepository assignnmentGradeRepository;

   @Autowired
   AssignmentRepository assignmentRepository;

   @Test
   public void createNewAssignment() throws Exception {
      
    //Create course      
    Course c = new Course();
    c.setCourse_id(TEST_COURSE_ID);
    c.setInstructor(TEST_INSTRUCTOR_EMAIL);
    c.setSemester("Fall");
    c.setYear(2021);
    c.setTitle("Test Course");

    Assignment a = new Assignment();
    a.setCourse(c);

    a.setDueDate(new java.sql.Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000));
    a.setName("TEST ASSIGNMENT");
    a.setNeedsGrading(1);

    Enrollment e = new Enrollment();
    e.setCourse(c);
    e.setStudentEmail(TEST_USER_EMAIL);
    e.setStudentName("Test");

    a = assignmentRepository.save(a);

    AssignmentGrade ag = null;

    System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_FILE_LOCATION);
    WebDriver driver = new ChromeDriver();
    driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

    driver.get(URL);
    Thread.sleep(SLEEP_DURATION);

      try {      
        WebElement we = driver.findElement(By.id("App"));
        we.findElement(By.id("assignmentName")).sendKeys("Assignment_1");
        we.findElement(By.id("dueDate")).sendKeys("10/12/21");
        we.findElement(By.id("courseId")).sendKeys("112233");
         
        driver.findElement(By.id("buttonSubmit")).click();
        Thread.sleep(SLEEP_DURATION);

        we = driver.findElement(By.xpath("//div[@data-value='Assignment_1']"));
        assertEquals("Assignment_1", we.getAttribute("data-value"));
    
        List<Assignment> assigns = assignmentRepository.findNeedGradingByEmail("dwisneski@csumb.edu");
        for(Assignment assign: assigns) {
        if(assign.getName() != "Assignment_1") {
            continue;
        }
        else if(assign.getName() == "Assignment_1") {
            assertEquals("Assignment_1", assign.getName());
            break;
        }

        assertEquals("Assignment_1", assign.getName());
        }
        
    } catch (Exception ex) {
        throw ex;
    } finally {

        grade = assignnmentGradeRepository.findByAssignmentIdAndStudentEmail(a.getId(), TEST_USER_EMAIL);
        if (grade!=null) assignnmentGradeRepository.delete(grade);
        enrollmentRepository.delete(e);
        assignmentRepository.delete(a);
        courseRepository.delete(c);

        driver.quit();
    }
}
   
}