package nu.senior_project.dormitory_marketplace.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import nu.senior_project.dormitory_marketplace.dto.GeneralPageableResponse;
import nu.senior_project.dormitory_marketplace.dto.job.JobDto;
import nu.senior_project.dormitory_marketplace.dto.job.JobRequest;
import nu.senior_project.dormitory_marketplace.dto.job.JobShortDto;
import nu.senior_project.dormitory_marketplace.dto.job.PayUnitDto;
import nu.senior_project.dormitory_marketplace.service.JobService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/job")
@RequiredArgsConstructor
@CrossOrigin
public class JobController {

    private final JobService jobService;
    @PostMapping
    public void create(@RequestBody JobRequest jobRequest, HttpServletRequest httpServletRequest) {
        jobService.createJob(jobRequest, httpServletRequest);
    }

    @PutMapping("/{id}")
    @PreAuthorize("@permissionCheck.checkJobOwnership(authentication, #id)")//???
    public void update(@RequestBody JobRequest jobRequest, HttpServletRequest httpServletRequest) {
        jobService.updateJob(jobRequest, httpServletRequest);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@permissionCheck.checkJobOwnership(authentication, #id)")
    public void delete(@PathVariable Long id, HttpServletRequest request) {
        jobService.deleteJob(id, request);
    }

    @GetMapping("/{id}")
    public JobDto get(@PathVariable Long id) {
        return jobService.getJob(id);
    }

    @GetMapping("/payUnits")
    public List<PayUnitDto> getPayUnits() {
        return jobService.getPayUnits();
    }

    @GetMapping("/search")
    public GeneralPageableResponse<JobShortDto> getJobs(@RequestParam(required = false) String queryText,
                                                        @RequestParam(required = false) Long payUnitId,
                                                        @RequestParam(required = false) Long payMin,
                                                        @RequestParam(required = false) Long payMax,
                                                        @RequestParam Integer offset,
                                                        @RequestParam Integer limit) {
        return jobService.getJobs(queryText, payUnitId, payMin, payMax, offset, limit);
    }

}
