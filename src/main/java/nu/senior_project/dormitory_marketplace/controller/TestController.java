package nu.senior_project.dormitory_marketplace.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@CrossOrigin
public class TestController {

    @GetMapping("/alive")
    private String alive() {
        return "ok";
    }

    @GetMapping("/customer")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    private String customer() {
        return "Customer page";
    }

    @GetMapping("/store")
    @PreAuthorize("hasAuthority('STORE')")
    private String store() {
        return "Store page";
    }

    @GetMapping("/super-admin")
    @PreAuthorize("hasAuthority('SUPERADMIN')")
    private String superAdmin() {
        return "Super Admin page";
    }
}
