package SGU.Tourio.Services;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.persistence.EntityExistsException;

import SGU.Tourio.DTO.ReportEmployeeDTO;
import SGU.Tourio.DTO.ViewGroupDTO;
import SGU.Tourio.Models.Group;
import SGU.Tourio.Models.GroupCostRel;
import SGU.Tourio.Models.GroupEmployeeRel;
import SGU.Tourio.Repositories.GroupEmpRelRepository;
import SGU.Tourio.Repositories.GroupRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import SGU.Tourio.DTO.CreateEmployeeDTO;
import SGU.Tourio.Models.Employee;
import SGU.Tourio.Repositories.EmployeeRepository;
import SGU.Tourio.Utils.FormatString;

@Service
public class EmployeeService {
    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    GroupEmpRelRepository groupRepository;

    public List<Employee> getAll() {
        return employeeRepository.findAll();
    }

    public List<ReportEmployeeDTO> getForTourReport(Optional<String> from, Optional<String> to) throws ParseException {
        List<Employee> employees = getAll();
        List<GroupEmployeeRel> groups;
        if (from.isPresent() && to.isPresent()) {
            Date fromDate = new SimpleDateFormat("yyyy-MM-dd").parse(from.get());
            Date toDate = new SimpleDateFormat("yyyy-MM-dd").parse(to.get());
            groups = groupRepository.findAllByGroupDateStartBetween(fromDate, toDate);
        } else {
            groups = groupRepository.findAll();
        }

        List<ReportEmployeeDTO> dtoList = new ArrayList<>();
        for (Employee employee : employees) {
            ReportEmployeeDTO dto = new ModelMapper().map(employee, ReportEmployeeDTO.class);
            int count = groups.stream().filter(g -> Objects.equals(g.getEmployee().getId(), employee.getId())).toList().size();
            dto.setGroupCount(count);
            dtoList.add(dto);
        }
        return dtoList;
    }

    public Employee get(Long id) {
        if (id == null)
            throw new NullPointerException("ID is null");

        Optional<Employee> employee = employeeRepository.findById(id);

        return employee.orElse(null);
    }

    public Employee get(String name) {
        if (name == null)
            throw new NullPointerException("Name is null");

        // Format
        name = FormatString.TitleCase(name);

        Optional<Employee> employee = employeeRepository.findByName(name);
        return employee.orElse(null);
    }

    public Employee create(CreateEmployeeDTO dto) throws EntityExistsException {
        // Format
        dto.setName(FormatString.TitleCase(dto.getName()));

        if (employeeRepository.existsByName(dto.getName())) {
            throw new EntityExistsException(dto.getName() + " existed");
        }
        Employee employee = new ModelMapper().map(dto, Employee.class);
        return employeeRepository.save(employee);
    }

    public Employee update(Employee employee) throws EntityExistsException {
        // Format
        employee.setName(FormatString.TitleCase(employee.getName()));
        Employee isExist = get(employee.getName());
        if (isExist != null && !employee.getId().equals(isExist.getId())) {
            throw new EntityExistsException(employee.getName() + " existed");
        }
        return employeeRepository.save(employee);
    }

    public void delete(Long id) {
        if (id == null)
            throw new NullPointerException("ID is null");

        if (employeeRepository.existsById(id))
            employeeRepository.deleteById(id);
    }
}
