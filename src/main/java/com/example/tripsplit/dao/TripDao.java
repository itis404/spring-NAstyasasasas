package com.example.tripsplit.dao;

import com.example.tripsplit.entity.Trip;
import com.example.tripsplit.entity.TripParticipant;
import com.example.tripsplit.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TripDao {

    @PersistenceContext
    private EntityManager entityManager;

    public List<Trip> searchTrips(String destination, User user) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Trip> query = cb.createQuery(Trip.class);
        Root<Trip> trip = query.from(Trip.class);

        Subquery<Long> subquery = query.subquery(Long.class);
        Root<TripParticipant> participant = subquery.from(TripParticipant.class);

        subquery.select(participant.get("trip").get("id"))
                .where(cb.equal(participant.get("user"), user));

        Predicate destinationPredicate = cb.like(
                cb.lower(trip.get("destination")), "%" + destination.toLowerCase() + "%"
        );

        Predicate ownerPredicate = cb.equal(trip.get("owner"), user);
        Predicate participantPredicate = trip.get("id").in(subquery);

        query.select(trip)
                .where(cb.and(destinationPredicate, cb.or(ownerPredicate, participantPredicate)))
                .distinct(true);

        return entityManager.createQuery(query).getResultList();
    }
}