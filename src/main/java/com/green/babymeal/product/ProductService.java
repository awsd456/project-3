package com.green.babymeal.product;

import com.green.babymeal.cate.model.CateSelVo;
import com.green.babymeal.common.entity.*;
import com.green.babymeal.common.repository.*;
import com.green.babymeal.product.model.ProductReviewDto;
import com.green.babymeal.product.model.ProductSelDto;
import com.green.babymeal.product.model.ProductVolumeDto;
import com.green.babymeal.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProductService {


    private final Long USERPk = 1L; // 테스트용 임시 유저 pk
    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductAllergyRepository ProductAllergyRepository;

    @Autowired
    private ProductCategoryRelationRepository productCategoryRelationRepository;

    @Autowired
    private ProductThumbnailRepository productThumbnailRepository;

    @Autowired
    private UserRepository userRepository;

    public int postReview(ProductReviewDto dto) {
        ProductEntity product = new ProductEntity();
        product.setProductId(dto.getProductId());
        UserEntity user = new UserEntity();
        user.setIuser(USERPk);

        // Review 생성
        ReviewEntity review = new ReviewEntity();
        review.setCtnt(dto.getCtnt());
        review.setProductId(product); // Product 객체 참조 설정
        review.setIuser(user); // User 객체 참조 설정
        reviewRepository.save(review);
        return 1;
    }

    public List<ReviewEntity> getReviewById(Long productId) {
        ProductEntity entity = new ProductEntity();
        entity.setProductId(productId);
        return reviewRepository.findAllByProductId(entity);
    }

    public List<ProductVolumeDto> selProductVolumeYearMonth(int year, int month) {
        return productRepository.findSaleVolume(year, month);
    }

    public ProductSelDto selProduct(Long productId) {
        // 상품 ID로부터 상품 정보를 조회
        ProductEntity productEntity = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지않는상품입니다 : " + productId)); // 예외처리
        // 상품과 관련된 알러지 정보 조회
        List<ProductAllergyEntity> productAllergies = ProductAllergyRepository.findByProductId_ProductId(productId);
        // 알러지 정보를 문자열 리스트로 변환
        List<String> allergyName = productAllergies.stream()
                .map(productAllergyEntity -> getAllergyName(productAllergyEntity.getAllergyId()))
                .collect(Collectors.toList());
        // 상품 단계 조회
        Long cateIdLong = 0L;
        ProductCateRelationEntity temp = new ProductCateRelationEntity();
        log.info("단계 : {}", cateIdLong);
        cateIdLong = productCategoryRelationRepository.findCateIdByProductId(productId);

        // 썸네일 URL 목록을 생성
        List<ProductThumbnailEntity> thumbnailEntities = productThumbnailRepository.findByProductId(productEntity);
        List<String> thumbnailList = new ArrayList<>();

        // 최대 4개까지 썸네일을 추가
        int maxThumbnails = 4;
        for (ProductThumbnailEntity thumbnailEntity : thumbnailEntities) {
            if (thumbnailList.size() < maxThumbnails) {
                thumbnailList.add(thumbnailEntity.getImg());
            } else {
                break; // 최대 개수에 도달하면 반복문을 종료
                // 썸네일 최대 4개만 출력하도록 고정
            }
        }

// 배송예정일 계산
        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();
        LocalTime afternoonOne = LocalTime.of(13, 0); // 오후 1시

// 주말 확인
        boolean isSaturday = currentDate.getDayOfWeek() == DayOfWeek.SATURDAY;
        boolean isSunday = currentDate.getDayOfWeek() == DayOfWeek.SUNDAY;

// 배송일 계산
        LocalDate estimatedDeliveryDate;

        if (isSaturday) {
            // 토요일인 경우 배송일 + 3
            estimatedDeliveryDate = currentDate.plusDays(2);
        } else if (isSunday || (currentTime.isAfter(afternoonOne) && !isSaturday)) {
            // 일요일이거나 현재 시각이 오후 1시 이후인 경우 배송일 + 2
            estimatedDeliveryDate = currentDate.plusDays(1);
        } else {
            // 나머지 경우에는 배송일 +1
            estimatedDeliveryDate = currentDate.plusDays(1);
        }

        // 조회된 상품 정보와 알러지 정보를 매핑하여 ProductSelDto 객체 생성
        ProductSelDto productDataDto = new ProductSelDto();
        productDataDto.setPName("[" + cateIdLong + "단계]" + productEntity.getPName());
        productDataDto.setCateId(cateIdLong);
        productDataDto.setDescription(productEntity.getDescription());
        productDataDto.setPPrice(productEntity.getPPrice());
        productDataDto.setPQuantity(productEntity.getPQuantity());
        productDataDto.setSaleVoumn(productEntity.getSaleVolume());
        productDataDto.setAllergyNames(allergyName);
        productDataDto.setDescription(productDataDto.getDescription());
        productDataDto.setThumbnail(thumbnailList);
        productDataDto.setDeliveryDate("예상 배송일은 "+estimatedDeliveryDate+"입니다.");
        return productDataDto;
    }

    private String getAllergyName(AllergyEntity allergyEntity) {
        // 알러지id로 알러지 종류(이름) 매칭
        if (allergyEntity == null) {
            return null;
        }
        return allergyEntity.getAllergyName();
    }

//    // 카테고리 추출
//    public Long getProductCategoryIdById(Long productId) {
//        List<CateSelVo> by = cateRepository.findBy(cateSelList.getCateId(), cateSelList.getCateDetailId());
//        if (null != cateSelList.getCateDetailId()) {
//            for (int i = 0; i < by.size(); i++) {
//                ProductCateRelationEntity productCateRelationEntities = productCategoryRelationRepository.find(by.get(i).getProductId());
//
//                by.get(i).setName("[" + productCateRelationEntities.getCategoryEntity().getCateId() + "단계]" + by.get(i).getName());
//                by.get(i).setThumbnail("/img/product/" + by.get(i).getProductId() + "/" + by.get(i).getThumbnail());
//            }
//    }
//

}
