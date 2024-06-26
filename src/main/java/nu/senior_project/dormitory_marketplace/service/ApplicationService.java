package nu.senior_project.dormitory_marketplace.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import nu.senior_project.dormitory_marketplace.dto.GeneralPageableResponse;
import nu.senior_project.dormitory_marketplace.dto.application.ApplicationDto;
import nu.senior_project.dormitory_marketplace.dto.application.ApplicationFullDto;
import nu.senior_project.dormitory_marketplace.entity.Application;
import nu.senior_project.dormitory_marketplace.entity.Lot;
import nu.senior_project.dormitory_marketplace.enums.EApplicationStatus;
import nu.senior_project.dormitory_marketplace.enums.ELotStatus;
import nu.senior_project.dormitory_marketplace.exception.auction.AuctionException;
import nu.senior_project.dormitory_marketplace.repository.ApplicationRepository;
import nu.senior_project.dormitory_marketplace.repository.LotRepository;
import nu.senior_project.dormitory_marketplace.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationService {
    private final ApplicationRepository applicationRepository;
    private final EntityConverterService entityConverterService;
    private final LotRepository lotRepository;
    private final UserRepository userRepository;

    @Transactional
    public synchronized void create(ApplicationDto applicationDto, HttpServletRequest request) {
        Lot lot = lotRepository.findByIdOrThrow(applicationDto.getLotId());

        checkApplicationConstraints(applicationDto, lot);

        Application application = entityConverterService.toApplication(applicationDto);
        Application previousWinner = applicationRepository.findByLotAndStatus(lot, EApplicationStatus.WINNING);

        if (previousWinner != null) {
            previousWinner.setStatus(EApplicationStatus.OUTBID);
            applicationRepository.save(previousWinner);
        }

        application.setLot(lot);
        application.setApplicant(userRepository.findByUsernameOrThrow(request.getUserPrincipal().getName()));
        application.setStatus(EApplicationStatus.WINNING);
        applicationRepository.save(application);

        lot.setCurrentPrice(application.getPrice());
        lotRepository.save(lot);
    }

    private void checkApplicationConstraints(ApplicationDto applicationDto, Lot lot) {
        if (!lot.getStatus().equals(ELotStatus.ACTIVE))
            throw new AuctionException("You cannot apply to lot which is not active");

        if (lot.getCurrentPrice() != null && lot.getCurrentPrice() >= applicationDto.getPrice())
            throw new AuctionException("You cannot offer a lower price than ");

        if (lot.getMinPrice() > applicationDto.getPrice())
            throw new AuctionException("You cannot offer a lower price than minimal one");

    }

    public ApplicationFullDto get(Long id) {
        return entityConverterService.toApplicationFullDto(applicationRepository.findByIdOrThrow(id));
    }

    public GeneralPageableResponse<ApplicationFullDto> getList(HttpServletRequest request, Integer limit, Integer offset) {
        String username = request.getUserPrincipal().getName();
        List<ApplicationFullDto> totalResults = applicationRepository
                .findByApplicant_UsernameOrderByIdDesc(username)
                .stream()
                .map(entityConverterService::toApplicationFullDto)
                .toList();

        return new GeneralPageableResponse<>(totalResults, limit, offset);
    }
}
