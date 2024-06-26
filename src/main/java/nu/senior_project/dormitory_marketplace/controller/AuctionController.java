package nu.senior_project.dormitory_marketplace.controller;

import lombok.RequiredArgsConstructor;
import nu.senior_project.dormitory_marketplace.dto.GeneralPageableResponse;
import nu.senior_project.dormitory_marketplace.dto.auction.AuctionDto;
import nu.senior_project.dormitory_marketplace.dto.lot.LotFullDto;
import nu.senior_project.dormitory_marketplace.service.AuctionService;
import org.springframework.data.relational.core.sql.In;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auction")
@CrossOrigin
public class AuctionController {

    private final AuctionService auctionService;

    @PostMapping
    @PreAuthorize("@permissionCheck.checkSuperUser(authentication)")
    public void create(@RequestBody AuctionDto auctionDto) {
        auctionService.create(auctionDto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("@permissionCheck.checkSuperUser(authentication)")
    public void update(@PathVariable Long id,
                        @RequestBody AuctionDto auctionDto) {
        auctionService.update(id, auctionDto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@permissionCheck.checkSuperUser(authentication)")
    public void delete(@PathVariable Long id) {
        auctionService.delete(id);
    }

    @GetMapping("/{id}")
    @PreAuthorize("@permissionCheck.checkSuperUserOrAuctionActive(authentication, #id)")
    public AuctionDto get(@PathVariable Long id) {
        return auctionService.get(id);
    }

    @GetMapping("/list")
    @PreAuthorize("@permissionCheck.checkSuperUser(authentication)")
    public GeneralPageableResponse<AuctionDto> getList(@RequestParam Integer limit,
                                                       @RequestParam Integer offset) {
        return auctionService.getList(limit, offset);
    }

    @GetMapping("/apply-list")
    public List<AuctionDto> getActiveAuctions() {
        return auctionService.getActiveAuctions();
    }

    @GetMapping("/{id}/lots")
    @PreAuthorize("@permissionCheck.checkSuperUserOrAuctionActive(authentication, #id)")
    public GeneralPageableResponse<LotFullDto> getLots(@PathVariable Long id,
                                    @RequestParam Integer limit,
                                    @RequestParam Integer offset) {
        return auctionService.getLots(id, limit, offset);
    }

    @GetMapping("/{id}/initiate")
    @PreAuthorize("@permissionCheck.checkSuperUser(authentication)")
    public void initiateAuction(@PathVariable Long id) {
        auctionService.initiateAuction(id);
    }

    @GetMapping("/get-active")
    public AuctionDto getActiveAction() {
        return auctionService.getActiveAuction();
    }

}
