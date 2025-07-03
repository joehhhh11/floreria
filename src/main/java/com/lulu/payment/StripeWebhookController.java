package com.lulu.payment;

import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.lulu.orders.model.OrderModel;
import com.lulu.orders.repository.OrderRepository;
import lombok.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/payment")
public class StripeWebhookController {


}
