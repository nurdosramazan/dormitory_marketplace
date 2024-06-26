package nu.senior_project.dormitory_marketplace.repository.elastic;

import nu.senior_project.dormitory_marketplace.entity.elastic.PostDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostDocumentRepository extends ElasticsearchRepository<PostDocument, String> {

}
