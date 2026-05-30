package com.example.tripsplit.controller;

import com.example.tripsplit.converter.ExpenseToExpenseDtoConverter;
import com.example.tripsplit.dto.*;
import com.example.tripsplit.entity.Trip;
import com.example.tripsplit.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Controller
@RequestMapping("/trips")
public class TripController {
    private final TripService tripService;
    private final ExpenseService expenseService;
    private final CommentService commentService;
    private final ExpenseToExpenseDtoConverter expenseConverter;

    @GetMapping
    public String tripsPage(@RequestParam(required = false) String destination, Model model, Authentication authentication) {
        if (destination != null && !destination.isBlank()) {
            model.addAttribute("trips", tripService.searchTrips(destination, authentication));
        } else {
            model.addAttribute("trips", tripService.findCurrentUserTrips(authentication));
        }

        model.addAttribute("destination", destination);
        model.addAttribute("tripRequest", new TripRequest());
        return "trips/list";
    }

    @PostMapping
    public String createTrip(@Valid @ModelAttribute("tripRequest") TripRequest tripRequest, BindingResult bindingResult, Model model, Authentication authentication) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("trips", tripService.findCurrentUserTrips(authentication));
            return "trips/list";
        }

        try {
            tripService.createTrip(tripRequest, authentication);
        } catch (IllegalArgumentException exception) {
            bindingResult.rejectValue("endDate", "error.endDate", exception.getMessage());
            model.addAttribute("trips", tripService.findCurrentUserTrips(authentication));
            return "trips/list";
        }

        return "redirect:/trips";
    }

    @GetMapping("/{id}")
    public String tripDetails(@PathVariable Long id, Model model, Authentication authentication) {
        addDetailsAttributes(id, model, authentication, new ExpenseRequest(), new ParticipantRequest(), new CommentRequest());
        return "trips/details";
    }

    @PostMapping("/{id}/expenses")
    public String addExpense(@PathVariable Long id, @Valid @ModelAttribute("expenseRequest") ExpenseRequest expenseRequest, BindingResult bindingResult, Model model, Authentication authentication) {
        if (bindingResult.hasErrors()) {
            addDetailsAttributes(id, model, authentication, expenseRequest, new ParticipantRequest(), new CommentRequest());
            return "trips/details";
        }

        tripService.getTripForCurrentUser(id, authentication);
        expenseService.addExpense(id, expenseRequest, authentication);
        return "redirect:/trips/" + id;
    }

    @PostMapping("/{tripId}/expenses/{expenseId}/delete")
    public String deleteExpense(@PathVariable Long tripId, @PathVariable Long expenseId, Authentication authentication) {
        expenseService.deleteExpense(expenseId, authentication);
        return "redirect:/trips/" + tripId;
    }

    @PostMapping("/{id}/participants")
    public String addParticipant(@PathVariable Long id, @Valid @ModelAttribute("participantRequest") ParticipantRequest participantRequest, BindingResult bindingResult, Model model, Authentication authentication) {
        tripService.checkOwner(id, authentication);

        if (bindingResult.hasErrors()) {
            addDetailsAttributes(id, model, authentication, new ExpenseRequest(), participantRequest, new CommentRequest());
            return "trips/details";
        }

        try {
            tripService.addParticipant(id, participantRequest.getEmail());
        } catch (IllegalArgumentException exception) {
            bindingResult.rejectValue("email", "error.email", exception.getMessage());
            addDetailsAttributes(id, model, authentication, new ExpenseRequest(), participantRequest, new CommentRequest());
            return "trips/details";
        }

        return "redirect:/trips/" + id;
    }

    @PostMapping("/{id}/comments")
    public String addComment(@PathVariable Long id, @Valid @ModelAttribute("commentRequest") CommentRequest commentRequest, BindingResult bindingResult, Model model, Authentication authentication) {
        if (bindingResult.hasErrors()) {
            addDetailsAttributes(id, model, authentication, new ExpenseRequest(), new ParticipantRequest(), commentRequest);
            return "trips/details";
        }

        tripService.getTripForCurrentUser(id, authentication);
        commentService.addComment(id, commentRequest, authentication);
        return "redirect:/trips/" + id;
    }

    @GetMapping("/{id}/edit")
    public String editTripPage(@PathVariable Long id, Model model, Authentication authentication) {
        tripService.checkOwner(id, authentication);
        Trip trip = tripService.getTripForCurrentUser(id, authentication);

        TripRequest tripRequest = new TripRequest();
        tripRequest.setTitle(trip.getTitle());
        tripRequest.setDestination(trip.getDestination());
        tripRequest.setStartDate(trip.getStartDate());
        tripRequest.setEndDate(trip.getEndDate());
        tripRequest.setDescription(trip.getDescription());
        tripRequest.setImageUrl(trip.getImageUrl());

        model.addAttribute("trip", trip);
        model.addAttribute("tripRequest", tripRequest);
        return "trips/edit";
    }

    @PostMapping("/{id}/edit")
    public String updateTrip(@PathVariable Long id, @Valid @ModelAttribute("tripRequest") TripRequest tripRequest, BindingResult bindingResult, Model model, Authentication authentication) {
        tripService.checkOwner(id, authentication);

        if (bindingResult.hasErrors()) {
            model.addAttribute("trip", tripService.getTripForCurrentUser(id, authentication));
            return "trips/edit";
        }

        try {
            tripService.updateTrip(id, tripRequest);
        } catch (IllegalArgumentException exception) {
            bindingResult.rejectValue("endDate", "error.endDate", exception.getMessage());
            model.addAttribute("trip", tripService.getTripForCurrentUser(id, authentication));
            return "trips/edit";
        }

        return "redirect:/trips/" + id;
    }

    @PostMapping("/{id}/delete")
    public String deleteTrip(@PathVariable Long id, Authentication authentication) {
        tripService.checkOwner(id, authentication);
        tripService.deleteTrip(id);
        return "redirect:/trips";
    }

    private void addDetailsAttributes(Long id, Model model, Authentication authentication, ExpenseRequest expenseRequest, ParticipantRequest participantRequest, CommentRequest commentRequest) {
        model.addAttribute("trip", tripService.getTripForCurrentUser(id, authentication));
        model.addAttribute("isOwner", tripService.isOwner(id, authentication));
        model.addAttribute("expenses", expenseService.findByTrip(id).stream().map(expenseConverter::convert).toList());
        model.addAttribute("total", expenseService.calculateTotal(id));
        model.addAttribute("expenseRequest", expenseRequest);
        model.addAttribute("participantRequest", participantRequest);
        model.addAttribute("participants", tripService.getParticipants(id));
        model.addAttribute("debts", expenseService.calculateDebts(id));
        model.addAttribute("comments", commentService.findByTrip(id));
        model.addAttribute("commentRequest", commentRequest);
    }
}
