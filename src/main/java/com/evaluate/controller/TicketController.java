package com.evaluate.controller;

import com.evaluate.common.constants.Constants;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Constants.Controller.BASE_URL)
    public class TicketController {

   // @Autowired
  //  TicketService ticketService;

//    @PostMapping(Constants.Controller.BOOK_TICKET)
//    @ValidateRequest
//    public ResponseEntity<Response> evaluateExpression(@RequestBody BookingRequestDto bookingRequestDto) {
//        try {
//            return APIResponse.renderSuccess(ticketService.bookTicket(bookingRequestDto), 200, HttpStatus.OK);
//        }catch(ObjectOptimisticLockingFailureException e){
//            throw new InvalidDataException("seats are not available");
//        }
//    }
    }
