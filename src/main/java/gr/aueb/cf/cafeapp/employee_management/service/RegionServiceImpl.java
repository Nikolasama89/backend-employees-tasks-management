package gr.aueb.cf.cafeapp.employee_management.service;

import gr.aueb.cf.cafeapp.employee_management.model.static_data.Region;
import gr.aueb.cf.cafeapp.employee_management.repository.RegionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RegionServiceImpl implements IRegionService{

    private final RegionRepository regionRepository;

    @Autowired
    public RegionServiceImpl(RegionRepository regionRepository) {
        this.regionRepository = regionRepository;
    }

    @Override
    public List<Region> findAllRegions() {
        return regionRepository.findAll();
    }
}
