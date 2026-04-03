package com.olivaris.olivaris_app.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.olivaris.olivaris_app.dto.CreatePhytoProductReq;
import com.olivaris.olivaris_app.dto.PhytoProductDto;
import com.olivaris.olivaris_app.services.PhytoProductService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping(value = "/api/phytoProduct")
@AllArgsConstructor
public class PhytoProductController {

    private final PhytoProductService phytoProductServ;

    @PostMapping("/")
    public ResponseEntity<PhytoProductDto> create(@Valid @RequestBody CreatePhytoProductReq body) {
        return phytoProductServ.create(body);
    }

    @GetMapping("/all")
    public ResponseEntity<List<PhytoProductDto>> getAll() {
        return phytoProductServ.getAllPhytoProduct();
    }
}
