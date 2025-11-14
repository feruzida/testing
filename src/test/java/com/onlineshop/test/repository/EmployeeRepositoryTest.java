package com.onlineshop.test.repository;

import com.onlineshop.test.entity.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EmployeeRepositoryTest {

    @Autowired
    EmployeeRepository employeeRepository;

    private Employee savedEmployee;

    @BeforeEach
    void setUp() {
        employeeRepository.deleteAll();

        Employee employee = new Employee();
        employee.setName("Islam Makhachev");
        employee.setSalary(500000L);

        savedEmployee = employeeRepository.save(employee);
    }

    @Test
    void saveEmployee_ShouldSaveEmployeeSuccessfully() {
        Employee employee = new Employee();
        employee.setName("Kamaru Usman");
        employee.setSalary(300000L);

        Employee saved = employeeRepository.save(employee);
        assertThat(saved.getId()).isNotNull();
    }

    @Test
    void findById_ShouldReturnEmployee_WhenEmployeeExists() {
        Optional<Employee> foundEmployee = employeeRepository.findById(savedEmployee.getId());

        assertThat(foundEmployee).isPresent();
        assertThat(foundEmployee.get().getName()).isEqualTo("Islam Makhachev");
    }

    @Test
    void findById_ShouldReturnEmpty_WhenEmployeeDoesNotExist() {
        Optional<Employee> foundEmployee = employeeRepository.findById(999L);
        assertThat(foundEmployee).isNotPresent();
    }

    @Test
    void findAll_ShouldReturnListOfEmployees() {
        List<Employee> employees = employeeRepository.findAll();
        assertThat(employees).hasSize(1);
    }

    @Test
    void deleteEmployee_ShouldDeleteEmployeeSuccessfully() {
        employeeRepository.delete(savedEmployee);

        Optional<Employee> result = employeeRepository.findById(savedEmployee.getId());
        assertThat(result).isNotPresent();
    }

    @Test
    void updateEmployee_ShouldUpdateEmployeeFieldsSuccessfully() {
        savedEmployee.setName("Alexander Volkanovski");
        employeeRepository.save(savedEmployee);

        Optional<Employee> updatedEmployee = employeeRepository.findById(savedEmployee.getId());
        assertThat(updatedEmployee.get().getName()).isEqualTo("Alexander Volkanovski");
    }

    @Test
    void existsById_ShouldReturnTrue_WhenEmployeeExists() {
        boolean exists = employeeRepository.existsById(savedEmployee.getId());
        assertThat(exists).isTrue();
    }

    @Test
    void count_ShouldReturnCorrectNumberOfEmployees() {
        long count = employeeRepository.count();
        assertThat(count).isEqualTo(1);
    }

    @Test
    void saveMultipleEmployees_ShouldIncreaseEmployeeCount() {
        Employee employee1 = new Employee();
        employee1.setName("Conor McGregor");
        employee1.setSalary(900000L);

        Employee employee2 = new Employee();
        employee2.setName("Dustin Poirier");
        employee2.setSalary(800000L);

        employeeRepository.save(employee1);
        employeeRepository.save(employee2);

        assertThat(employeeRepository.count()).isEqualTo(3);
    }

    @Test
    void findByName_ShouldReturnEmployeesWithMatchingName() {
        List<Employee> result = employeeRepository.findAll().stream().filter(e -> e.getName().equals("Islam Makhachev")).toList();

        assertThat(result).hasSize(1);
    }
}
