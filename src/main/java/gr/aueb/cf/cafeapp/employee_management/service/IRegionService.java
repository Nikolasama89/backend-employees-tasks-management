package gr.aueb.cf.cafeapp.employee_management.service;

import gr.aueb.cf.cafeapp.employee_management.model.static_data.Region;

import java.util.List;

public interface IRegionService {
    List<Region> findAllRegions();
}
