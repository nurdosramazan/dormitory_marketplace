package nu.senior_project.dormitory_marketplace.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import nu.senior_project.dormitory_marketplace.dto.GeneralPageableResponse;
import nu.senior_project.dormitory_marketplace.dto.application.ApplicationDto;
import nu.senior_project.dormitory_marketplace.dto.application.ApplicationFullDto;
import nu.senior_project.dormitory_marketplace.service.ApplicationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/application")
@RequiredArgsConstructor
@CrossOrigin
public class ApplicationController {

    private final ApplicationService applicationService;
    @PostMapping
    public void create(@RequestBody ApplicationDto applicationDto, HttpServletRequest request) {
        applicationService.create(applicationDto, request);
    }

    @GetMapping("/{id}")
    public ApplicationFullDto get(@PathVariable Long id) {
        return applicationService.get(id);
    }

    @GetMapping("/list")
    public GeneralPageableResponse<ApplicationFullDto> getList(HttpServletRequest request,
                                                               @RequestParam Integer limit,
                                                               @RequestParam Integer offset) {
        return applicationService.getList(request, limit, offset);
    }

}
