package edu.tcu.cs.backend.availability.converter;

import edu.tcu.cs.backend.availability.Availability;
import edu.tcu.cs.backend.availability.dto.AvailabilityDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class AvailabilityToAvailabilityDtoConverter implements Converter<Availability, AvailabilityDto> {
    @Override
    public AvailabilityDto convert(Availability source) {
        return new AvailabilityDto(
                source.getUser().getUserId(),
                source.getGame().getGameId(),
                source.getAvailability(),
                source.getComment()
        );
    }
}

