package com.pfc.thindesk.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {
    @GetMapping("/login") public String login() { return "login"; }
    @GetMapping("/") public String inicial() { return "redirect:/dashboard"; }
    @GetMapping("/dashboard") public String dashboard() { return "dashboard"; }
    @GetMapping("/chamados") public String chamados() { return "chamados"; }
    @GetMapping("/clientes") public String clientes() { return "clientes"; }
    @GetMapping("/ajustes-horarios") public String horarios() { return "ajustes-horarios"; }
}
