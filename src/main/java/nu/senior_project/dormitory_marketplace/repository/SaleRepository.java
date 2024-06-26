package nu.senior_project.dormitory_marketplace.repository;

import nu.senior_project.dormitory_marketplace.entity.Post;
import nu.senior_project.dormitory_marketplace.entity.Sale;
import nu.senior_project.dormitory_marketplace.entity.User;
import nu.senior_project.dormitory_marketplace.enums.ESaleStatus;
import nu.senior_project.dormitory_marketplace.exception.NotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {
    default Sale findByIdOrThrow(Long id) {
        return findById(id).orElseThrow(() -> new NotFoundException("Sale not found"));
    }

    List<Sale> findByPostAndStatusIn(Post post, List<Short> statuses);

    List<Sale> findByBuyerOrderByIdDesc(User buyer);
    List<Sale> findBySellerOrderByIdDesc(User buyer);
    List<Sale> findBySellerAndStatusOrderByIdDesc(User buyer, Short status);
}

