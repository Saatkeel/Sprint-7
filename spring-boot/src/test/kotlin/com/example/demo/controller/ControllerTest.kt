package com.example.demo.controller

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content

@WebMvcTest(Controller::class)
internal class ControllerTest{
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun `GET request should return content with status 200 OK`(){
        val result = mockMvc.perform(get("/api/hello"))

        result.andExpect{
            status().isOk
            content().string("Hello World!")
        }
    }
}