package com.green.babymeal.baby;

import com.green.babymeal.baby.model.*;
import com.green.babymeal.common.entity.AllergyEntity;
import com.green.babymeal.common.entity.UserBabyalleEntity;
import com.green.babymeal.common.entity.UserBabyinfoEntity;
import com.green.babymeal.common.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BabyService {

    private final BabyMapper mapper;
    private final BabyRepository rep;
    private final BabyAlleRepository repository;

    public BabyInsVo insBaby(BabyInsDto dto){


           UserBabyinfoEntity entity = new UserBabyinfoEntity();
           UserEntity userEntity = new UserEntity();
           entity.setBirthday(dto.getBirthday());
           entity.setPrefer(dto.getPrefer());
           userEntity.setIuser(dto.getIuser());
           entity.setUserEntity(userEntity);

//        if(rep.findById(dto.getIuser()) == null){
            rep.save(entity);
//        }

            UserBabyalleEntity userBabyalleEntity = new UserBabyalleEntity();
            userBabyalleEntity.setUserBabyinfoEntity(entity);
            AllergyEntity allergyEntity = new AllergyEntity();
            allergyEntity.setAllergyId(dto.getAllegyId());
            userBabyalleEntity.setAllergyEntity(allergyEntity);
            repository.save(userBabyalleEntity);


        return BabyInsVo.builder()
                .birthday(entity.getBirthday())
                .prefer(entity.getPrefer())
                .iuser(userEntity.getIuser())
                //.babyId(userBabyalleEntity.getBabyallergy())
                .build();
    }


    public void delete(Long babyId){
//        repository.deleteById();
        rep.deleteById(babyId);
    }



    public List sel(Long iuser){
        List<BaByInfoVo> baByInfoVos = mapper.selBaby(iuser);
        List list=new ArrayList();
        for (int i = 0; i < baByInfoVos.size(); i++) {
            BabyAllergyTotalVo vo=new BabyAllergyTotalVo();
            BaByInfoVo baByInfoVo = baByInfoVos.get(i);
            Long babyId = baByInfoVos.get(i).getBabyId();
            List<BabyAllergyInfoVo> babyAllergyInfoVos = mapper.selBabyAllergy(babyId);
            vo.setBaByInfoVo(baByInfoVo);
            vo.setBabyAllergyList(babyAllergyInfoVos);
            list.add(vo);
        }
        return list;

    }
}
