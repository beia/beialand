package com.beia_consult_international.solomon;

import com.beia_consult_international.solomon.model.*;
import com.beia_consult_international.solomon.repository.BeaconRepository;
import com.beia_consult_international.solomon.repository.CampaignRepository;
import com.beia_consult_international.solomon.repository.MallRepository;
import com.beia_consult_international.solomon.repository.UserRepository;
import com.beia_consult_international.solomon.service.CampaignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

@SpringBootApplication
public class SolomonApplication implements CommandLineRunner {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MallRepository mallRepository;
    @Autowired
    private CampaignService campaignService;
    @Autowired
    private BeaconRepository beaconRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public static void main(String[] args) {
        SpringApplication.run(SolomonApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        List<User> users = List.of(
          User.builder()
                  .username("admin")
                  .password(passwordEncoder.encode("solomon"))
                  .firstName("Admin")
                  .role(Role.ADMIN)
                  .build(),
          User.builder()
                  .username("McDonald's")
                  .password(passwordEncoder.encode("mc"))
                  .firstName("McDonald's")
                  .role(Role.COMPANY)
                  .build(),
          User.builder()
                  .username("Altex")
                  .password(passwordEncoder.encode("altex"))
                  .firstName("Altex")
                  .role(Role.COMPANY)
                  .build(),
          User.builder()
                  .username("Zara")
                  .password(passwordEncoder.encode("zara"))
                  .firstName("Zara")
                  .role(Role.COMPANY)
                  .build(),
          User.builder()
                  .username("Starbucks")
                  .password(passwordEncoder.encode("starbucks"))
                  .firstName("Starbucks")
                  .role(Role.COMPANY)
                  .build(),
          User.builder()
                  .username("Emag")
                  .password(passwordEncoder.encode("emag"))
                  .firstName("Emag")
                  .role(Role.COMPANY)
                  .build(),
          User.builder()
                  .username("Nike")
                  .password(passwordEncoder.encode("nike"))
                  .firstName("Nike")
                  .role(Role.COMPANY)
                  .build(),
          User.builder()
                  .username("Taco Bell")
                  .password(passwordEncoder.encode("taco bell"))
                  .firstName("Taco Bell")
                  .role(Role.COMPANY)
                  .build(),
          User.builder()
                  .username("Bershka")
                  .password(passwordEncoder.encode("bershka"))
                  .firstName("Bershka")
                  .role(Role.COMPANY)
                  .build());
        userRepository.saveAll(users);


        List<Mall> malls = List.of(
          Mall.builder()
                  .name("Beia Consult International")
                  .latitude(44.3957038)
                  .longitude(26.1027026)
                  .build(),
          Mall.builder()
                  .name("Baneasa Shopping City")
                  .latitude(44.508874)
                  .longitude(26.087669)
                  .build(),
          Mall.builder()
                  .name("Promenada Mall")
                  .latitude(44.478531)
                  .longitude(26.102645)
                  .build(),
          Mall.builder()
                  .name("Veranda Mall")
                  .latitude(44.452251)
                  .longitude(26.130569)
                  .build(),
          Mall.builder()
                  .name("Izvor")
                  .latitude(44.436242)
                  .longitude(26.086765)
                  .build());
        mallRepository.saveAll(malls);


        List<Beacon> beacons = List.of(
          Beacon.builder()
                  .manufacturerId("LKhV")
                  .name("Starbucks")
                  .latitude(44.4364283)
                  .longitude(26.0868782)
                  .major(41302)
                  .minor(22282)
                  .layer(1)
                  .floor(0)
                  .mall(malls.get(4))
                  .user(users.get(4))
                  .manufacturer("KONTAKT")
                  .build(),
          Beacon.builder()
                  .manufacturerId("MTFa")
                  .name("Taco Bell")
                  .latitude(44.395706)
                  .longitude(26.10266)
                  .major(59730)
                  .minor(3532)
                  .layer(1)
                  .floor(0)
                  .mall(malls.get(0))
                  .user(users.get(7))
                  .manufacturer("KONTAKT")
                  .build(),
          Beacon.builder()
                  .manufacturerId("PcNy")
                  .name("Nike")
                  .latitude(44.3957388)
                  .longitude(26.1027663)
                  .major(58450)
                  .minor(19566)
                  .layer(1)
                  .floor(0)
                  .mall(malls.get(0))
                  .user(users.get(6))
                  .manufacturer("KONTAKT")
                  .build(),
          Beacon.builder()
                  .manufacturerId("R4JH")
                  .name("Zara")
                  .latitude(44.4363632)
                  .longitude(26.0868611)
                  .major(24334)
                  .minor(57021)
                  .layer(1)
                  .floor(0)
                  .mall(malls.get(4))
                  .user(users.get(3))
                  .manufacturer("KONTAKT")
                  .build(),
          Beacon.builder()
                  .manufacturerId("rrZd")
                  .name("McDonald's")
                  .latitude(44.4364185)
                  .longitude(26.0869449)
                  .major(39824)
                  .minor(22135)
                  .layer(1)
                  .floor(0)
                  .mall(malls.get(4))
                  .user(users.get(1))
                  .manufacturer("KONTAKT")
                  .build(),
          Beacon.builder()
                  .manufacturerId("tZF7")
                  .name("Emag")
                  .latitude(44.3956626)
                  .longitude(26.1026764)
                  .major(3204)
                  .minor(63655)
                  .layer(1)
                  .floor(0)
                  .mall(malls.get(0))
                  .user(users.get(5))
                  .manufacturer("KONTAKT")
                  .build(),
          Beacon.builder()
                  .manufacturerId("v7mz")
                  .name("Bershka")
                  .latitude(44.3956943)
                  .longitude(26.1027958)
                  .major(25098)
                  .minor(4628)
                  .layer(1)
                  .floor(0)
                  .mall(malls.get(0))
                  .user(users.get(8))
                  .manufacturer("KONTAKT")
                  .build(),
          Beacon.builder()
                  .manufacturerId("zbCe")
                  .name("Altex")
                  .latitude(44.4363572)
                  .longitude(26.0869305)
                  .major(60181)
                  .minor(7706)
                  .layer(1)
                  .floor(0)
                  .mall(malls.get(4))
                  .user(users.get(2))
                  .manufacturer("KONTAKT")
                  .build());
        beaconRepository.saveAll(beacons);

        //BEIA CAMPAIGNS
        List<Campaign> campaignsBeia = List.of(
          Campaign
                  .builder()
                  .title("Adidasi nike")
                  .description("pret: 500 lei")
                  .category(Category.Shoes)
                  .startDate(LocalDateTime.now().minusDays(1))
                  .endDate(LocalDateTime.now().plusDays(100))
                  .user(users.get(6))
                  .build(),
            Campaign
                    .builder()
                    .title("Iphone 11 pro")
                    .description("pret: 4500 lei")
                    .category(Category.Smartphones)
                    .startDate(LocalDateTime.now().minusDays(10))
                    .endDate(LocalDateTime.now().plusDays(50))
                    .user(users.get(5))
                    .build(),
            Campaign
                    .builder()
                    .title("Taco bell oferta 1")
                    .description("pret: 35 lei")
                    .category(Category.Food)
                    .startDate(LocalDateTime.now().minusDays(5))
                    .endDate(LocalDateTime.now().plusDays(30))
                    .user(users.get(7))
                    .build(),
            Campaign
                    .builder()
                    .title("SmartTv Samsung")
                    .description("pret: 3000 lei")
                    .category(Category.Electronics)
                    .startDate(LocalDateTime.now().minusDays(5))
                    .endDate(LocalDateTime.now().plusDays(30))
                    .user(users.get(5))
                    .build(),
            Campaign
                    .builder()
                    .title("Tricou dama")
                    .description("pret: 60 lei")
                    .category(Category.Clothes)
                    .startDate(LocalDateTime.now().minusDays(5))
                    .endDate(LocalDateTime.now().plusDays(30))
                    .user(users.get(8))
                    .build(),
            Campaign
                    .builder()
                    .title("Sapca Nike")
                    .description("pret: 150 lei")
                    .category(Category.Clothes)
                    .startDate(LocalDateTime.now().minusDays(5))
                    .endDate(LocalDateTime.now().plusDays(30))
                    .user(users.get(6))
                    .build()
        );

        //IZVOR CAMPAIGNS
        List<Campaign> campaignsIzvor = List.of(
                Campaign
                        .builder()
                        .title("Adidasi nike")
                        .description("pret: 500 lei")
                        .category(Category.Shoes)
                        .startDate(LocalDateTime.now().minusDays(1))
                        .endDate(LocalDateTime.now().plusDays(100))
                        .user(users.get(4))
                        .build(),
                Campaign
                        .builder()
                        .title("Iphone 11 pro")
                        .description("pret: 4500 lei")
                        .category(Category.Smartphones)
                        .startDate(LocalDateTime.now().minusDays(10))
                        .endDate(LocalDateTime.now().plusDays(50))
                        .user(users.get(2))
                        .build(),
                Campaign
                        .builder()
                        .title("MC oferta test")
                        .description("pret: 35 lei")
                        .category(Category.Food)
                        .startDate(LocalDateTime.now().minusDays(5))
                        .endDate(LocalDateTime.now().plusDays(30))
                        .user(users.get(1))
                        .build(),
                Campaign
                        .builder()
                        .title("SmartTv Samsung")
                        .description("pret: 3000 lei")
                        .category(Category.Electronics)
                        .startDate(LocalDateTime.now().minusDays(5))
                        .endDate(LocalDateTime.now().plusDays(30))
                        .user(users.get(2))
                        .build(),
                Campaign
                        .builder()
                        .title("Tricou dama")
                        .description("pret: 60 lei")
                        .category(Category.Clothes)
                        .startDate(LocalDateTime.now().minusDays(5))
                        .endDate(LocalDateTime.now().plusDays(30))
                        .user(users.get(3))
                        .build(),
                Campaign
                        .builder()
                        .title("Sapca Nike")
                        .description("pret: 150 lei")
                        .category(Category.Clothes)
                        .startDate(LocalDateTime.now().minusDays(5))
                        .endDate(LocalDateTime.now().plusDays(30))
                        .user(users.get(3))
                        .build()
        );

        campaignService.saveAll(campaignsBeia);
        campaignService.saveAll(campaignsIzvor);
    }
}
