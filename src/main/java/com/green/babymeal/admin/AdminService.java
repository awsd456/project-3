package com.green.babymeal.admin;

import com.green.babymeal.admin.model.OrderDetailVo;
import com.green.babymeal.admin.model.OrderlistRes;
import com.green.babymeal.admin.model.ProductVo;
import com.green.babymeal.admin.model.UserVo;
import com.green.babymeal.common.entity.OrderDetailEntity;
import com.green.babymeal.common.entity.OrderlistEntity;
import com.green.babymeal.common.repository.OrderDetailRepository;
import com.green.babymeal.common.repository.OrderlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    @Autowired
    private OrderlistRepository orderlistRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;


    public Page<OrderlistRes> allOrder(LocalDate startDate, LocalDate endDate,
                                       String filter1, String filter2, String filter3, String filter4,
                                       Pageable pageable) {
        // "필터1 : 검색어  필터2 : 주문번호  필터3 : 상품번호   필터4 : 주문상태"
        // 기간 내 주문리스트를 가져온다
        Page<OrderlistEntity> outputOrderlist = orderlistRepository.findByCreatedAtBetween(startDate, endDate, pageable);
        // 주문정보 담을 객체 생성
        List<OrderlistRes> resultList = new ArrayList<>();

        for (OrderlistEntity order : outputOrderlist.getContent()) {
            List<OrderDetailEntity> orderDetails = orderDetailRepository.findByOrderId_OrderCode(order.getOrderCode());

            UserVo userVoData = UserVo.builder()
                    .name(order.getIuser().getName())
                    .iuser(order.getIuser().getIuser())
                    .build();

            List<OrderDetailVo> orderDetailVoList = new ArrayList<>();
            if (!orderDetails.isEmpty()) {
                orderDetailVoList = orderDetails.stream()
                        .map(detail -> OrderDetailVo.builder()
                                .orderDetailId(detail.getOrderDetailId())
                                .productId(detail.getProductId().getProductId())
                                .count(detail.getCount())
                                .totalPrice(detail.getTotalPrice())
                                .build())
                        .collect(Collectors.toList());
            }

            if (!orderDetails.isEmpty()) {
                OrderlistRes orderlistRes = OrderlistRes.builder()
                        .orderId(order.getOrderId())
                        .ordercode(order.getOrderCode())
                        .userVo(userVoData)
                        .payment(order.getPayment())
                        .shipment(order.getShipment())
                        .cancel(order.getCancel())
                        .phoneNm(order.getPhoneNm())
                        .request(order.getRequest())
                        .reciever(order.getReciever())
                        .address(order.getAddress())
                        .addressDetail(order.getAddressDetail())
                        .delYn(order.getDelYn())
                        .usepoint(order.getUsepoint())
                        .orderDetailVo(orderDetailVoList)
                        .build();
                resultList.add(orderlistRes); // 기본 데이터 조회 완료
            }

            if (filter2 != null) {
                resultList.removeIf(orderlistRes -> !orderlistRes.getOrdercode().equals(Long.parseLong(filter2)));
            }
        }
        return new PageImpl<>(resultList, outputOrderlist.getPageable(), outputOrderlist.getTotalElements());
    }
}




//        // 필터2 : 주문번호 기준 필터링
//        if (filter2 != null) {
//            resultList.removeIf(orderRes -> !orderRes.getOrdercode().equals(Long.parseLong(filter2)));
//        }
//
//        if (filter3 != null) {
//            resultList.removeIf(orderRes -> {
//                for (OrderDetailEntity orderDetail : orderRes.getOrderDetails()) {
//                    if (!orderDetail.getProductId().getProductId().equals(Long.parseLong(filter3))) {
//                        return true; // 상품번호가 일치하지 않으면 해당 주문 정보를 리스트에서 제거
//                    }
//                }
//                return false;
//            });
//        }
//
//        if (filter4 != null) {
//            resultList.removeIf(orderRes -> !orderRes.getPayment().equals(Long.parseLong(filter4)));
//        }

//        // Page 객체로 변환
//        Page<OrderlistRes> resultPage = new PageImpl<>(resultList, pageable, resultList.size());
//
//        return null;
//    }


//        if (filter1 != null) {
//            //검색어
//            filterOrderlist = applyFilter1(filterOrderlist, filter1);
//        }


//        if (filter2 != null) {
//            // 주문번호 필터 적용
//            Long orderCode = Long.parseLong(filter2);
//            List<OrderDetailEntity> filteredByOrderCode = orderDetailRepository.findByOrderId_OrderCode(orderCode);
//            filterOrderlist.removeIf(order -> !order.getOrdercode().equals(orderCode));
//        }
//
//        public List<OrderlistEntity> selOrder(Long orderCode){
//            return orderlistRepository.findOrderById(orderCode);
//        }
//}
//
//