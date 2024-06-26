package nu.senior_project.dormitory_marketplace.service;

import lombok.RequiredArgsConstructor;
import nu.senior_project.dormitory_marketplace.dto.application.ApplicationDto;
import nu.senior_project.dormitory_marketplace.dto.auction.AuctionDto;
import nu.senior_project.dormitory_marketplace.dto.application.ApplicationFullDto;
import nu.senior_project.dormitory_marketplace.dto.lot.LotDto;
import nu.senior_project.dormitory_marketplace.dto.category.CategoryDto;
import nu.senior_project.dormitory_marketplace.dto.image.ImageDto;
import nu.senior_project.dormitory_marketplace.dto.job.JobDto;
import nu.senior_project.dormitory_marketplace.dto.job.JobRequest;
import nu.senior_project.dormitory_marketplace.dto.job.JobShortDto;
import nu.senior_project.dormitory_marketplace.dto.job.PayUnitDto;
import nu.senior_project.dormitory_marketplace.dto.lot.LotFullDto;
import nu.senior_project.dormitory_marketplace.dto.post.PostDto;
import nu.senior_project.dormitory_marketplace.dto.post.PostRequest;
import nu.senior_project.dormitory_marketplace.dto.post.PostShortDto;
import nu.senior_project.dormitory_marketplace.dto.registration.RegistrationRequest;
import nu.senior_project.dormitory_marketplace.dto.registration.StoreRegistrationRequest;
import nu.senior_project.dormitory_marketplace.dto.sale.SaleFullDto;
import nu.senior_project.dormitory_marketplace.dto.user.*;
import nu.senior_project.dormitory_marketplace.entity.*;
import nu.senior_project.dormitory_marketplace.entity.elastic.JobDocument;
import nu.senior_project.dormitory_marketplace.entity.elastic.PostDocument;
import nu.senior_project.dormitory_marketplace.entity.image.CategoryImage;
import nu.senior_project.dormitory_marketplace.entity.image.PostImage;
import nu.senior_project.dormitory_marketplace.entity.image.StoreImage;
import nu.senior_project.dormitory_marketplace.entity.image.UserImage;
import nu.senior_project.dormitory_marketplace.enums.ESaleStatus;
import nu.senior_project.dormitory_marketplace.repository.*;
import nu.senior_project.dormitory_marketplace.repository.CategoryRepository;
import nu.senior_project.dormitory_marketplace.repository.RoleRepository;
import nu.senior_project.dormitory_marketplace.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EntityConverterService {

    private final RoleRepository roleRepository;

    private final CategoryRepository categoryRepository;

    private final UserRepository userRepository;

    private final StoreRepository storeRepository;

    private final PostRepository postRepository;

    private final PayUnitRepository payUnitRepository;

    private final PasswordEncoder passwordEncoder;

    public User toUser(RegistrationRequest registrationRequest, Long roleId) {

        Role role = roleRepository.findByIdOrThrow(roleId);

        return User.builder()
                .username(registrationRequest.getUsername())
                .password(passwordEncoder.encode(registrationRequest.getPassword()))
                .email(registrationRequest.getEmail())
                .firstname(registrationRequest.getFirstname())
                .secondName(registrationRequest.getSecondName())
                .roles(Collections.singletonList(role))
                .isStore(roleId.equals(AuthService.STORE_ROLE_ID))
                .build();
    }

    public Store toStore(StoreRegistrationRequest request, Long userId) {
        Category category = categoryRepository.findByIdOrThrow(request.getCategoryId());
        User user = userRepository.findByIdOrThrow(userId);

        return Store.builder()
                .name(request.getStoreName())
                .description(request.getDescription())
                .category(category)
                .owner(user)
                .paypalEmail(request.getPaypalEmail())
                .build();
     }

    public Post toPost(PostRequest postRequest, String username) {

        User user = userRepository.findByUsernameOrThrow(username);
        Category category = categoryRepository.findByIdOrThrow(postRequest.getCategoryId());

        return Post.builder()
                .title(postRequest.getTitle())
                .description(postRequest.getDescription())
                .price(postRequest.getPrice())
                .owner(user)
                .category(category)
                .isActive(Boolean.TRUE)
                .build();
    }
    public ImageDto toImageDto(UserImage userImage) {
        if (userImage == null)
            return null;

        return ImageDto.builder()
                .id(userImage.getId())
                .path(userImage.getPath())
                .build();
    }

    public ImageDto toImageDto(PostImage postImage) {
        if (postImage == null)
            return null;

        return ImageDto.builder()
                .id(postImage.getId())
                .path(postImage.getPath())
                .build();
    }

    public ImageDto toImageDto(CategoryImage categoryImage) {
        if (categoryImage == null)
            return null;

        return ImageDto.builder()
                .id(categoryImage.getId())
                .path(categoryImage.getPath())
                .build();
    }

    public ImageDto toImageDto(StoreImage storeImage) {
        if (storeImage == null)
            return null;

        return ImageDto.builder()
                .id(storeImage.getId())
                .path(storeImage.getPath())
                .build();
    }

    public PostDto toPostDto(Post post) {
        PostDto postDto = PostDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .owner(UserShortDto.builder()
                        .username(post.getOwner().getUsername())
                        .firstName(post.getOwner().getFirstname())
                        .secondName(post.getOwner().getSecondName())
                        .id(post.getOwner().getId())
                        .build())
                .price(post.getPrice())
                .description(post.getDescription())
                .images(new ArrayList<>())
                .build();

        for (PostImage image : post.getImages()) {
            postDto.getImages().add(toImageDto(image));
        }

        return postDto;
    }

    public PostShortDto toPostShortDto(PostDocument postDocument) {

        return PostShortDto.builder()
                .id(Long.valueOf(postDocument.getId()))
                .name(postDocument.getTitle())
                .description(postDocument.getDescription())
                .price(postDocument.getPrice())
                .imageDto(new ImageDto(null, !postDocument.getImageUrls().isEmpty()
                        ? postDocument.getImageUrls().get(0).getUrl()
                        : null))
                .category(CategoryDto.builder()
                        .id(postDocument.getCategoryId())
                        .name(postDocument.getCategoryName())
                        .build())
                .owner(UserShortDto.builder()
                        .username(postDocument.getOwnerUsername())
                        .id(postDocument.getOwnerId())
                        .firstName(postDocument.getOwnerFirstName())
                        .secondName(postDocument.getOwnerSecondName())
                        .build())
                .rating(postDocument.getRating())
                .build();
    }

    public PostShortDto toPostShortDto(Post post) {

        List<PostImage> postImageList = post.getImages();

        return PostShortDto.builder()
                .id(post.getId())
                .name(post.getTitle())
                .description(post.getDescription())
                .price(post.getPrice())
                .category(CategoryDto.builder()
                        .id(post.getCategory().getId())
                        .name(post.getCategory().getName())
                        .build())
                .owner(UserShortDto.builder()
                        .username(post.getOwner().getUsername())
                        .firstName(post.getOwner().getFirstname())
                        .secondName(post.getOwner().getSecondName())
                        .id(post.getOwner().getId())
                        .build())
                .imageDto(toImageDto(postImageList.isEmpty()
                        ? null
                        : postImageList.get(0)))
                .rating(post.getAverageRating() == null ? "0" : post.getAverageRating().toString())
                .build();
    }


    public CategoryDto toCategoryDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .imageDto(toImageDto(category.getImage()))
                .build();
    }

    public StoreDto toStoreDto(Store store) {
        return StoreDto.builder()
                .id(store.getId())
                .name(store.getName())
                .description(store.getDescription())
                .categoryName(store.getCategory().getName())
                .images(store.getImages().stream().map(this::toImageDto).toList())
                .build();
    }

    public UserRole toUserRole(User user) {
        UserRole userRole = new UserRole();
        userRole.setId(user.getId());
        userRole.setRoles(user.getRoles().stream().map(Role::getRoleName).toList());
        return userRole;
    }

    public UserInfo toUserInfo(User user) {
        StoreDto storeDto = null;

        if (user.getIsStore()) {
            Store store = storeRepository.findByOwnerIdOrThrow(user.getId());
            storeDto = toStoreDto(store);
        }

        return UserInfo.builder()
                .id(user.getId())
                .roles(user.getRoles().stream().map(Role::getRoleName).toList())
                .firstname(user.getFirstname())
                .secondName(user.getSecondName())
                .username(user.getUsername())
                .email(user.getEmail())
                .profileImage(toImageDto(user.getImage()))
                .storeInfo(storeDto)
                .build();
    }

    public Sale toSale(Post post, User buyer, User seller) {

        return Sale.builder()
                .buyer(buyer)
                .status(ESaleStatus.NEEDS_SELLER_VALIDATION.getValue())
                .post(post)
                .price(post.getPrice())
                .seller(seller)
                .build();
    }

    public Job toJob(JobRequest jobRequest) {
        return Job.builder()
                .name(jobRequest.getName())
                .description(jobRequest.getDescription())
                .payPerUnit(jobRequest.getPayPerUnit())
                .qualifications(jobRequest.getQualifications())
                .contactInfo(jobRequest.getContactInfo())
                .build();
    }

    public void toJob(JobRequest jobRequest, Job job) {
        job.setName(jobRequest.getName());
        job.setDescription(jobRequest.getDescription());
        job.setPayPerUnit(jobRequest.getPayPerUnit());
        job.setQualifications(jobRequest.getQualifications());
        job.setContactInfo(jobRequest.getContactInfo());
    }

    public PayUnitDto toPayUnitDto(PayUnit payUnit) {
        return PayUnitDto.builder()
                .name(payUnit.getName())
                .id(payUnit.getId())
                .build();
    }

    public UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstname(user.getFirstname())
                .secondName(user.getSecondName())
                .email(user.getEmail())
                .isStore(user.getIsStore())
                .build();
    }

    public JobDto toJobDto(Job job) {
        return JobDto.builder()
                .id(job.getId())
                .name(job.getName())
                .description(job.getDescription())
                .createdDate(job.getCreatedDate())
                .modifiedDate(job.getModifiedDate())
                .qualifications(job.getQualifications())
                .payPerUnit(job.getPayPerUnit())
                .payUnit(toPayUnitDto(job.getPayUnit()))
                .contactInfo(job.getContactInfo())
                .owner(toUserDto(job.getOwner()))
                .build();
    }

    public JobShortDto toJobShortDto(JobDocument jobDocument) {
        return JobShortDto.builder()
                .id(jobDocument.getId())
                .name(jobDocument.getName())
                .description(jobDocument.getDescription())
                .payPerUnit(jobDocument.getPayPerUnit())
                .payUnit(toPayUnitDto(payUnitRepository.findByIdOrThrow(jobDocument.getPayUnitId())))
                .build();
    }

    public Auction toAuction(AuctionDto auctionDto) {
        return Auction.builder()
                .name(auctionDto.getName())
                .startTime(auctionDto.getStartTime())
                .endTime(auctionDto.getEndTime())
                .applicationAcceptTime(auctionDto.getApplicationAcceptTime())
                .build();
    }

    public AuctionDto toAuctionDto(Auction auction) {
        return AuctionDto.builder()
                .id(auction.getId())
                .name(auction.getName())
                .startTime(auction.getStartTime())
                .endTime(auction.getEndTime())
                .applicationAcceptTime(auction.getApplicationAcceptTime())
                .build();
    }

    public Lot toLot(LotDto lotDto) {
        return Lot.builder()
                .minPrice(lotDto.getMinPrice())
                .build();
    }

    public LotFullDto toLotFullDto(Lot lot) {
        return LotFullDto.builder()
                .id(lot.getId())
                .postId(lot.getPost().getId())
                .currentPrice(lot.getCurrentPrice())
                .minPrice(lot.getMinPrice())
                .numberOfBids(lot.getApplications().size())
                .auctionId(lot.getAuction().getId())
                .post(toPostDto(lot.getPost()))
                .auction(toAuctionDto(lot.getAuction()))
                .creator(toUserDto(lot.getCreator()))
                .build();
    }

    public Application toApplication(ApplicationDto applicationDto) {
        return Application.builder()
                .price(applicationDto.getPrice())
                .build();
    }

    public ApplicationFullDto toApplicationFullDto(Application application) {
        return ApplicationFullDto.builder()
                .applicant(toUserDto(application.getApplicant()))
                .lot(toLotFullDto(application.getLot()))
                .price(application.getPrice())
                .status(application.getStatus())
                .id(application.getId())
                .build();
    }

    public SaleFullDto toSaleFullDto(Sale sale) {
        return SaleFullDto.builder()
                .buyer(toUserDto(sale.getBuyer()))
                .seller(toUserDto(sale.getSeller()))
                .createdTime(sale.getCreatedTime())
                .modifiedTime(sale.getModifiedTime())
                .price(sale.getPrice())
                .post(toPostShortDto(sale.getPost()))
                .status(ESaleStatus.getString(sale.getStatus()))
                .id(sale.getId())
                .build();
    }
}