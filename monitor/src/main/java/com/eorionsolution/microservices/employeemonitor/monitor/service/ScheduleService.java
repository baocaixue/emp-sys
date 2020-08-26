package com.eorionsolution.microservices.employeemonitor.monitor.service;

import com.eorionsolution.microservices.employeemonitor.monitor.domain.EmployeeSchedule;
import com.eorionsolution.microservices.employeemonitor.monitor.domain.ScheduleDTO;
import com.eorionsolution.microservices.employeemonitor.monitor.repository.EmployeeRepository;
import com.eorionsolution.microservices.employeemonitor.monitor.repository.EmployeeScheduleRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.time.ZoneId;
import java.util.LinkedList;

@Service
@AllArgsConstructor
@Log4j2
public class ScheduleService {
    private final EmployeeRepository employeeRepository;
    private final EmployeeScheduleRepository employeeScheduleRepository;


    public Flux<EmployeeSchedule> parseUploadedSchedulerFile(FilePart file) {
        return file.content()
                .<InputStream>reduce(new InputStream() {
                    public int read() { return -1; }
                }, (s, d) -> new SequenceInputStream(s, d.asInputStream())
            )
                .flatMapMany(inputStream -> {
                    try {
                        var workBook = WorkbookFactory.create(inputStream);
                        var sheet = workBook.getSheetAt(0);
                        var ri = sheet.iterator();
                        ri.next();
                        ri.next();
                        var result = new LinkedList<ScheduleDTO>();
                        while (ri.hasNext()) {
                            ScheduleDTO dto = new ScheduleDTO();
                            var ci = ri.next().cellIterator();
                            if (ci.hasNext()) {
                                dto.setDate(ci.next().getDateCellValue());
                            }
                            if (ci.hasNext()) {
                                dto.setDepartmentMorning(ci.next().getStringCellValue());
                            }
                            if (ci.hasNext()) {
                                dto.setDepartmentAfternoon(ci.next().getStringCellValue());
                            }
                            if (ci.hasNext()) {
                                dto.setSalesMorning(ci.next().getStringCellValue());
                            }
                            if (ci.hasNext()) {
                                dto.setSalesAfternoon(ci.next().getStringCellValue());
                            }
                            if (ci.hasNext()) {
                                dto.setServiceMorning(ci.next().getStringCellValue());
                            }
                            if (ci.hasNext()) {
                                dto.setServiceAfternoon(ci.next().getStringCellValue());
                            }
                            if (ci.hasNext()) {
                                dto.setRedCrossMorning(ci.next().getStringCellValue());
                            }
                            if (ci.hasNext()) {
                                dto.setRedCrossAfternoon(ci.next().getStringCellValue());
                            }
                            result.add(dto);
                        }
                        return Flux.fromIterable(result);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return Flux.empty();
                })
                .flatMap(dto -> employeeScheduleRepository.save(EmployeeSchedule.fromScheduleDTO(dto)));
    }
}
