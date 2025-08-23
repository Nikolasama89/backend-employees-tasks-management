package gr.aueb.cf.cafeapp.employee_management.controller;

import gr.aueb.cf.cafeapp.employee_management.model.static_data.Region;
import gr.aueb.cf.cafeapp.employee_management.service.IRegionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/regions")
public class RegionController {

    private final IRegionService regionService;

    public RegionController(IRegionService regionService) {
        this.regionService = regionService;
    }

    @GetMapping
    public List<Region> getAll() {
        return regionService.findAllRegions();
    }
}
