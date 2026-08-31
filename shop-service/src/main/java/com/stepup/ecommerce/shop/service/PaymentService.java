package com.stepup.ecommerce.shop.service;

import com.stepup.ecommerce.shop.client.AmountDto;
import com.stepup.ecommerce.shop.client.WalletClient;
import com.stepup.ecommerce.shop.entity.Order;
import com.stepup.ecommerce.shop.entity.OrderStatus;
import com.stepup.ecommerce.shop.entity.Payment;
import com.stepup.ecommerce.shop.entity.PaymentStatus;
import com.stepup.ecommerce.shop.payment.PaymentRequest;
import com.stepup.ecommerce.shop.payment.PaymentResponse;
import com.stepup.ecommerce.shop.repository.OrderRepository;
import com.stepup.ecommerce.shop.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class PaymentService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final WalletGatewayService walletGatewayService;

    public PaymentService(
            OrderRepository orderRepository,
            PaymentRepository paymentRepository,
            WalletGatewayService walletGatewayService
    ) {
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
        this.walletGatewayService = walletGatewayService;
    }

    @Transactional
    public PaymentResponse pay(Long userId, Long orderId, PaymentRequest request) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("Order does not belong to this user");
        }

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new RuntimeException("Order is not in a payable state");
        }

        if (paymentRepository.findByOrder(order).isPresent()) {
            throw new RuntimeException("Order already has a payment");
        }

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setWalletId(request.walletId());
        payment.setAmount(order.getTotalAmount());
        payment.setPaymentDate(LocalDateTime.now());

        try {
            walletGatewayService.withdraw(order.getTotalAmount());
            payment.setStatus(PaymentStatus.SUCCESS);
            order.setStatus(OrderStatus.PAID);
        } catch (Exception e) {
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
            throw new RuntimeException("Payment failed: wallet withdrawal unsuccessful", e);
        }

        payment = paymentRepository.save(payment);
        orderRepository.save(order);

        return new PaymentResponse(
                payment.getPaymentId(),
                order.getOrderId(),
                payment.getAmount(),
                payment.getStatus().name(),
                payment.getPaymentDate()
        );
    }
}