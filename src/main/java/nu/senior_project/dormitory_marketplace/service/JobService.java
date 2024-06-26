package nu.senior_project.dormitory_marketplace.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import nu.senior_project.dormitory_marketplace.dto.GeneralPageableResponse;
import nu.senior_project.dormitory_marketplace.dto.job.JobDto;
import nu.senior_project.dormitory_marketplace.dto.job.JobRequest;
import nu.senior_project.dormitory_marketplace.dto.job.JobShortDto;
import nu.senior_project.dormitory_marketplace.dto.job.PayUnitDto;
import nu.senior_project.dormitory_marketplace.entity.Job;
import nu.senior_project.dormitory_marketplace.entity.PayUnit;
import nu.senior_project.dormitory_marketplace.entity.elastic.JobDocument;
import nu.senior_project.dormitory_marketplace.exception.InsufficientRightsException;
import nu.senior_project.dormitory_marketplace.repository.JobRepository;
import nu.senior_project.dormitory_marketplace.repository.PayUnitRepository;
import nu.senior_project.dormitory_marketplace.repository.UserRepository;
import nu.senior_project.dormitory_marketplace.repository.elastic.JobDocumentRepository;
import nu.senior_project.dormitory_marketplace.util.PageableElastic;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.core.query.StringQuery;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobService {

    private final EntityConverterService entityConverterService;
    private final UserRepository userRepository;
    private final PayUnitRepository payUnitRepository;
    private final JobRepository jobRepository;
    private final ElasticsearchOperations elasticsearchOperations;
    private final JobDocumentRepository jobDocumentRepository;

    public void createJob(JobRequest jobRequest, HttpServletRequest request) {
        Job job = entityConverterService.toJob(jobRequest);
        job.setOwner(userRepository.findByUsernameOrThrow(request.getUserPrincipal().getName()));
        job.setPayUnit(payUnitRepository.findByIdOrThrow(jobRequest.getPayUnitId()));
        jobRepository.save(job);
        jobDocumentRepository.save(new JobDocument(job));
    }

    public void updateJob(JobRequest jobRequest, HttpServletRequest request) {
        Job job = jobRepository.findByIdOrThrow(jobRequest.getId());

        if (!userHasRights(job, request.getUserPrincipal().getName())) {
            throw new InsufficientRightsException("no right to update job");
        }

        entityConverterService.toJob(jobRequest, job);
        job.setPayUnit(payUnitRepository.findByIdOrThrow(jobRequest.getPayUnitId()));

        jobRepository.save(job);
        jobDocumentRepository.save(new JobDocument(job));

    }

    public void deleteJob(Long id, HttpServletRequest request) {
        Job job = jobRepository.findByIdOrThrow(id);

        if (!userHasRights(job, request.getUserPrincipal().getName())) {
            throw new InsufficientRightsException("no right to delete job");
        }

        jobRepository.delete(job);
    }

    public JobDto getJob(Long id) {
        Job job = jobRepository.findByIdOrThrow(id);
        return entityConverterService.toJobDto(job);
    }

    public GeneralPageableResponse<JobShortDto> getJobs(String queryText, Long payUnitId, Long payMin, Long payMax, Integer offset, Integer limit) {
        String queryString = constructQueryString(queryText, payUnitId, payMin, payMax);
        Query query = new StringQuery(queryString);

        SearchHits<JobDocument> response = elasticsearchOperations.search(query, JobDocument.class);

        List<JobShortDto> totalResults = response.getSearchHits()
                .stream()
                .map(hit -> entityConverterService.toJobShortDto(hit.getContent()))
                .toList();

        return new GeneralPageableResponse<>(totalResults, limit, offset);
    }

    private String constructQueryString(String queryText, Long payUnitId, Long payMin, Long payMax) {
        StringBuilder queryStringBuilder = new StringBuilder();
        queryStringBuilder.append("{\"bool\": { \"must\": [");


        if (queryText != null && !queryText.isEmpty()) {
            queryStringBuilder.append("{ \"multi_match\": { \"query\": \"").append(queryText).append("\", \"fields\": [\"name\", \"description\"], \"fuzziness\": \"AUTO\" } },");
        }

        if (payUnitId != null) {
            queryStringBuilder.append("{ \"match\": { \"payUnitId\": ").append(payUnitId).append(" } },");
        }

        if (payMin != null || payMax != null) {
            queryStringBuilder.append("{ \"range\": { \"payPerUnit\": { ");
            if (payMin != null) {
                queryStringBuilder.append("\"gte\": ").append(payMin).append(",");
            }
            if (payMax != null) {
                queryStringBuilder.append("\"lte\": ").append(payMax);
            }
            queryStringBuilder.append(" } } },");
        }

        if (queryStringBuilder.charAt(queryStringBuilder.length() - 1) == ',') {
            queryStringBuilder.deleteCharAt(queryStringBuilder.length() - 1);
        }

        queryStringBuilder.append(" ] } }, \"sort\": [ { \"_score\": { \"order\": \"desc\" } } ] }");

        return queryStringBuilder.toString();
    }

    public List<PayUnitDto> getPayUnits() {
        List<PayUnit> payUnits = payUnitRepository.findAll();
        return payUnits.stream().map(entityConverterService::toPayUnitDto).collect(Collectors.toList());
    }

    private Boolean userHasRights(Job job, String username) {
        return username.equals(job.getOwner().getUsername());
    }
}
