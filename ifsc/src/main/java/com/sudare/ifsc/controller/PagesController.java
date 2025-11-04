package com.sudare.ifsc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PagesController {

  @GetMapping("/")
  public String home() { return "index"; }

  @GetMapping("/pedidos")
  public String pedidos() { return "pedidos"; }

  @GetMapping("/cardapio")
  public String cardapio() { return "cardapio"; }

  @GetMapping("/relatorios")
  public String relatorios() { return "relatorios"; }
}

