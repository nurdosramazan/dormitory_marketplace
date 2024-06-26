package nu.senior_project.dormitory_marketplace.service;

import lombok.RequiredArgsConstructor;
import nu.senior_project.dormitory_marketplace.entity.Post;
import nu.senior_project.dormitory_marketplace.entity.Rating;
import nu.senior_project.dormitory_marketplace.entity.Sale;
import nu.senior_project.dormitory_marketplace.entity.User;
import nu.senior_project.dormitory_marketplace.enums.ESaleStatus;
import nu.senior_project.dormitory_marketplace.exception.InsufficientRightsException;
import nu.senior_project.dormitory_marketplace.repository.PostRepository;
import nu.senior_project.dormitory_marketplace.repository.RatingRepository;
import nu.senior_project.dormitory_marketplace.repository.SaleRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RatingService {
    private final RatingRepository ratingRepository;
    private final SaleRepository saleRepository;
    private final PostRepository postRepository;

    public void rate(Long saleId, Integer stars, String comment) {
        Sale sale = saleRepository.findByIdOrThrow(saleId);
        Post post = sale.getPost();
        User user = sale.getBuyer();

        if (sale.getStatus() != ESaleStatus.APPROVED_BY_BUYER.getValue()
                || sale.getStatus() != ESaleStatus.FINISHED.getValue()) {
            throw new InsufficientRightsException("You need to buy the product to rate!");        }

        if (stars < 1 || stars > 5) {
            throw new ArrayIndexOutOfBoundsException("Wrong stars");
        }

        Rating existingRating = ratingRepository.findByPostIdAndUserId(post.getId(), user.getId());
        if (existingRating != null) {
            existingRating.setCreatedAt(LocalDateTime.now());
            existingRating.setRating(stars);
            ratingRepository.save(existingRating);
        } else {
            Rating rating = new Rating();
            rating.setRating(stars);
            rating.setComment(comment);
            rating.setUser(user);
            rating.setPost(post);
            rating.setCreatedAt(LocalDateTime.now());
            ratingRepository.save(rating);
        }

        post.setAverageRating(getAverageRating(post.getId()));
        postRepository.save(post);
    }

    private Double getAverageRating(Long postId) {
        List<Rating> ratings = getRatings(postId);
        if (ratings.isEmpty()) {
            return null;
        }

        double sum = 0;
        for (Rating rating : ratings) {
            sum += rating.getRating();
        }

        return sum / ratings.size();
    }
    public List<Rating> getRatings(Long postId) {
        return ratingRepository.findByPostId(postId);
    }
}
