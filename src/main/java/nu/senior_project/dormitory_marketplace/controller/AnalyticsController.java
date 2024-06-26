package nu.senior_project.dormitory_marketplace.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import nu.senior_project.dormitory_marketplace.dto.AnalyticsDto;
import nu.senior_project.dormitory_marketplace.service.AnalyticsService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/analytics")
@RequiredArgsConstructor
@CrossOrigin
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping
    public AnalyticsDto getAnalytics(HttpServletRequest request) {
        return analyticsService.getAnalytics(request);
    }
}
