package nu.senior_project.dormitory_marketplace.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import nu.senior_project.dormitory_marketplace.dto.GeneralPageableResponse;
import nu.senior_project.dormitory_marketplace.dto.lot.LotDto;
import nu.senior_project.dormitory_marketplace.dto.lot.LotFullDto;
import nu.senior_project.dormitory_marketplace.service.LotService;
import org.springframework.data.relational.core.sql.In;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/lot")
@CrossOrigin
public class LotController {
    private final LotService lotService;

    @PostMapping
    @PreAuthorize("@permissionCheck.checkPostOwnership(authentication, #lotDto.getPostId())")
    public void create(@RequestBody LotDto lotDto,
                        HttpServletRequest request) {
        lotService.create(lotDto, request);
    }

    @PutMapping("/update-price/{id}")
    @PreAuthorize("@permissionCheck.checkLotOwnership(authentication, #id)")
    public void updatePrice(@PathVariable Long id,
                             @RequestParam Integer price) {
        lotService.updatePrice(id, price);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@permissionCheck.checkLotOwnership(authentication, #id)")
    public void delete(@PathVariable Long id) {
        lotService.delete(id);
    }

    @GetMapping("/{id}")
    public LotFullDto get(@PathVariable Long id) {
        return lotService.get(id);
    }

    @GetMapping("/list")
    public GeneralPageableResponse<LotFullDto> getLots(HttpServletRequest request,
                                                       @RequestParam Integer limit,
                                                       @RequestParam Integer offset) {
        return lotService.getList(request, limit, offset);
    }

    @PutMapping("/deactivate/{id}")
    @PreAuthorize("@permissionCheck.checkLotOwnership(authentication, #id)")
    public void deactivate(@PathVariable Long id) {
        lotService.deactivate(id);
    }

}
