package nu.senior_project.dormitory_marketplace;

import nu.senior_project.dormitory_marketplace.entity.Job;
import nu.senior_project.dormitory_marketplace.entity.Post;
import nu.senior_project.dormitory_marketplace.entity.elastic.JobDocument;
import nu.senior_project.dormitory_marketplace.entity.elastic.PostDocument;
import nu.senior_project.dormitory_marketplace.repository.JobRepository;
import nu.senior_project.dormitory_marketplace.repository.PostRepository;
import nu.senior_project.dormitory_marketplace.repository.elastic.JobDocumentRepository;
import nu.senior_project.dormitory_marketplace.service.elastic.PostDocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Runner implements CommandLineRunner {

    @Autowired
    private PostDocumentService postDocumentService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private JobDocumentRepository jobDocumentRepository;

    @Autowired
    private JobRepository jobRepository;

    @Override
    public void run(String... args) throws Exception {
        List<Post> posts = postRepository.findAll();
        for (var post : posts) {
            postDocumentService.save(new PostDocument(post));
        }

        List<Job> jobs = jobRepository.findAll();
        for (var job : jobs) {
            jobDocumentRepository.save(new JobDocument(job));
        }
    }
}
