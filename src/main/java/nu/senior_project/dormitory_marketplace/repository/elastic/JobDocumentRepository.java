package nu.senior_project.dormitory_marketplace.repository.elastic;

import nu.senior_project.dormitory_marketplace.entity.elastic.JobDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface JobDocumentRepository extends ElasticsearchRepository<JobDocument, Long> {
}
