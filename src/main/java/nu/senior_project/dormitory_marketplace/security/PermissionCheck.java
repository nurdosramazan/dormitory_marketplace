package nu.senior_project.dormitory_marketplace.security;

import lombok.RequiredArgsConstructor;
import nu.senior_project.dormitory_marketplace.entity.*;
import nu.senior_project.dormitory_marketplace.enums.EAuctionStatus;
import nu.senior_project.dormitory_marketplace.repository.*;
import nu.senior_project.dormitory_marketplace.service.AuthService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PermissionCheck {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final LotRepository lotRepository;
    private final JobRepository jobRepository;
    private final SaleRepository saleRepository;
    private final AuctionRepository auctionRepository;
    public boolean checkPostOwnership(Authentication authentication, Long postId) {
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User user = userRepository.findByUsernameOrThrow(username);
        Post post = postRepository.findByIdOrThrow(postId);
        return (user.equals(post.getOwner()));
    }

    public boolean checkLotOwnership(Authentication authentication, Long lotId) {
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User user = userRepository.findByUsernameOrThrow(username);
        Lot lot = lotRepository.findByIdOrThrow(lotId);
        return lot.getCreator().equals(user);
    }

    public boolean checkJobOwnership(Authentication authentication, Long jobId) {
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User user = userRepository.findByUsernameOrThrow(username);
        Job job = jobRepository.findByIdOrThrow(jobId);
        return job.getOwner().equals(user);
    }

    public boolean checkSaleProcessingRights(Authentication authentication, Long saleId, String actor) {
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User user = userRepository.findByUsernameOrThrow(username);
        Sale sale = saleRepository.findByIdOrThrow(saleId);
        return user.equals(actor.equals("buyer") ? sale.getBuyer() : sale.getSeller());
    }

    public boolean checkUserInfoUpdateRights(Authentication authentication, Long userId) {
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User user = userRepository.findByUsernameOrThrow(username);
        return user.getId().equals(userId);
    }

    public boolean checkSuperUser(Authentication authentication) {
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User user = userRepository.findByUsernameOrThrow(username);
        return isSuperAdmin(user);
    }

    public boolean checkSuperUserOrAuctionActive(Authentication authentication, Long auctionId) {
        Auction auction = auctionRepository.findByIdOrThrow(auctionId);
        boolean isActive = List.of(EAuctionStatus.ACTIVE, EAuctionStatus.INITIATED).contains(auction.getStatus());

        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User user = userRepository.findByUsernameOrThrow(username);

        return isActive || isSuperAdmin(user);
    }

    private boolean isSuperAdmin(User user) {
        return user.getRoles()
                .stream()
                .map(Role::getId)
                .toList()
                .contains(AuthService.SUPERADMIN_ROLE_ID);
    }

}
