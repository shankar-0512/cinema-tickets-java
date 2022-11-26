package uk.gov.dwp.uc.pairtest;

import thirdparty.paymentgateway.TicketPaymentServiceImpl;
import thirdparty.seatbooking.SeatReservationServiceImpl;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest.Type;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

public class TicketServiceImpl implements TicketService {
	/**
	 * Should only have private methods other than the one below.
	 */

	@Override
	public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests)
			throws InvalidPurchaseException {

		// Invalid if requests = 0 or requests > 20.
		if (ticketTypeRequests.length == 0 || ticketTypeRequests.length > 19) {
			System.out.println("INVALID NUMBER OF REQUESTS. PLEASE REQUEST TICKETS BETWEEN 0 and 20.");
			throw new InvalidPurchaseException();
		}

		// Invalid if account id is below 1.
		if (accountId <= 0) {
			System.out.println("INVALID ACCOUNT ID.");
			throw new InvalidPurchaseException();
		}

		// Declarations
		int totalPrice = 0;
		int totalSeats = 0;
		boolean adultFlag = false;

		// Looping through each request of the purchaser
		for (TicketTypeRequest request : ticketTypeRequests) {

			/*
			 * Adding total price and setting adult flag as true since child and infant
			 * tickets cannot be purchased without an adult.
			 */
			if (request.getTicketType() == Type.ADULT) {
				totalPrice += 20;
				adultFlag = true;
				totalSeats++;
			}
			// Adding the cost of child ticket to total price.
			else if (request.getTicketType() == Type.CHILD) {
				totalPrice += 10;
				totalSeats++;
			}
		}

		// Invalid if Children and Infants are not accompanied by an Adult
		if (!adultFlag) {
			System.out.println("CHILD AND INFANT TICKETS CANNOT BE BOUGHT WITHOUT AN ADULT.");
			throw new InvalidPurchaseException();
		}

		// Calling Ticket Payment Service
		TicketPaymentServiceImpl pay = new TicketPaymentServiceImpl();
		pay.makePayment(accountId, totalPrice);

		// Calling seat reservation service
		SeatReservationServiceImpl reserve = new SeatReservationServiceImpl();
		reserve.reserveSeat(accountId, totalSeats);
	}

}
