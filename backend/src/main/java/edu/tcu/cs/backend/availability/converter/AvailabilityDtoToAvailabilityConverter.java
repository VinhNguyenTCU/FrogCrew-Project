package edu.tcu.cs.backend.availability.converter;

import edu.tcu.cs.backend.availability.Availability;
import edu.tcu.cs.backend.availability.dto.AvailabilityDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class AvailabilityDtoToAvailabilityConverter implements Converter<AvailabilityDto, Availability> {
    @Override
    public Availability convert(AvailabilityDto source) {
        Availability memberAvailability = new Availability();
        memberAvailability.setAvailability(source.availability());
        memberAvailability.setComment(source.comment());
        return memberAvailability;
    }
}
