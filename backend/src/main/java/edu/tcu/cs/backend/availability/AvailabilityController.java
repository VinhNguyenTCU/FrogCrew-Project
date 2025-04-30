package edu.tcu.cs.backend.availability;

import edu.tcu.cs.backend.availability.converter.AvailabilityDtoToAvailabilityConverter;
import edu.tcu.cs.backend.availability.converter.AvailabilityToAvailabilityDtoConverter;
import edu.tcu.cs.backend.availability.dto.AvailabilityDto;
import edu.tcu.cs.backend.system.Result;
import edu.tcu.cs.backend.system.StatusCode;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/availability")
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    private final AvailabilityDtoToAvailabilityConverter availabilityDtoToAvailabilityConverter;

    private final AvailabilityToAvailabilityDtoConverter availabilityToAvailabilityDtoConverter;


    public AvailabilityController(AvailabilityService availabilityService, AvailabilityDtoToAvailabilityConverter availabilityDtoToAvailabilityConverter, AvailabilityToAvailabilityDtoConverter availabilityToAvailabilityDtoConverter) {
        this.availabilityService = availabilityService;
        this.availabilityDtoToAvailabilityConverter = availabilityDtoToAvailabilityConverter;
        this.availabilityToAvailabilityDtoConverter = availabilityToAvailabilityDtoConverter;

    }

    @PostMapping
    public Result addAvailability(@RequestBody AvailabilityDto availabilityDto) {
        Availability availability = this.availabilityDtoToAvailabilityConverter.convert(availabilityDto);
        Availability savedAvailability =this.availabilityService.save(availability);
        AvailabilityDto returnAvailabilityDto = this.availabilityToAvailabilityDtoConverter.convert(savedAvailability);
        return new Result(true, StatusCode.SUCCESS, "Save Success", returnAvailabilityDto);
    }

}
