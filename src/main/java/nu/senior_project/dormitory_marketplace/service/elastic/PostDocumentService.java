package nu.senior_project.dormitory_marketplace.service.elastic;

import lombok.SneakyThrows;
import nu.senior_project.dormitory_marketplace.dto.GeneralPageableResponse;
import nu.senior_project.dormitory_marketplace.dto.post.PostShortDto;
import nu.senior_project.dormitory_marketplace.entity.elastic.PostDocument;
import nu.senior_project.dormitory_marketplace.repository.elastic.PostDocumentRepository;
import nu.senior_project.dormitory_marketplace.service.EntityConverterService;
import nu.senior_project.dormitory_marketplace.util.PageableElastic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.core.query.StringQuery;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PostDocumentService {

    @Autowired
    private PostDocumentRepository postDocumentRepository;

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    @Autowired
    private EntityConverterService entityConverterService;

    public void save(PostDocument postDocument) {
        postDocumentRepository.save(postDocument);
    }
    @SneakyThrows
    public GeneralPageableResponse<PostShortDto> getPosts(String queryText, Long categoryId, Integer priceMin, Integer priceMax, Integer limit, Integer offset) {

        String queryString = constructQueryString(queryText, categoryId, priceMin, priceMax);
        Query query = new StringQuery(queryString);

        SearchHits<PostDocument> response = elasticsearchOperations.search(query, PostDocument.class);

        List<PostShortDto> totalResult = response.getSearchHits()
                .stream()
                .map(hit -> entityConverterService.toPostShortDto(hit.getContent()))
                .toList();

        return new GeneralPageableResponse<>(totalResult, limit, offset);
    }

    private String constructQueryString(String queryText, Long categoryId, Integer priceMin, Integer priceMax) {

        StringBuilder queryStringBuilder = new StringBuilder();
        queryStringBuilder.append("{\"bool\": { \"must\": [");


        if (queryText != null && !queryText.isEmpty()) {
            queryStringBuilder.append("{ \"multi_match\": { \"query\": \"").append(queryText).append("\", \"fields\": [\"title\", \"description\"], \"fuzziness\": \"AUTO\" } },");
        }

        if (categoryId != null) {
            queryStringBuilder.append("{ \"match\": { \"categoryId\": ").append(categoryId).append(" } },");
        }

        if (priceMin != null || priceMax != null) {
            queryStringBuilder.append("{ \"range\": { \"price\": { ");
            if (priceMin != null) {
                queryStringBuilder.append("\"gte\": ").append(priceMin).append(",");
            }
            if (priceMax != null) {
                queryStringBuilder.append("\"lte\": ").append(priceMax);
            }
            queryStringBuilder.append(" } } },");
        }

        if (queryStringBuilder.charAt(queryStringBuilder.length() - 1) == ',') {
            queryStringBuilder.deleteCharAt(queryStringBuilder.length() - 1);
        }

        queryStringBuilder.append(" ] } }, \"sort\": [ { \"_score\": { \"order\": \"desc\" } } ] }");

        return queryStringBuilder.toString();
    }

}
