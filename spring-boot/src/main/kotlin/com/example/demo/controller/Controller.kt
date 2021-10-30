package com.example.demo.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController("api")
class Controller{
    @GetMapping("hello")
    fun hello() = "Hello World!"
}