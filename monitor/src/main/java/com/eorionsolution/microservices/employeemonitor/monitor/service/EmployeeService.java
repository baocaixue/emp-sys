package com.eorionsolution.microservices.employeemonitor.monitor.service;

import com.eorionsolution.microservices.employeemonitor.domain.DeviceMessage;
import com.eorionsolution.microservices.employeemonitor.monitor.domain.Employee;
import com.eorionsolution.microservices.employeemonitor.monitor.repository.EmployeeAvatarRepository;
import com.eorionsolution.microservices.employeemonitor.monitor.repository.EmployeeRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Service
@AllArgsConstructor
@Log4j2
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final EmployeeAvatarRepository employeeAvatarRepository;
    private final DeviceMessageService deviceMessageService;
    private final RefreshDurationService refreshDurationService;
    private final EmployeeAvatarService employeeAvatarService;

    public Flux<Employee> findAll() {
        return employeeRepository.findAll();
    }

    public Mono<Employee> save(Mono<Employee> employee) {
        return employee
                .flatMap(e ->
                employeeAvatarService
                .save(e.getEmployeeAvatar())
                        .map(e::setEmployeeAvatar)
        ).flatMap(employeeRepository::save);
    }

    public Mono<Object> checkExist(Mono<Employee> employee) {
        return employee.map(Employee::getSn)
                .flatMap(employeeRepository::findById)
                .flatMap(__ -> Mono.error(new DuplicateKeyException("")))
                .switchIfEmpty(Mono.just(employee));
    }

    public Flux<Employee> findEmployeesInByDuration() {
        return employeeRepository.findAllByMacInAndEnabledIsTrue(
                deviceMessageService.getMessagesByDuration(refreshDurationService.findOne())
                        .map(DeviceMessage::getMac)).sort((o1, o2) -> o2.getPriority() - o1.getPriority());
    }

    public Flux<Employee> findEmployeesOutByDuration() {
        return employeeRepository.findAllByMacNotInAndEnabledIsTrue(
                deviceMessageService.getMessagesByDuration(refreshDurationService.findOne())
                        .map(DeviceMessage::getMac)).sort((o1, o2) -> o2.getPriority() - o1.getPriority());
    }

    @Transactional
    public Mono<Void> deleteEmployeeCascadingWithAvatar(Mono<Employee> employee) {
        return employee
                .map(Employee::getSn)
                .filter(sn -> !StringUtils.isEmpty(sn))
                .flatMap(employeeRepository::findById)
                .flatMap(e -> {
                    var avatarId = e.getEmployeeAvatar().getId();
                    return employeeRepository.deleteBySn(e.getSn())
                            .then(employeeAvatarRepository.deleteById(avatarId));
                });
    }
}
