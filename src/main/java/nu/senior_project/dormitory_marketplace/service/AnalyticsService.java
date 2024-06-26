package nu.senior_project.dormitory_marketplace.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import nu.senior_project.dormitory_marketplace.dto.AnalyticsDto;
import nu.senior_project.dormitory_marketplace.dto.PostAnalyticsDto;
import nu.senior_project.dormitory_marketplace.entity.Sale;
import nu.senior_project.dormitory_marketplace.entity.User;
import nu.senior_project.dormitory_marketplace.enums.ESaleStatus;
import nu.senior_project.dormitory_marketplace.repository.SaleRepository;
import nu.senior_project.dormitory_marketplace.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final SaleRepository saleRepository;
    private final UserRepository userRepository;
    private final EntityConverterService entityConverterService;

    public AnalyticsDto getAnalytics(HttpServletRequest request) {
        String username = request.getUserPrincipal().getName();
        User user = userRepository.findByUsernameOrThrow(username);
        List<Sale> sales = saleRepository.findBySellerAndStatusOrderByIdDesc(user, ESaleStatus.FINISHED.getValue());

        AnalyticsDto result = new AnalyticsDto();
        Map<Long, PostAnalyticsDto> salesByPosts = new HashMap<>();

        Integer totalPrice = 0;
        Integer totalWeekPrice = 0;
        Integer totalMonthPrice = 0;

        for (var sale : sales) {
            if (salesByPosts.containsKey(sale.getPost().getId())) {
                var postAnalytics = salesByPosts.get(sale.getPost().getId());
                postAnalytics.setSoldCount(postAnalytics.getSoldCount() + 1);
                postAnalytics.setTotalEarnings(postAnalytics.getTotalEarnings() + sale.getPrice());
            } else {
                var postAnalytics = new PostAnalyticsDto();
                postAnalytics.setSoldCount(1);
                postAnalytics.setPostShortDto(entityConverterService.toPostShortDto(sale.getPost()));
                postAnalytics.setTotalEarnings(sale.getPrice());
                salesByPosts.put(sale.getPost().getId(), postAnalytics);
            }
            if (sale.getModifiedTime().after(new Timestamp(System.currentTimeMillis() - 7L * 24 * 60 * 60 * 1000))) {
                totalWeekPrice += sale.getPrice();
            }

            if (sale.getModifiedTime().after(new Timestamp(System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000))) {
                totalMonthPrice += sale.getPrice();
            }

            totalPrice += sale.getPrice();
        }

        result.setTotalItemsSold(sales.size());
        result.setTotalEarnings(totalPrice);
        result.setPostsData(salesByPosts.values().stream().toList());
        result.setLastWeekRevenue(totalWeekPrice);
        result.setLastMonthRevenue(totalMonthPrice);

        return result;
    }
}
