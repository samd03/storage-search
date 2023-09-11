package com.hevo;

import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@Slf4j
public class Controller {

    @Autowired
    private Service service;
    @GetMapping("/search")
    public Response search(@RequestParam("q") String query) {
        return Response.builder()
                .filesList(service.search(query))
                .build();
    }

    @PostMapping("/discover")
    public String discover(@RequestParam(value = "p", required = false) String prefix) {
        service.indexFiles(prefix);
        return "ok";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam("p") String path) {
        service.delete(path);
        return "ok";
    }
}
