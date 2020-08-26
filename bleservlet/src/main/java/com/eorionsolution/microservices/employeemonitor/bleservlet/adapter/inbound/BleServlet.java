package com.eorionsolution.microservices.employeemonitor.bleservlet.adapter.inbound;

import com.eorionsolution.microservices.employeemonitor.bleservlet.service.BleMessageService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = "/*", asyncSupported = true)
@Log4j2
@AllArgsConstructor
public class BleServlet extends HttpServlet {
    private final BleMessageService bleMessageService;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        bleMessageService.saveBleMessageToMongo(req.getInputStream());
    }

}
