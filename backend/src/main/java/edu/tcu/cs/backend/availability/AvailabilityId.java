package edu.tcu.cs.backend.availability;

import java.io.Serializable;
import java.util.Objects;

public class AvailabilityId implements Serializable {
    private Integer userId;
    private Integer gameId;

    public AvailabilityId() {}

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof AvailabilityId availabilityId)) return false;
        return Objects.equals(userId, availabilityId.userId) &&
                Objects.equals(gameId, availabilityId.gameId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, gameId);
    }


    /*
    * @IdClass
    * - You
    * */
}
